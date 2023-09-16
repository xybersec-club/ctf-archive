package io.flutter.embedding.engine.loader;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import io.flutter.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ResourceExtractor {
    private static final String[] SUPPORTED_ABIS = getSupportedAbis();
    private static final String TAG = "ResourceExtractor";
    private static final String TIMESTAMP_PREFIX = "res_timestamp-";
    private final AssetManager mAssetManager;
    private final String mDataDirPath;
    private ExtractTask mExtractTask;
    private final PackageManager mPackageManager;
    private final String mPackageName;
    private final HashSet<String> mResources = new HashSet<>();

    static long getVersionCode(PackageInfo packageInfo) {
        if (Build.VERSION.SDK_INT >= 28) {
            return packageInfo.getLongVersionCode();
        }
        return packageInfo.versionCode;
    }

    /* loaded from: classes.dex */
    private static class ExtractTask extends AsyncTask<Void, Void, Void> {
        private final AssetManager mAssetManager;
        private final String mDataDirPath;
        private final PackageManager mPackageManager;
        private final String mPackageName;
        private final HashSet<String> mResources;

        ExtractTask(String dataDirPath, HashSet<String> resources, String packageName, PackageManager packageManager, AssetManager assetManager) {
            this.mDataDirPath = dataDirPath;
            this.mResources = resources;
            this.mAssetManager = assetManager;
            this.mPackageName = packageName;
            this.mPackageManager = packageManager;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... unused) {
            File dataDir = new File(this.mDataDirPath);
            String timestamp = ResourceExtractor.checkTimestamp(dataDir, this.mPackageManager, this.mPackageName);
            if (timestamp != null) {
                ResourceExtractor.deleteFiles(this.mDataDirPath, this.mResources);
                if (extractAPK(dataDir) && timestamp != null) {
                    try {
                        new File(dataDir, timestamp).createNewFile();
                    } catch (IOException e) {
                        Log.w(ResourceExtractor.TAG, "Failed to write resource timestamp");
                    }
                }
                return null;
            }
            return null;
        }

        private boolean extractAPK(File dataDir) {
            Iterator<String> it = this.mResources.iterator();
            while (it.hasNext()) {
                String asset = it.next();
                try {
                    String resource = "assets/" + asset;
                    File output = new File(dataDir, asset);
                    if (!output.exists()) {
                        if (output.getParentFile() != null) {
                            output.getParentFile().mkdirs();
                        }
                        InputStream is = this.mAssetManager.open(asset);
                        OutputStream os = new FileOutputStream(output);
                        ResourceExtractor.copy(is, os);
                        os.close();
                        if (is != null) {
                            is.close();
                        }
                        Log.i(ResourceExtractor.TAG, "Extracted baseline resource " + resource);
                    }
                } catch (FileNotFoundException e) {
                } catch (IOException ioe) {
                    Log.w(ResourceExtractor.TAG, "Exception unpacking resources: " + ioe.getMessage());
                    ResourceExtractor.deleteFiles(this.mDataDirPath, this.mResources);
                    return false;
                }
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResourceExtractor(String dataDirPath, String packageName, PackageManager packageManager, AssetManager assetManager) {
        this.mDataDirPath = dataDirPath;
        this.mPackageName = packageName;
        this.mPackageManager = packageManager;
        this.mAssetManager = assetManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResourceExtractor addResource(String resource) {
        this.mResources.add(resource);
        return this;
    }

    ResourceExtractor addResources(Collection<String> resources) {
        this.mResources.addAll(resources);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResourceExtractor start() {
        if (this.mExtractTask != null) {
            Log.e(TAG, "Attempted to start resource extraction while another extraction was in progress.");
        }
        ExtractTask extractTask = new ExtractTask(this.mDataDirPath, this.mResources, this.mPackageName, this.mPackageManager, this.mAssetManager);
        this.mExtractTask = extractTask;
        extractTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void waitForCompletion() {
        ExtractTask extractTask = this.mExtractTask;
        if (extractTask == null) {
            return;
        }
        try {
            extractTask.get();
        } catch (InterruptedException | CancellationException | ExecutionException e) {
            deleteFiles(this.mDataDirPath, this.mResources);
        }
    }

    private static String[] getExistingTimestamps(File dataDir) {
        return dataDir.list(new FilenameFilter() { // from class: io.flutter.embedding.engine.loader.ResourceExtractor.1
            @Override // java.io.FilenameFilter
            public boolean accept(File dir, String name) {
                return name.startsWith(ResourceExtractor.TIMESTAMP_PREFIX);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void deleteFiles(String dataDirPath, HashSet<String> resources) {
        File dataDir = new File(dataDirPath);
        Iterator<String> it = resources.iterator();
        while (it.hasNext()) {
            String resource = it.next();
            File file = new File(dataDir, resource);
            if (file.exists()) {
                file.delete();
            }
        }
        String[] existingTimestamps = getExistingTimestamps(dataDir);
        if (existingTimestamps == null) {
            return;
        }
        for (String timestamp : existingTimestamps) {
            new File(dataDir, timestamp).delete();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String checkTimestamp(File dataDir, PackageManager packageManager, String packageName) {
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return TIMESTAMP_PREFIX;
            }
            String expectedTimestamp = TIMESTAMP_PREFIX + getVersionCode(packageInfo) + "-" + packageInfo.lastUpdateTime;
            String[] existingTimestamps = getExistingTimestamps(dataDir);
            if (existingTimestamps == null) {
                Log.i(TAG, "No extracted resources found");
                return expectedTimestamp;
            }
            if (existingTimestamps.length == 1) {
                Log.i(TAG, "Found extracted resources " + existingTimestamps[0]);
            }
            if (existingTimestamps.length != 1 || !expectedTimestamp.equals(existingTimestamps[0])) {
                Log.i(TAG, "Resource version mismatch " + expectedTimestamp);
                return expectedTimestamp;
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            return TIMESTAMP_PREFIX;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[16384];
        while (true) {
            int i = in.read(buf);
            if (i >= 0) {
                out.write(buf, 0, i);
            } else {
                return;
            }
        }
    }

    private static String[] getSupportedAbis() {
        if (Build.VERSION.SDK_INT >= 21) {
            return Build.SUPPORTED_ABIS;
        }
        ArrayList<String> cpuAbis = new ArrayList<>(Arrays.asList(Build.CPU_ABI, Build.CPU_ABI2));
        cpuAbis.removeAll(Arrays.asList(null, ""));
        return (String[]) cpuAbis.toArray(new String[0]);
    }
}
