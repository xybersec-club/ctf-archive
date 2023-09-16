package io.flutter.embedding.engine.loader;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import io.flutter.FlutterInjector;
import io.flutter.Log;
import io.flutter.embedding.engine.FlutterJNI;
import io.flutter.embedding.engine.FlutterShellArgs;
import io.flutter.embedding.engine.loader.FlutterLoader;
import io.flutter.util.HandlerCompat;
import io.flutter.util.PathUtils;
import io.flutter.util.TraceSection;
import io.flutter.view.VsyncWaiter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
/* loaded from: classes.dex */
public class FlutterLoader {
    static final String AOT_SHARED_LIBRARY_NAME = "aot-shared-library-name";
    static final String AOT_VMSERVICE_SHARED_LIBRARY_NAME = "aot-vmservice-shared-library-name";
    static final String AUTOMATICALLY_REGISTER_PLUGINS_KEY = "automatically-register-plugins";
    private static final String DEFAULT_KERNEL_BLOB = "kernel_blob.bin";
    private static final String DEFAULT_LIBRARY = "libflutter.so";
    private static final String ENABLE_IMPELLER_META_DATA_KEY = "io.flutter.embedding.android.EnableImpeller";
    static final String FLUTTER_ASSETS_DIR_KEY = "flutter-assets-dir";
    static final String ISOLATE_SNAPSHOT_DATA_KEY = "isolate-snapshot-data";
    private static final String LEAK_VM_META_DATA_KEY = "io.flutter.embedding.android.LeakVM";
    private static final String OLD_GEN_HEAP_SIZE_META_DATA_KEY = "io.flutter.embedding.android.OldGenHeapSize";
    static final String SNAPSHOT_ASSET_PATH_KEY = "snapshot-asset-path";
    private static final String TAG = "FlutterLoader";
    private static final String VMSERVICE_SNAPSHOT_LIBRARY = "libvmservice_snapshot.so";
    static final String VM_SNAPSHOT_DATA_KEY = "vm-snapshot-data";
    private static FlutterLoader instance;
    private ExecutorService executorService;
    private FlutterApplicationInfo flutterApplicationInfo;
    private FlutterJNI flutterJNI;
    Future<InitResult> initResultFuture;
    private long initStartTimestampMillis;
    private boolean initialized;
    private Settings settings;

    public FlutterLoader() {
        this(FlutterInjector.instance().getFlutterJNIFactory().provideFlutterJNI());
    }

    public FlutterLoader(FlutterJNI flutterJNI) {
        this(flutterJNI, FlutterInjector.instance().executorService());
    }

    public FlutterLoader(FlutterJNI flutterJNI, ExecutorService executorService) {
        this.initialized = false;
        this.flutterJNI = flutterJNI;
        this.executorService = executorService;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InitResult {
        final String appStoragePath;
        final String dataDirPath;
        final String engineCachesPath;

        /* synthetic */ InitResult(String x0, String x1, String x2, AnonymousClass1 x3) {
            this(x0, x1, x2);
        }

        private InitResult(String appStoragePath, String engineCachesPath, String dataDirPath) {
            this.appStoragePath = appStoragePath;
            this.engineCachesPath = engineCachesPath;
            this.dataDirPath = dataDirPath;
        }
    }

    public void startInitialization(Context applicationContext) {
        startInitialization(applicationContext, new Settings());
    }

    public void startInitialization(Context applicationContext, Settings settings) {
        VsyncWaiter waiter;
        if (this.settings != null) {
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("startInitialization must be called on the main thread");
        }
        TraceSection.begin("FlutterLoader#startInitialization");
        try {
            Context appContext = applicationContext.getApplicationContext();
            this.settings = settings;
            this.initStartTimestampMillis = SystemClock.uptimeMillis();
            this.flutterApplicationInfo = ApplicationInfoLoader.load(appContext);
            if (Build.VERSION.SDK_INT >= 17) {
                DisplayManager dm = (DisplayManager) appContext.getSystemService("display");
                waiter = VsyncWaiter.getInstance(dm, this.flutterJNI);
            } else {
                float fps = ((WindowManager) appContext.getSystemService("window")).getDefaultDisplay().getRefreshRate();
                waiter = VsyncWaiter.getInstance(fps, this.flutterJNI);
            }
            waiter.init();
            this.initResultFuture = this.executorService.submit(new AnonymousClass1(appContext));
        } finally {
            TraceSection.end();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: io.flutter.embedding.engine.loader.FlutterLoader$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass1 implements Callable<InitResult> {
        final /* synthetic */ Context val$appContext;

        AnonymousClass1(Context context) {
            this.val$appContext = context;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public InitResult call() {
            TraceSection.begin("FlutterLoader initTask");
            try {
                ResourceExtractor resourceExtractor = FlutterLoader.this.initResources(this.val$appContext);
                FlutterLoader.this.flutterJNI.loadLibrary();
                FlutterLoader.this.flutterJNI.updateRefreshRate();
                FlutterLoader.this.executorService.execute(new Runnable() { // from class: io.flutter.embedding.engine.loader.FlutterLoader$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        FlutterLoader.AnonymousClass1.this.m27lambda$call$0$ioflutterembeddingengineloaderFlutterLoader$1();
                    }
                });
                if (resourceExtractor != null) {
                    resourceExtractor.waitForCompletion();
                }
                return new InitResult(PathUtils.getFilesDir(this.val$appContext), PathUtils.getCacheDirectory(this.val$appContext), PathUtils.getDataDirectory(this.val$appContext), null);
            } finally {
                TraceSection.end();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$call$0$io-flutter-embedding-engine-loader-FlutterLoader$1  reason: not valid java name */
        public /* synthetic */ void m27lambda$call$0$ioflutterembeddingengineloaderFlutterLoader$1() {
            FlutterLoader.this.flutterJNI.prefetchDefaultFontManager();
        }
    }

    public void ensureInitializationComplete(Context applicationContext, String[] args) {
        int oldGenHeapSizeMegaBytes;
        if (this.initialized) {
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("ensureInitializationComplete must be called on the main thread");
        }
        if (this.settings == null) {
            throw new IllegalStateException("ensureInitializationComplete must be called after startInitialization");
        }
        TraceSection.begin("FlutterLoader#ensureInitializationComplete");
        try {
            try {
                InitResult result = this.initResultFuture.get();
                List<String> shellArgs = new ArrayList<>();
                shellArgs.add("--icu-symbol-prefix=_binary_icudtl_dat");
                shellArgs.add("--icu-native-lib-path=" + this.flutterApplicationInfo.nativeLibraryDir + File.separator + DEFAULT_LIBRARY);
                if (args != null) {
                    Collections.addAll(shellArgs, args);
                }
                String snapshotAssetPath = result.dataDirPath + File.separator + this.flutterApplicationInfo.flutterAssetsDir;
                String kernelPath = snapshotAssetPath + File.separator + DEFAULT_KERNEL_BLOB;
                shellArgs.add("--snapshot-asset-path=" + snapshotAssetPath);
                shellArgs.add("--vm-snapshot-data=" + this.flutterApplicationInfo.vmSnapshotData);
                shellArgs.add("--isolate-snapshot-data=" + this.flutterApplicationInfo.isolateSnapshotData);
                shellArgs.add("--cache-dir-path=" + result.engineCachesPath);
                if (this.flutterApplicationInfo.domainNetworkPolicy != null) {
                    shellArgs.add("--domain-network-policy=" + this.flutterApplicationInfo.domainNetworkPolicy);
                }
                if (this.settings.getLogTag() != null) {
                    shellArgs.add("--log-tag=" + this.settings.getLogTag());
                }
                ApplicationInfo applicationInfo = applicationContext.getPackageManager().getApplicationInfo(applicationContext.getPackageName(), 128);
                Bundle metaData = applicationInfo.metaData;
                int oldGenHeapSizeMegaBytes2 = metaData != null ? metaData.getInt(OLD_GEN_HEAP_SIZE_META_DATA_KEY) : 0;
                if (oldGenHeapSizeMegaBytes2 == 0) {
                    try {
                        ActivityManager activityManager = (ActivityManager) applicationContext.getSystemService("activity");
                        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
                        activityManager.getMemoryInfo(memInfo);
                        double d = memInfo.totalMem;
                        Double.isNaN(d);
                        oldGenHeapSizeMegaBytes = (int) ((d / 1000000.0d) / 2.0d);
                    } catch (Exception e) {
                        e = e;
                        Log.e(TAG, "Flutter initialization failed.", e);
                        throw new RuntimeException(e);
                    }
                } else {
                    oldGenHeapSizeMegaBytes = oldGenHeapSizeMegaBytes2;
                }
                shellArgs.add("--old-gen-heap-size=" + oldGenHeapSizeMegaBytes);
                DisplayMetrics displayMetrics = applicationContext.getResources().getDisplayMetrics();
                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;
                int resourceCacheMaxBytesThreshold = screenWidth * screenHeight * 12 * 4;
                shellArgs.add("--resource-cache-max-bytes-threshold=" + resourceCacheMaxBytesThreshold);
                shellArgs.add("--prefetched-default-font-manager");
                if (metaData != null && metaData.getBoolean(ENABLE_IMPELLER_META_DATA_KEY, false)) {
                    shellArgs.add(FlutterShellArgs.ARG_ENABLE_IMPELLER);
                }
                String leakVM = isLeakVM(metaData) ? "true" : "false";
                shellArgs.add("--leak-vm=" + leakVM);
                long initTimeMillis = SystemClock.uptimeMillis() - this.initStartTimestampMillis;
                this.flutterJNI.init(applicationContext, (String[]) shellArgs.toArray(new String[0]), kernelPath, result.appStoragePath, result.engineCachesPath, initTimeMillis);
                this.initialized = true;
                TraceSection.end();
            } catch (Throwable th) {
                e = th;
                TraceSection.end();
                throw e;
            }
        } catch (Exception e2) {
            e = e2;
        } catch (Throwable th2) {
            e = th2;
            TraceSection.end();
            throw e;
        }
    }

    private static boolean isLeakVM(Bundle metaData) {
        if (metaData == null) {
            return true;
        }
        return metaData.getBoolean(LEAK_VM_META_DATA_KEY, true);
    }

    public void ensureInitializationCompleteAsync(final Context applicationContext, final String[] args, final Handler callbackHandler, final Runnable callback) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("ensureInitializationComplete must be called on the main thread");
        }
        if (this.settings == null) {
            throw new IllegalStateException("ensureInitializationComplete must be called after startInitialization");
        }
        if (this.initialized) {
            callbackHandler.post(callback);
        } else {
            this.executorService.execute(new Runnable() { // from class: io.flutter.embedding.engine.loader.FlutterLoader$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    FlutterLoader.this.m26xa15f5dc1(applicationContext, args, callbackHandler, callback);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$ensureInitializationCompleteAsync$1$io-flutter-embedding-engine-loader-FlutterLoader  reason: not valid java name */
    public /* synthetic */ void m26xa15f5dc1(final Context applicationContext, final String[] args, final Handler callbackHandler, final Runnable callback) {
        try {
            this.initResultFuture.get();
            HandlerCompat.createAsyncHandler(Looper.getMainLooper()).post(new Runnable() { // from class: io.flutter.embedding.engine.loader.FlutterLoader$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    FlutterLoader.this.m25xafb5b7a2(applicationContext, args, callbackHandler, callback);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Flutter initialization failed.", e);
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$ensureInitializationCompleteAsync$0$io-flutter-embedding-engine-loader-FlutterLoader  reason: not valid java name */
    public /* synthetic */ void m25xafb5b7a2(Context applicationContext, String[] args, Handler callbackHandler, Runnable callback) {
        ensureInitializationComplete(applicationContext.getApplicationContext(), args);
        callbackHandler.post(callback);
    }

    public boolean initialized() {
        return this.initialized;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ResourceExtractor initResources(Context applicationContext) {
        String dataDirPath = PathUtils.getDataDirectory(applicationContext);
        String packageName = applicationContext.getPackageName();
        PackageManager packageManager = applicationContext.getPackageManager();
        AssetManager assetManager = applicationContext.getResources().getAssets();
        ResourceExtractor resourceExtractor = new ResourceExtractor(dataDirPath, packageName, packageManager, assetManager);
        resourceExtractor.addResource(fullAssetPathFrom(this.flutterApplicationInfo.vmSnapshotData)).addResource(fullAssetPathFrom(this.flutterApplicationInfo.isolateSnapshotData)).addResource(fullAssetPathFrom(DEFAULT_KERNEL_BLOB));
        resourceExtractor.start();
        return resourceExtractor;
    }

    public String findAppBundlePath() {
        return this.flutterApplicationInfo.flutterAssetsDir;
    }

    public String getLookupKeyForAsset(String asset) {
        return fullAssetPathFrom(asset);
    }

    public String getLookupKeyForAsset(String asset, String packageName) {
        return getLookupKeyForAsset("packages" + File.separator + packageName + File.separator + asset);
    }

    public boolean automaticallyRegisterPlugins() {
        return this.flutterApplicationInfo.automaticallyRegisterPlugins;
    }

    private String fullAssetPathFrom(String filePath) {
        return this.flutterApplicationInfo.flutterAssetsDir + File.separator + filePath;
    }

    /* loaded from: classes.dex */
    public static class Settings {
        private String logTag;

        public String getLogTag() {
            return this.logTag;
        }

        public void setLogTag(String tag) {
            this.logTag = tag;
        }
    }
}
