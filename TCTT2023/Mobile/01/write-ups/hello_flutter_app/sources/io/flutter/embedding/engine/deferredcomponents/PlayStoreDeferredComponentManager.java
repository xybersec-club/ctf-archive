package io.flutter.embedding.engine.deferredcomponents;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import androidx.core.os.EnvironmentCompat;
import com.google.android.play.core.splitinstall.SplitInstallException;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import io.flutter.Log;
import io.flutter.embedding.engine.FlutterJNI;
import io.flutter.embedding.engine.loader.ApplicationInfoLoader;
import io.flutter.embedding.engine.loader.FlutterApplicationInfo;
import io.flutter.embedding.engine.systemchannels.DeferredComponentChannel;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
/* loaded from: classes.dex */
public class PlayStoreDeferredComponentManager implements DeferredComponentManager {
    public static final String MAPPING_KEY = DeferredComponentManager.class.getName() + ".loadingUnitMapping";
    private static final String TAG = "PlayStoreDeferredComponentManager";
    private DeferredComponentChannel channel;
    private Context context;
    private FlutterApplicationInfo flutterApplicationInfo;
    private FlutterJNI flutterJNI;
    private FeatureInstallStateUpdatedListener listener;
    protected SparseArray<String> loadingUnitIdToComponentNames;
    protected SparseArray<String> loadingUnitIdToSharedLibraryNames;
    private Map<String, Integer> nameToSessionId;
    private SparseIntArray sessionIdToLoadingUnitId;
    private SparseArray<String> sessionIdToName;
    private SparseArray<String> sessionIdToState;
    private SplitInstallManager splitInstallManager;

    /* loaded from: classes.dex */
    private class FeatureInstallStateUpdatedListener implements SplitInstallStateUpdatedListener {
        private FeatureInstallStateUpdatedListener() {
        }

        public void onStateUpdate(SplitInstallSessionState state) {
            int sessionId = state.sessionId();
            if (PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId) != null) {
                switch (state.status()) {
                    case 1:
                        Log.d(PlayStoreDeferredComponentManager.TAG, String.format("Module \"%s\" (sessionId %d) install pending.", PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId), Integer.valueOf(sessionId)));
                        PlayStoreDeferredComponentManager.this.sessionIdToState.put(sessionId, "pending");
                        return;
                    case 2:
                        Log.d(PlayStoreDeferredComponentManager.TAG, String.format("Module \"%s\" (sessionId %d) downloading.", PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId), Integer.valueOf(sessionId)));
                        PlayStoreDeferredComponentManager.this.sessionIdToState.put(sessionId, "downloading");
                        return;
                    case 3:
                        Log.d(PlayStoreDeferredComponentManager.TAG, String.format("Module \"%s\" (sessionId %d) downloaded.", PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId), Integer.valueOf(sessionId)));
                        PlayStoreDeferredComponentManager.this.sessionIdToState.put(sessionId, "downloaded");
                        return;
                    case 4:
                        Log.d(PlayStoreDeferredComponentManager.TAG, String.format("Module \"%s\" (sessionId %d) installing.", PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId), Integer.valueOf(sessionId)));
                        PlayStoreDeferredComponentManager.this.sessionIdToState.put(sessionId, "installing");
                        return;
                    case 5:
                        Log.d(PlayStoreDeferredComponentManager.TAG, String.format("Module \"%s\" (sessionId %d) install successfully.", PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId), Integer.valueOf(sessionId)));
                        PlayStoreDeferredComponentManager playStoreDeferredComponentManager = PlayStoreDeferredComponentManager.this;
                        playStoreDeferredComponentManager.loadAssets(playStoreDeferredComponentManager.sessionIdToLoadingUnitId.get(sessionId), (String) PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId));
                        if (PlayStoreDeferredComponentManager.this.sessionIdToLoadingUnitId.get(sessionId) > 0) {
                            PlayStoreDeferredComponentManager playStoreDeferredComponentManager2 = PlayStoreDeferredComponentManager.this;
                            playStoreDeferredComponentManager2.loadDartLibrary(playStoreDeferredComponentManager2.sessionIdToLoadingUnitId.get(sessionId), (String) PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId));
                        }
                        if (PlayStoreDeferredComponentManager.this.channel != null) {
                            PlayStoreDeferredComponentManager.this.channel.completeInstallSuccess((String) PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId));
                        }
                        PlayStoreDeferredComponentManager.this.sessionIdToName.delete(sessionId);
                        PlayStoreDeferredComponentManager.this.sessionIdToLoadingUnitId.delete(sessionId);
                        PlayStoreDeferredComponentManager.this.sessionIdToState.put(sessionId, "installed");
                        return;
                    case 6:
                        Log.e(PlayStoreDeferredComponentManager.TAG, String.format("Module \"%s\" (sessionId %d) install failed with: %s", PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId), Integer.valueOf(sessionId), Integer.valueOf(state.errorCode())));
                        PlayStoreDeferredComponentManager.this.flutterJNI.deferredComponentInstallFailure(PlayStoreDeferredComponentManager.this.sessionIdToLoadingUnitId.get(sessionId), "Module install failed with " + state.errorCode(), true);
                        if (PlayStoreDeferredComponentManager.this.channel != null) {
                            PlayStoreDeferredComponentManager.this.channel.completeInstallError((String) PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId), "Android Deferred Component failed to install.");
                        }
                        PlayStoreDeferredComponentManager.this.sessionIdToName.delete(sessionId);
                        PlayStoreDeferredComponentManager.this.sessionIdToLoadingUnitId.delete(sessionId);
                        PlayStoreDeferredComponentManager.this.sessionIdToState.put(sessionId, "failed");
                        return;
                    case 7:
                        Log.d(PlayStoreDeferredComponentManager.TAG, String.format("Module \"%s\" (sessionId %d) install canceled.", PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId), Integer.valueOf(sessionId)));
                        if (PlayStoreDeferredComponentManager.this.channel != null) {
                            PlayStoreDeferredComponentManager.this.channel.completeInstallError((String) PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId), "Android Deferred Component installation canceled.");
                        }
                        PlayStoreDeferredComponentManager.this.sessionIdToName.delete(sessionId);
                        PlayStoreDeferredComponentManager.this.sessionIdToLoadingUnitId.delete(sessionId);
                        PlayStoreDeferredComponentManager.this.sessionIdToState.put(sessionId, "cancelled");
                        return;
                    case 8:
                        Log.d(PlayStoreDeferredComponentManager.TAG, String.format("Module \"%s\" (sessionId %d) install requires user confirmation.", PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId), Integer.valueOf(sessionId)));
                        PlayStoreDeferredComponentManager.this.sessionIdToState.put(sessionId, "requiresUserConfirmation");
                        return;
                    case 9:
                        Log.d(PlayStoreDeferredComponentManager.TAG, String.format("Module \"%s\" (sessionId %d) install canceling.", PlayStoreDeferredComponentManager.this.sessionIdToName.get(sessionId), Integer.valueOf(sessionId)));
                        PlayStoreDeferredComponentManager.this.sessionIdToState.put(sessionId, "canceling");
                        return;
                    default:
                        Log.d(PlayStoreDeferredComponentManager.TAG, "Unknown status: " + state.status());
                        return;
                }
            }
        }
    }

    public PlayStoreDeferredComponentManager(Context context, FlutterJNI flutterJNI) {
        this.context = context;
        this.flutterJNI = flutterJNI;
        this.flutterApplicationInfo = ApplicationInfoLoader.load(context);
        this.splitInstallManager = SplitInstallManagerFactory.create(context);
        FeatureInstallStateUpdatedListener featureInstallStateUpdatedListener = new FeatureInstallStateUpdatedListener();
        this.listener = featureInstallStateUpdatedListener;
        this.splitInstallManager.registerListener(featureInstallStateUpdatedListener);
        this.sessionIdToName = new SparseArray<>();
        this.sessionIdToLoadingUnitId = new SparseIntArray();
        this.sessionIdToState = new SparseArray<>();
        this.nameToSessionId = new HashMap();
        this.loadingUnitIdToComponentNames = new SparseArray<>();
        this.loadingUnitIdToSharedLibraryNames = new SparseArray<>();
        initLoadingUnitMappingToComponentNames();
    }

    @Override // io.flutter.embedding.engine.deferredcomponents.DeferredComponentManager
    public void setJNI(FlutterJNI flutterJNI) {
        this.flutterJNI = flutterJNI;
    }

    private boolean verifyJNI() {
        if (this.flutterJNI == null) {
            Log.e(TAG, "No FlutterJNI provided. `setJNI` must be called on the DeferredComponentManager before attempting to load dart libraries or invoking with platform channels.");
            return false;
        }
        return true;
    }

    @Override // io.flutter.embedding.engine.deferredcomponents.DeferredComponentManager
    public void setDeferredComponentChannel(DeferredComponentChannel channel) {
        this.channel = channel;
    }

    private ApplicationInfo getApplicationInfo() {
        try {
            return this.context.getPackageManager().getApplicationInfo(this.context.getPackageName(), 128);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void initLoadingUnitMappingToComponentNames() {
        Bundle metaData;
        String[] split;
        String str = DeferredComponentManager.class.getName() + ".loadingUnitMapping";
        ApplicationInfo applicationInfo = getApplicationInfo();
        if (applicationInfo != null && (metaData = applicationInfo.metaData) != null) {
            String str2 = MAPPING_KEY;
            String rawMappingString = metaData.getString(str2, null);
            if (rawMappingString == null) {
                Log.e(TAG, "No loading unit to dynamic feature module name found. Ensure '" + str2 + "' is defined in the base module's AndroidManifest.");
            } else if (!rawMappingString.equals("")) {
                for (String entry : rawMappingString.split(",")) {
                    String[] splitEntry = entry.split(":", -1);
                    int loadingUnitId = Integer.parseInt(splitEntry[0]);
                    this.loadingUnitIdToComponentNames.put(loadingUnitId, splitEntry[1]);
                    if (splitEntry.length > 2) {
                        this.loadingUnitIdToSharedLibraryNames.put(loadingUnitId, splitEntry[2]);
                    }
                }
            }
        }
    }

    @Override // io.flutter.embedding.engine.deferredcomponents.DeferredComponentManager
    public void installDeferredComponent(final int loadingUnitId, final String componentName) {
        final String resolvedComponentName = componentName != null ? componentName : this.loadingUnitIdToComponentNames.get(loadingUnitId);
        if (resolvedComponentName == null) {
            Log.e(TAG, "Deferred component name was null and could not be resolved from loading unit id.");
        } else if (resolvedComponentName.equals("") && loadingUnitId > 0) {
            loadDartLibrary(loadingUnitId, resolvedComponentName);
        } else {
            SplitInstallRequest request = SplitInstallRequest.newBuilder().addModule(resolvedComponentName).build();
            this.splitInstallManager.startInstall(request).addOnSuccessListener(new OnSuccessListener() { // from class: io.flutter.embedding.engine.deferredcomponents.PlayStoreDeferredComponentManager$$ExternalSyntheticLambda0
                public final void onSuccess(Object obj) {
                    PlayStoreDeferredComponentManager.this.m23x3d4be899(resolvedComponentName, loadingUnitId, (Integer) obj);
                }
            }).addOnFailureListener(new OnFailureListener() { // from class: io.flutter.embedding.engine.deferredcomponents.PlayStoreDeferredComponentManager$$ExternalSyntheticLambda1
                public final void onFailure(Exception exc) {
                    PlayStoreDeferredComponentManager.this.m24x6bfd52b8(loadingUnitId, componentName, exc);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$installDeferredComponent$0$io-flutter-embedding-engine-deferredcomponents-PlayStoreDeferredComponentManager  reason: not valid java name */
    public /* synthetic */ void m23x3d4be899(String resolvedComponentName, int loadingUnitId, Integer sessionId) {
        this.sessionIdToName.put(sessionId.intValue(), resolvedComponentName);
        this.sessionIdToLoadingUnitId.put(sessionId.intValue(), loadingUnitId);
        if (this.nameToSessionId.containsKey(resolvedComponentName)) {
            this.sessionIdToState.remove(this.nameToSessionId.get(resolvedComponentName).intValue());
        }
        this.nameToSessionId.put(resolvedComponentName, sessionId);
        this.sessionIdToState.put(sessionId.intValue(), "Requested");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$installDeferredComponent$1$io-flutter-embedding-engine-deferredcomponents-PlayStoreDeferredComponentManager  reason: not valid java name */
    public /* synthetic */ void m24x6bfd52b8(int loadingUnitId, String componentName, Exception exception) {
        switch (((SplitInstallException) exception).getErrorCode()) {
            case -6:
                this.flutterJNI.deferredComponentInstallFailure(loadingUnitId, String.format("Install of deferred component module \"%s\" failed with a network error", componentName), true);
                return;
            case -2:
                this.flutterJNI.deferredComponentInstallFailure(loadingUnitId, String.format("Install of deferred component module \"%s\" failed as it is unavailable", componentName), false);
                return;
            default:
                this.flutterJNI.deferredComponentInstallFailure(loadingUnitId, String.format("Install of deferred component module \"%s\" failed with error %d: %s", componentName, Integer.valueOf(((SplitInstallException) exception).getErrorCode()), ((SplitInstallException) exception).getMessage()), false);
                return;
        }
    }

    @Override // io.flutter.embedding.engine.deferredcomponents.DeferredComponentManager
    public String getDeferredComponentInstallState(int loadingUnitId, String componentName) {
        String resolvedComponentName = componentName != null ? componentName : this.loadingUnitIdToComponentNames.get(loadingUnitId);
        if (resolvedComponentName == null) {
            Log.e(TAG, "Deferred component name was null and could not be resolved from loading unit id.");
            return EnvironmentCompat.MEDIA_UNKNOWN;
        } else if (!this.nameToSessionId.containsKey(resolvedComponentName)) {
            if (!this.splitInstallManager.getInstalledModules().contains(resolvedComponentName)) {
                return EnvironmentCompat.MEDIA_UNKNOWN;
            }
            return "installedPendingLoad";
        } else {
            int sessionId = this.nameToSessionId.get(resolvedComponentName).intValue();
            return this.sessionIdToState.get(sessionId);
        }
    }

    @Override // io.flutter.embedding.engine.deferredcomponents.DeferredComponentManager
    public void loadAssets(int loadingUnitId, String componentName) {
        if (!verifyJNI()) {
            return;
        }
        try {
            Context context = this.context;
            Context createPackageContext = context.createPackageContext(context.getPackageName(), 0);
            this.context = createPackageContext;
            AssetManager assetManager = createPackageContext.getAssets();
            this.flutterJNI.updateJavaAssetManager(assetManager, this.flutterApplicationInfo.flutterAssetsDir);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // io.flutter.embedding.engine.deferredcomponents.DeferredComponentManager
    public void loadDartLibrary(int loadingUnitId, String componentName) {
        String abi;
        File[] listFiles;
        String[] strArr;
        if (!verifyJNI() || loadingUnitId < 0) {
            return;
        }
        String aotSharedLibraryName = this.loadingUnitIdToSharedLibraryNames.get(loadingUnitId);
        if (aotSharedLibraryName == null) {
            aotSharedLibraryName = this.flutterApplicationInfo.aotSharedLibraryName + "-" + loadingUnitId + ".part.so";
        }
        if (Build.VERSION.SDK_INT >= 21) {
            abi = Build.SUPPORTED_ABIS[0];
        } else {
            abi = Build.CPU_ABI;
        }
        String pathAbi = abi.replace("-", "_");
        List<String> apkPaths = new ArrayList<>();
        List<String> soPaths = new ArrayList<>();
        Queue<File> searchFiles = new LinkedList<>();
        searchFiles.add(this.context.getFilesDir());
        if (Build.VERSION.SDK_INT >= 21) {
            for (String path : this.context.getApplicationInfo().splitSourceDirs) {
                searchFiles.add(new File(path));
            }
        }
        while (!searchFiles.isEmpty()) {
            File file = searchFiles.remove();
            if (file != null && file.isDirectory() && file.listFiles() != null) {
                for (File f : file.listFiles()) {
                    searchFiles.add(f);
                }
            } else {
                String name = file.getName();
                if (name.endsWith(".apk") && ((name.startsWith(componentName) || name.startsWith("split_config")) && name.contains(pathAbi))) {
                    apkPaths.add(file.getAbsolutePath());
                } else if (name.equals(aotSharedLibraryName)) {
                    soPaths.add(file.getAbsolutePath());
                }
            }
        }
        List<String> searchPaths = new ArrayList<>();
        searchPaths.add(aotSharedLibraryName);
        for (String path2 : apkPaths) {
            searchPaths.add(path2 + "!lib/" + abi + "/" + aotSharedLibraryName);
        }
        for (String path3 : soPaths) {
            searchPaths.add(path3);
        }
        this.flutterJNI.loadDartDeferredLibrary(loadingUnitId, (String[]) searchPaths.toArray(new String[searchPaths.size()]));
    }

    @Override // io.flutter.embedding.engine.deferredcomponents.DeferredComponentManager
    public boolean uninstallDeferredComponent(int loadingUnitId, String componentName) {
        String resolvedComponentName = componentName != null ? componentName : this.loadingUnitIdToComponentNames.get(loadingUnitId);
        if (resolvedComponentName == null) {
            Log.e(TAG, "Deferred component name was null and could not be resolved from loading unit id.");
            return false;
        }
        List<String> modulesToUninstall = new ArrayList<>();
        modulesToUninstall.add(resolvedComponentName);
        this.splitInstallManager.deferredUninstall(modulesToUninstall);
        if (this.nameToSessionId.get(resolvedComponentName) != null) {
            this.sessionIdToState.delete(this.nameToSessionId.get(resolvedComponentName).intValue());
            return true;
        }
        return true;
    }

    @Override // io.flutter.embedding.engine.deferredcomponents.DeferredComponentManager
    public void destroy() {
        this.splitInstallManager.unregisterListener(this.listener);
        this.channel = null;
        this.flutterJNI = null;
    }
}
