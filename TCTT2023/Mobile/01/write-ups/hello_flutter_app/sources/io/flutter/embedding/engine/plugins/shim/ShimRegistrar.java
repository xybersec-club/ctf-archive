package io.flutter.embedding.engine.plugins.shim;

import android.app.Activity;
import android.content.Context;
import io.flutter.FlutterInjector;
import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.platform.PlatformViewRegistry;
import io.flutter.view.FlutterView;
import io.flutter.view.TextureRegistry;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
class ShimRegistrar implements PluginRegistry.Registrar, FlutterPlugin, ActivityAware {
    private static final String TAG = "ShimRegistrar";
    private ActivityPluginBinding activityPluginBinding;
    private final Map<String, Object> globalRegistrarMap;
    private FlutterPlugin.FlutterPluginBinding pluginBinding;
    private final String pluginId;
    private final Set<PluginRegistry.ViewDestroyListener> viewDestroyListeners = new HashSet();
    private final Set<PluginRegistry.RequestPermissionsResultListener> requestPermissionsResultListeners = new HashSet();
    private final Set<PluginRegistry.ActivityResultListener> activityResultListeners = new HashSet();
    private final Set<PluginRegistry.NewIntentListener> newIntentListeners = new HashSet();
    private final Set<PluginRegistry.UserLeaveHintListener> userLeaveHintListeners = new HashSet();

    public ShimRegistrar(String pluginId, Map<String, Object> globalRegistrarMap) {
        this.pluginId = pluginId;
        this.globalRegistrarMap = globalRegistrarMap;
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public Activity activity() {
        ActivityPluginBinding activityPluginBinding = this.activityPluginBinding;
        if (activityPluginBinding != null) {
            return activityPluginBinding.getActivity();
        }
        return null;
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public Context context() {
        FlutterPlugin.FlutterPluginBinding flutterPluginBinding = this.pluginBinding;
        if (flutterPluginBinding != null) {
            return flutterPluginBinding.getApplicationContext();
        }
        return null;
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public Context activeContext() {
        return this.activityPluginBinding == null ? context() : activity();
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public BinaryMessenger messenger() {
        FlutterPlugin.FlutterPluginBinding flutterPluginBinding = this.pluginBinding;
        if (flutterPluginBinding != null) {
            return flutterPluginBinding.getBinaryMessenger();
        }
        return null;
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public TextureRegistry textures() {
        FlutterPlugin.FlutterPluginBinding flutterPluginBinding = this.pluginBinding;
        if (flutterPluginBinding != null) {
            return flutterPluginBinding.getTextureRegistry();
        }
        return null;
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public PlatformViewRegistry platformViewRegistry() {
        FlutterPlugin.FlutterPluginBinding flutterPluginBinding = this.pluginBinding;
        if (flutterPluginBinding != null) {
            return flutterPluginBinding.getPlatformViewRegistry();
        }
        return null;
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public FlutterView view() {
        throw new UnsupportedOperationException("The new embedding does not support the old FlutterView.");
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public String lookupKeyForAsset(String asset) {
        return FlutterInjector.instance().flutterLoader().getLookupKeyForAsset(asset);
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public String lookupKeyForAsset(String asset, String packageName) {
        return FlutterInjector.instance().flutterLoader().getLookupKeyForAsset(asset, packageName);
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public PluginRegistry.Registrar publish(Object value) {
        this.globalRegistrarMap.put(this.pluginId, value);
        return this;
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public PluginRegistry.Registrar addRequestPermissionsResultListener(PluginRegistry.RequestPermissionsResultListener listener) {
        this.requestPermissionsResultListeners.add(listener);
        ActivityPluginBinding activityPluginBinding = this.activityPluginBinding;
        if (activityPluginBinding != null) {
            activityPluginBinding.addRequestPermissionsResultListener(listener);
        }
        return this;
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public PluginRegistry.Registrar addActivityResultListener(PluginRegistry.ActivityResultListener listener) {
        this.activityResultListeners.add(listener);
        ActivityPluginBinding activityPluginBinding = this.activityPluginBinding;
        if (activityPluginBinding != null) {
            activityPluginBinding.addActivityResultListener(listener);
        }
        return this;
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public PluginRegistry.Registrar addNewIntentListener(PluginRegistry.NewIntentListener listener) {
        this.newIntentListeners.add(listener);
        ActivityPluginBinding activityPluginBinding = this.activityPluginBinding;
        if (activityPluginBinding != null) {
            activityPluginBinding.addOnNewIntentListener(listener);
        }
        return this;
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public PluginRegistry.Registrar addUserLeaveHintListener(PluginRegistry.UserLeaveHintListener listener) {
        this.userLeaveHintListeners.add(listener);
        ActivityPluginBinding activityPluginBinding = this.activityPluginBinding;
        if (activityPluginBinding != null) {
            activityPluginBinding.addOnUserLeaveHintListener(listener);
        }
        return this;
    }

    @Override // io.flutter.plugin.common.PluginRegistry.Registrar
    public PluginRegistry.Registrar addViewDestroyListener(PluginRegistry.ViewDestroyListener listener) {
        this.viewDestroyListeners.add(listener);
        return this;
    }

    @Override // io.flutter.embedding.engine.plugins.FlutterPlugin
    public void onAttachedToEngine(FlutterPlugin.FlutterPluginBinding binding) {
        Log.v(TAG, "Attached to FlutterEngine.");
        this.pluginBinding = binding;
    }

    @Override // io.flutter.embedding.engine.plugins.FlutterPlugin
    public void onDetachedFromEngine(FlutterPlugin.FlutterPluginBinding binding) {
        Log.v(TAG, "Detached from FlutterEngine.");
        for (PluginRegistry.ViewDestroyListener listener : this.viewDestroyListeners) {
            listener.onViewDestroy(null);
        }
        this.pluginBinding = null;
        this.activityPluginBinding = null;
    }

    @Override // io.flutter.embedding.engine.plugins.activity.ActivityAware
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        Log.v(TAG, "Attached to an Activity.");
        this.activityPluginBinding = binding;
        addExistingListenersToActivityPluginBinding();
    }

    @Override // io.flutter.embedding.engine.plugins.activity.ActivityAware
    public void onDetachedFromActivityForConfigChanges() {
        Log.v(TAG, "Detached from an Activity for config changes.");
        this.activityPluginBinding = null;
    }

    @Override // io.flutter.embedding.engine.plugins.activity.ActivityAware
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        Log.v(TAG, "Reconnected to an Activity after config changes.");
        this.activityPluginBinding = binding;
        addExistingListenersToActivityPluginBinding();
    }

    @Override // io.flutter.embedding.engine.plugins.activity.ActivityAware
    public void onDetachedFromActivity() {
        Log.v(TAG, "Detached from an Activity.");
        this.activityPluginBinding = null;
    }

    private void addExistingListenersToActivityPluginBinding() {
        for (PluginRegistry.RequestPermissionsResultListener listener : this.requestPermissionsResultListeners) {
            this.activityPluginBinding.addRequestPermissionsResultListener(listener);
        }
        for (PluginRegistry.ActivityResultListener listener2 : this.activityResultListeners) {
            this.activityPluginBinding.addActivityResultListener(listener2);
        }
        for (PluginRegistry.NewIntentListener listener3 : this.newIntentListeners) {
            this.activityPluginBinding.addOnNewIntentListener(listener3);
        }
        for (PluginRegistry.UserLeaveHintListener listener4 : this.userLeaveHintListeners) {
            this.activityPluginBinding.addOnUserLeaveHintListener(listener4);
        }
    }
}
