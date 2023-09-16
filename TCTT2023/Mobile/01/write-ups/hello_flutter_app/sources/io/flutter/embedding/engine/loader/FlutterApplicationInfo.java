package io.flutter.embedding.engine.loader;
/* loaded from: classes.dex */
public final class FlutterApplicationInfo {
    private static final String DEFAULT_AOT_SHARED_LIBRARY_NAME = "libapp.so";
    private static final String DEFAULT_FLUTTER_ASSETS_DIR = "flutter_assets";
    private static final String DEFAULT_ISOLATE_SNAPSHOT_DATA = "isolate_snapshot_data";
    private static final String DEFAULT_VM_SNAPSHOT_DATA = "vm_snapshot_data";
    public final String aotSharedLibraryName;
    final boolean automaticallyRegisterPlugins;
    public final String domainNetworkPolicy;
    public final String flutterAssetsDir;
    public final String isolateSnapshotData;
    public final String nativeLibraryDir;
    public final String vmSnapshotData;

    public FlutterApplicationInfo(String aotSharedLibraryName, String vmSnapshotData, String isolateSnapshotData, String flutterAssetsDir, String domainNetworkPolicy, String nativeLibraryDir, boolean automaticallyRegisterPlugins) {
        this.aotSharedLibraryName = aotSharedLibraryName == null ? DEFAULT_AOT_SHARED_LIBRARY_NAME : aotSharedLibraryName;
        this.vmSnapshotData = vmSnapshotData == null ? DEFAULT_VM_SNAPSHOT_DATA : vmSnapshotData;
        this.isolateSnapshotData = isolateSnapshotData == null ? DEFAULT_ISOLATE_SNAPSHOT_DATA : isolateSnapshotData;
        this.flutterAssetsDir = flutterAssetsDir == null ? DEFAULT_FLUTTER_ASSETS_DIR : flutterAssetsDir;
        this.nativeLibraryDir = nativeLibraryDir;
        this.domainNetworkPolicy = domainNetworkPolicy == null ? "" : domainNetworkPolicy;
        this.automaticallyRegisterPlugins = automaticallyRegisterPlugins;
    }
}
