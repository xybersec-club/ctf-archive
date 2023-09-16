package io.flutter.embedding.engine;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.lifecycle.Lifecycle;
import io.flutter.Log;
import io.flutter.embedding.android.ExclusiveAppComponent;
import io.flutter.embedding.engine.loader.FlutterLoader;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.PluginRegistry;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityControlSurface;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.broadcastreceiver.BroadcastReceiverAware;
import io.flutter.embedding.engine.plugins.broadcastreceiver.BroadcastReceiverControlSurface;
import io.flutter.embedding.engine.plugins.broadcastreceiver.BroadcastReceiverPluginBinding;
import io.flutter.embedding.engine.plugins.contentprovider.ContentProviderAware;
import io.flutter.embedding.engine.plugins.contentprovider.ContentProviderControlSurface;
import io.flutter.embedding.engine.plugins.contentprovider.ContentProviderPluginBinding;
import io.flutter.embedding.engine.plugins.lifecycle.HiddenLifecycleReference;
import io.flutter.embedding.engine.plugins.service.ServiceAware;
import io.flutter.embedding.engine.plugins.service.ServiceControlSurface;
import io.flutter.embedding.engine.plugins.service.ServicePluginBinding;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.util.TraceSection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class FlutterEngineConnectionRegistry implements PluginRegistry, ActivityControlSurface, ServiceControlSurface, BroadcastReceiverControlSurface, ContentProviderControlSurface {
    private static final String TAG = "FlutterEngineCxnRegstry";
    private FlutterEngineActivityPluginBinding activityPluginBinding;
    private BroadcastReceiver broadcastReceiver;
    private FlutterEngineBroadcastReceiverPluginBinding broadcastReceiverPluginBinding;
    private ContentProvider contentProvider;
    private FlutterEngineContentProviderPluginBinding contentProviderPluginBinding;
    private ExclusiveAppComponent<Activity> exclusiveActivity;
    private final FlutterEngine flutterEngine;
    private final FlutterPlugin.FlutterPluginBinding pluginBinding;
    private Service service;
    private FlutterEngineServicePluginBinding servicePluginBinding;
    private final Map<Class<? extends FlutterPlugin>, FlutterPlugin> plugins = new HashMap();
    private final Map<Class<? extends FlutterPlugin>, ActivityAware> activityAwarePlugins = new HashMap();
    private boolean isWaitingForActivityReattachment = false;
    private final Map<Class<? extends FlutterPlugin>, ServiceAware> serviceAwarePlugins = new HashMap();
    private final Map<Class<? extends FlutterPlugin>, BroadcastReceiverAware> broadcastReceiverAwarePlugins = new HashMap();
    private final Map<Class<? extends FlutterPlugin>, ContentProviderAware> contentProviderAwarePlugins = new HashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public FlutterEngineConnectionRegistry(Context appContext, FlutterEngine flutterEngine, FlutterLoader flutterLoader, FlutterEngineGroup group) {
        this.flutterEngine = flutterEngine;
        this.pluginBinding = new FlutterPlugin.FlutterPluginBinding(appContext, flutterEngine, flutterEngine.getDartExecutor(), flutterEngine.getRenderer(), flutterEngine.getPlatformViewsController().getRegistry(), new DefaultFlutterAssets(flutterLoader), group);
    }

    public void destroy() {
        Log.v(TAG, "Destroying.");
        detachFromAppComponent();
        removeAll();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // io.flutter.embedding.engine.plugins.PluginRegistry
    public void add(FlutterPlugin plugin) {
        TraceSection.begin("FlutterEngineConnectionRegistry#add " + plugin.getClass().getSimpleName());
        try {
            if (has(plugin.getClass())) {
                Log.w(TAG, "Attempted to register plugin (" + plugin + ") but it was already registered with this FlutterEngine (" + this.flutterEngine + ").");
                return;
            }
            Log.v(TAG, "Adding plugin: " + plugin);
            this.plugins.put(plugin.getClass(), plugin);
            plugin.onAttachedToEngine(this.pluginBinding);
            if (plugin instanceof ActivityAware) {
                ActivityAware activityAware = (ActivityAware) plugin;
                this.activityAwarePlugins.put(plugin.getClass(), activityAware);
                if (isAttachedToActivity()) {
                    activityAware.onAttachedToActivity(this.activityPluginBinding);
                }
            }
            if (plugin instanceof ServiceAware) {
                ServiceAware serviceAware = (ServiceAware) plugin;
                this.serviceAwarePlugins.put(plugin.getClass(), serviceAware);
                if (isAttachedToService()) {
                    serviceAware.onAttachedToService(this.servicePluginBinding);
                }
            }
            if (plugin instanceof BroadcastReceiverAware) {
                BroadcastReceiverAware broadcastReceiverAware = (BroadcastReceiverAware) plugin;
                this.broadcastReceiverAwarePlugins.put(plugin.getClass(), broadcastReceiverAware);
                if (isAttachedToBroadcastReceiver()) {
                    broadcastReceiverAware.onAttachedToBroadcastReceiver(this.broadcastReceiverPluginBinding);
                }
            }
            if (plugin instanceof ContentProviderAware) {
                ContentProviderAware contentProviderAware = (ContentProviderAware) plugin;
                this.contentProviderAwarePlugins.put(plugin.getClass(), contentProviderAware);
                if (isAttachedToContentProvider()) {
                    contentProviderAware.onAttachedToContentProvider(this.contentProviderPluginBinding);
                }
            }
        } finally {
            TraceSection.end();
        }
    }

    @Override // io.flutter.embedding.engine.plugins.PluginRegistry
    public void add(Set<FlutterPlugin> plugins) {
        for (FlutterPlugin plugin : plugins) {
            add(plugin);
        }
    }

    @Override // io.flutter.embedding.engine.plugins.PluginRegistry
    public boolean has(Class<? extends FlutterPlugin> pluginClass) {
        return this.plugins.containsKey(pluginClass);
    }

    @Override // io.flutter.embedding.engine.plugins.PluginRegistry
    public FlutterPlugin get(Class<? extends FlutterPlugin> pluginClass) {
        return this.plugins.get(pluginClass);
    }

    @Override // io.flutter.embedding.engine.plugins.PluginRegistry
    public void remove(Class<? extends FlutterPlugin> pluginClass) {
        FlutterPlugin plugin = this.plugins.get(pluginClass);
        if (plugin == null) {
            return;
        }
        TraceSection.begin("FlutterEngineConnectionRegistry#remove " + pluginClass.getSimpleName());
        try {
            if (plugin instanceof ActivityAware) {
                if (isAttachedToActivity()) {
                    ActivityAware activityAware = (ActivityAware) plugin;
                    activityAware.onDetachedFromActivity();
                }
                this.activityAwarePlugins.remove(pluginClass);
            }
            if (plugin instanceof ServiceAware) {
                if (isAttachedToService()) {
                    ServiceAware serviceAware = (ServiceAware) plugin;
                    serviceAware.onDetachedFromService();
                }
                this.serviceAwarePlugins.remove(pluginClass);
            }
            if (plugin instanceof BroadcastReceiverAware) {
                if (isAttachedToBroadcastReceiver()) {
                    BroadcastReceiverAware broadcastReceiverAware = (BroadcastReceiverAware) plugin;
                    broadcastReceiverAware.onDetachedFromBroadcastReceiver();
                }
                this.broadcastReceiverAwarePlugins.remove(pluginClass);
            }
            if (plugin instanceof ContentProviderAware) {
                if (isAttachedToContentProvider()) {
                    ContentProviderAware contentProviderAware = (ContentProviderAware) plugin;
                    contentProviderAware.onDetachedFromContentProvider();
                }
                this.contentProviderAwarePlugins.remove(pluginClass);
            }
            plugin.onDetachedFromEngine(this.pluginBinding);
            this.plugins.remove(pluginClass);
        } finally {
            TraceSection.end();
        }
    }

    @Override // io.flutter.embedding.engine.plugins.PluginRegistry
    public void remove(Set<Class<? extends FlutterPlugin>> pluginClasses) {
        for (Class<? extends FlutterPlugin> pluginClass : pluginClasses) {
            remove(pluginClass);
        }
    }

    @Override // io.flutter.embedding.engine.plugins.PluginRegistry
    public void removeAll() {
        remove(new HashSet(this.plugins.keySet()));
        this.plugins.clear();
    }

    private void detachFromAppComponent() {
        if (isAttachedToActivity()) {
            detachFromActivity();
        } else if (isAttachedToService()) {
            detachFromService();
        } else if (isAttachedToBroadcastReceiver()) {
            detachFromBroadcastReceiver();
        } else if (isAttachedToContentProvider()) {
            detachFromContentProvider();
        }
    }

    private boolean isAttachedToActivity() {
        return this.exclusiveActivity != null;
    }

    private Activity attachedActivity() {
        ExclusiveAppComponent<Activity> exclusiveAppComponent = this.exclusiveActivity;
        if (exclusiveAppComponent != null) {
            return exclusiveAppComponent.getAppComponent();
        }
        return null;
    }

    @Override // io.flutter.embedding.engine.plugins.activity.ActivityControlSurface
    public void attachToActivity(ExclusiveAppComponent<Activity> exclusiveActivity, Lifecycle lifecycle) {
        TraceSection.begin("FlutterEngineConnectionRegistry#attachToActivity");
        try {
            ExclusiveAppComponent<Activity> exclusiveAppComponent = this.exclusiveActivity;
            if (exclusiveAppComponent != null) {
                exclusiveAppComponent.detachFromFlutterEngine();
            }
            detachFromAppComponent();
            this.exclusiveActivity = exclusiveActivity;
            attachToActivityInternal(exclusiveActivity.getAppComponent(), lifecycle);
        } finally {
            TraceSection.end();
        }
    }

    private void attachToActivityInternal(Activity activity, Lifecycle lifecycle) {
        boolean useSoftwareRendering;
        this.activityPluginBinding = new FlutterEngineActivityPluginBinding(activity, lifecycle);
        if (activity.getIntent() != null) {
            useSoftwareRendering = activity.getIntent().getBooleanExtra(FlutterShellArgs.ARG_KEY_ENABLE_SOFTWARE_RENDERING, false);
        } else {
            useSoftwareRendering = false;
        }
        this.flutterEngine.getPlatformViewsController().setSoftwareRendering(useSoftwareRendering);
        this.flutterEngine.getPlatformViewsController().attach(activity, this.flutterEngine.getRenderer(), this.flutterEngine.getDartExecutor());
        for (ActivityAware activityAware : this.activityAwarePlugins.values()) {
            if (this.isWaitingForActivityReattachment) {
                activityAware.onReattachedToActivityForConfigChanges(this.activityPluginBinding);
            } else {
                activityAware.onAttachedToActivity(this.activityPluginBinding);
            }
        }
        this.isWaitingForActivityReattachment = false;
    }

    @Override // io.flutter.embedding.engine.plugins.activity.ActivityControlSurface
    public void detachFromActivityForConfigChanges() {
        if (isAttachedToActivity()) {
            TraceSection.begin("FlutterEngineConnectionRegistry#detachFromActivityForConfigChanges");
            try {
                this.isWaitingForActivityReattachment = true;
                for (ActivityAware activityAware : this.activityAwarePlugins.values()) {
                    activityAware.onDetachedFromActivityForConfigChanges();
                }
                detachFromActivityInternal();
                return;
            } finally {
                TraceSection.end();
            }
        }
        Log.e(TAG, "Attempted to detach plugins from an Activity when no Activity was attached.");
    }

    @Override // io.flutter.embedding.engine.plugins.activity.ActivityControlSurface
    public void detachFromActivity() {
        if (isAttachedToActivity()) {
            TraceSection.begin("FlutterEngineConnectionRegistry#detachFromActivity");
            try {
                for (ActivityAware activityAware : this.activityAwarePlugins.values()) {
                    activityAware.onDetachedFromActivity();
                }
                detachFromActivityInternal();
                return;
            } finally {
                TraceSection.end();
            }
        }
        Log.e(TAG, "Attempted to detach plugins from an Activity when no Activity was attached.");
    }

    private void detachFromActivityInternal() {
        this.flutterEngine.getPlatformViewsController().detach();
        this.exclusiveActivity = null;
        this.activityPluginBinding = null;
    }

    @Override // io.flutter.embedding.engine.plugins.activity.ActivityControlSurface
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        if (isAttachedToActivity()) {
            TraceSection.begin("FlutterEngineConnectionRegistry#onRequestPermissionsResult");
            try {
                return this.activityPluginBinding.onRequestPermissionsResult(requestCode, permissions, grantResult);
            } finally {
                TraceSection.end();
            }
        }
        Log.e(TAG, "Attempted to notify ActivityAware plugins of onRequestPermissionsResult, but no Activity was attached.");
        return false;
    }

    @Override // io.flutter.embedding.engine.plugins.activity.ActivityControlSurface
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isAttachedToActivity()) {
            TraceSection.begin("FlutterEngineConnectionRegistry#onActivityResult");
            try {
                return this.activityPluginBinding.onActivityResult(requestCode, resultCode, data);
            } finally {
                TraceSection.end();
            }
        }
        Log.e(TAG, "Attempted to notify ActivityAware plugins of onActivityResult, but no Activity was attached.");
        return false;
    }

    @Override // io.flutter.embedding.engine.plugins.activity.ActivityControlSurface
    public void onNewIntent(Intent intent) {
        if (isAttachedToActivity()) {
            TraceSection.begin("FlutterEngineConnectionRegistry#onNewIntent");
            try {
                this.activityPluginBinding.onNewIntent(intent);
                return;
            } finally {
                TraceSection.end();
            }
        }
        Log.e(TAG, "Attempted to notify ActivityAware plugins of onNewIntent, but no Activity was attached.");
    }

    @Override // io.flutter.embedding.engine.plugins.activity.ActivityControlSurface
    public void onUserLeaveHint() {
        if (isAttachedToActivity()) {
            TraceSection.begin("FlutterEngineConnectionRegistry#onUserLeaveHint");
            try {
                this.activityPluginBinding.onUserLeaveHint();
                return;
            } finally {
                TraceSection.end();
            }
        }
        Log.e(TAG, "Attempted to notify ActivityAware plugins of onUserLeaveHint, but no Activity was attached.");
    }

    @Override // io.flutter.embedding.engine.plugins.activity.ActivityControlSurface
    public void onSaveInstanceState(Bundle bundle) {
        if (isAttachedToActivity()) {
            TraceSection.begin("FlutterEngineConnectionRegistry#onSaveInstanceState");
            try {
                this.activityPluginBinding.onSaveInstanceState(bundle);
                return;
            } finally {
                TraceSection.end();
            }
        }
        Log.e(TAG, "Attempted to notify ActivityAware plugins of onSaveInstanceState, but no Activity was attached.");
    }

    @Override // io.flutter.embedding.engine.plugins.activity.ActivityControlSurface
    public void onRestoreInstanceState(Bundle bundle) {
        if (isAttachedToActivity()) {
            TraceSection.begin("FlutterEngineConnectionRegistry#onRestoreInstanceState");
            try {
                this.activityPluginBinding.onRestoreInstanceState(bundle);
                return;
            } finally {
                TraceSection.end();
            }
        }
        Log.e(TAG, "Attempted to notify ActivityAware plugins of onRestoreInstanceState, but no Activity was attached.");
    }

    private boolean isAttachedToService() {
        return this.service != null;
    }

    @Override // io.flutter.embedding.engine.plugins.service.ServiceControlSurface
    public void attachToService(Service service, Lifecycle lifecycle, boolean isForeground) {
        TraceSection.begin("FlutterEngineConnectionRegistry#attachToService");
        try {
            detachFromAppComponent();
            this.service = service;
            this.servicePluginBinding = new FlutterEngineServicePluginBinding(service, lifecycle);
            for (ServiceAware serviceAware : this.serviceAwarePlugins.values()) {
                serviceAware.onAttachedToService(this.servicePluginBinding);
            }
        } finally {
            TraceSection.end();
        }
    }

    @Override // io.flutter.embedding.engine.plugins.service.ServiceControlSurface
    public void detachFromService() {
        if (isAttachedToService()) {
            TraceSection.begin("FlutterEngineConnectionRegistry#detachFromService");
            try {
                for (ServiceAware serviceAware : this.serviceAwarePlugins.values()) {
                    serviceAware.onDetachedFromService();
                }
                this.service = null;
                this.servicePluginBinding = null;
                return;
            } finally {
                TraceSection.end();
            }
        }
        Log.e(TAG, "Attempted to detach plugins from a Service when no Service was attached.");
    }

    @Override // io.flutter.embedding.engine.plugins.service.ServiceControlSurface
    public void onMoveToForeground() {
        if (isAttachedToService()) {
            TraceSection.begin("FlutterEngineConnectionRegistry#onMoveToForeground");
            try {
                this.servicePluginBinding.onMoveToForeground();
            } finally {
                TraceSection.end();
            }
        }
    }

    @Override // io.flutter.embedding.engine.plugins.service.ServiceControlSurface
    public void onMoveToBackground() {
        if (isAttachedToService()) {
            TraceSection.begin("FlutterEngineConnectionRegistry#onMoveToBackground");
            try {
                this.servicePluginBinding.onMoveToBackground();
            } finally {
                TraceSection.end();
            }
        }
    }

    private boolean isAttachedToBroadcastReceiver() {
        return this.broadcastReceiver != null;
    }

    @Override // io.flutter.embedding.engine.plugins.broadcastreceiver.BroadcastReceiverControlSurface
    public void attachToBroadcastReceiver(BroadcastReceiver broadcastReceiver, Lifecycle lifecycle) {
        TraceSection.begin("FlutterEngineConnectionRegistry#attachToBroadcastReceiver");
        try {
            detachFromAppComponent();
            this.broadcastReceiver = broadcastReceiver;
            this.broadcastReceiverPluginBinding = new FlutterEngineBroadcastReceiverPluginBinding(broadcastReceiver);
            for (BroadcastReceiverAware broadcastReceiverAware : this.broadcastReceiverAwarePlugins.values()) {
                broadcastReceiverAware.onAttachedToBroadcastReceiver(this.broadcastReceiverPluginBinding);
            }
        } finally {
            TraceSection.end();
        }
    }

    @Override // io.flutter.embedding.engine.plugins.broadcastreceiver.BroadcastReceiverControlSurface
    public void detachFromBroadcastReceiver() {
        if (isAttachedToBroadcastReceiver()) {
            TraceSection.begin("FlutterEngineConnectionRegistry#detachFromBroadcastReceiver");
            try {
                for (BroadcastReceiverAware broadcastReceiverAware : this.broadcastReceiverAwarePlugins.values()) {
                    broadcastReceiverAware.onDetachedFromBroadcastReceiver();
                }
                return;
            } finally {
                TraceSection.end();
            }
        }
        Log.e(TAG, "Attempted to detach plugins from a BroadcastReceiver when no BroadcastReceiver was attached.");
    }

    private boolean isAttachedToContentProvider() {
        return this.contentProvider != null;
    }

    @Override // io.flutter.embedding.engine.plugins.contentprovider.ContentProviderControlSurface
    public void attachToContentProvider(ContentProvider contentProvider, Lifecycle lifecycle) {
        TraceSection.begin("FlutterEngineConnectionRegistry#attachToContentProvider");
        try {
            detachFromAppComponent();
            this.contentProvider = contentProvider;
            this.contentProviderPluginBinding = new FlutterEngineContentProviderPluginBinding(contentProvider);
            for (ContentProviderAware contentProviderAware : this.contentProviderAwarePlugins.values()) {
                contentProviderAware.onAttachedToContentProvider(this.contentProviderPluginBinding);
            }
        } finally {
            TraceSection.end();
        }
    }

    @Override // io.flutter.embedding.engine.plugins.contentprovider.ContentProviderControlSurface
    public void detachFromContentProvider() {
        if (isAttachedToContentProvider()) {
            TraceSection.begin("FlutterEngineConnectionRegistry#detachFromContentProvider");
            try {
                for (ContentProviderAware contentProviderAware : this.contentProviderAwarePlugins.values()) {
                    contentProviderAware.onDetachedFromContentProvider();
                }
                return;
            } finally {
                TraceSection.end();
            }
        }
        Log.e(TAG, "Attempted to detach plugins from a ContentProvider when no ContentProvider was attached.");
    }

    /* loaded from: classes.dex */
    private static class DefaultFlutterAssets implements FlutterPlugin.FlutterAssets {
        final FlutterLoader flutterLoader;

        private DefaultFlutterAssets(FlutterLoader flutterLoader) {
            this.flutterLoader = flutterLoader;
        }

        @Override // io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
        public String getAssetFilePathByName(String assetFileName) {
            return this.flutterLoader.getLookupKeyForAsset(assetFileName);
        }

        @Override // io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
        public String getAssetFilePathByName(String assetFileName, String packageName) {
            return this.flutterLoader.getLookupKeyForAsset(assetFileName, packageName);
        }

        @Override // io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
        public String getAssetFilePathBySubpath(String assetSubpath) {
            return this.flutterLoader.getLookupKeyForAsset(assetSubpath);
        }

        @Override // io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
        public String getAssetFilePathBySubpath(String assetSubpath, String packageName) {
            return this.flutterLoader.getLookupKeyForAsset(assetSubpath, packageName);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FlutterEngineActivityPluginBinding implements ActivityPluginBinding {
        private final Activity activity;
        private final HiddenLifecycleReference hiddenLifecycleReference;
        private final Set<PluginRegistry.RequestPermissionsResultListener> onRequestPermissionsResultListeners = new HashSet();
        private final Set<PluginRegistry.ActivityResultListener> onActivityResultListeners = new HashSet();
        private final Set<PluginRegistry.NewIntentListener> onNewIntentListeners = new HashSet();
        private final Set<PluginRegistry.UserLeaveHintListener> onUserLeaveHintListeners = new HashSet();
        private final Set<ActivityPluginBinding.OnSaveInstanceStateListener> onSaveInstanceStateListeners = new HashSet();

        public FlutterEngineActivityPluginBinding(Activity activity, Lifecycle lifecycle) {
            this.activity = activity;
            this.hiddenLifecycleReference = new HiddenLifecycleReference(lifecycle);
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
        public Activity getActivity() {
            return this.activity;
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
        public Object getLifecycle() {
            return this.hiddenLifecycleReference;
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
        public void addRequestPermissionsResultListener(PluginRegistry.RequestPermissionsResultListener listener) {
            this.onRequestPermissionsResultListeners.add(listener);
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
        public void removeRequestPermissionsResultListener(PluginRegistry.RequestPermissionsResultListener listener) {
            this.onRequestPermissionsResultListeners.remove(listener);
        }

        boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
            boolean didConsumeResult = false;
            for (PluginRegistry.RequestPermissionsResultListener listener : this.onRequestPermissionsResultListeners) {
                didConsumeResult = listener.onRequestPermissionsResult(requestCode, permissions, grantResult) || didConsumeResult;
            }
            return didConsumeResult;
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
        public void addActivityResultListener(PluginRegistry.ActivityResultListener listener) {
            this.onActivityResultListeners.add(listener);
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
        public void removeActivityResultListener(PluginRegistry.ActivityResultListener listener) {
            this.onActivityResultListeners.remove(listener);
        }

        boolean onActivityResult(int requestCode, int resultCode, Intent data) {
            boolean didConsumeResult = false;
            Iterator it = new HashSet(this.onActivityResultListeners).iterator();
            while (it.hasNext()) {
                PluginRegistry.ActivityResultListener listener = (PluginRegistry.ActivityResultListener) it.next();
                didConsumeResult = listener.onActivityResult(requestCode, resultCode, data) || didConsumeResult;
            }
            return didConsumeResult;
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
        public void addOnNewIntentListener(PluginRegistry.NewIntentListener listener) {
            this.onNewIntentListeners.add(listener);
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
        public void removeOnNewIntentListener(PluginRegistry.NewIntentListener listener) {
            this.onNewIntentListeners.remove(listener);
        }

        void onNewIntent(Intent intent) {
            for (PluginRegistry.NewIntentListener listener : this.onNewIntentListeners) {
                listener.onNewIntent(intent);
            }
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
        public void addOnUserLeaveHintListener(PluginRegistry.UserLeaveHintListener listener) {
            this.onUserLeaveHintListeners.add(listener);
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
        public void removeOnUserLeaveHintListener(PluginRegistry.UserLeaveHintListener listener) {
            this.onUserLeaveHintListeners.remove(listener);
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
        public void addOnSaveStateListener(ActivityPluginBinding.OnSaveInstanceStateListener listener) {
            this.onSaveInstanceStateListeners.add(listener);
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
        public void removeOnSaveStateListener(ActivityPluginBinding.OnSaveInstanceStateListener listener) {
            this.onSaveInstanceStateListeners.remove(listener);
        }

        void onUserLeaveHint() {
            for (PluginRegistry.UserLeaveHintListener listener : this.onUserLeaveHintListeners) {
                listener.onUserLeaveHint();
            }
        }

        void onSaveInstanceState(Bundle bundle) {
            for (ActivityPluginBinding.OnSaveInstanceStateListener listener : this.onSaveInstanceStateListeners) {
                listener.onSaveInstanceState(bundle);
            }
        }

        void onRestoreInstanceState(Bundle bundle) {
            for (ActivityPluginBinding.OnSaveInstanceStateListener listener : this.onSaveInstanceStateListeners) {
                listener.onRestoreInstanceState(bundle);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FlutterEngineServicePluginBinding implements ServicePluginBinding {
        private final HiddenLifecycleReference hiddenLifecycleReference;
        private final Set<ServiceAware.OnModeChangeListener> onModeChangeListeners = new HashSet();
        private final Service service;

        FlutterEngineServicePluginBinding(Service service, Lifecycle lifecycle) {
            this.service = service;
            this.hiddenLifecycleReference = lifecycle != null ? new HiddenLifecycleReference(lifecycle) : null;
        }

        @Override // io.flutter.embedding.engine.plugins.service.ServicePluginBinding
        public Service getService() {
            return this.service;
        }

        @Override // io.flutter.embedding.engine.plugins.service.ServicePluginBinding
        public Object getLifecycle() {
            return this.hiddenLifecycleReference;
        }

        @Override // io.flutter.embedding.engine.plugins.service.ServicePluginBinding
        public void addOnModeChangeListener(ServiceAware.OnModeChangeListener listener) {
            this.onModeChangeListeners.add(listener);
        }

        @Override // io.flutter.embedding.engine.plugins.service.ServicePluginBinding
        public void removeOnModeChangeListener(ServiceAware.OnModeChangeListener listener) {
            this.onModeChangeListeners.remove(listener);
        }

        void onMoveToForeground() {
            for (ServiceAware.OnModeChangeListener listener : this.onModeChangeListeners) {
                listener.onMoveToForeground();
            }
        }

        void onMoveToBackground() {
            for (ServiceAware.OnModeChangeListener listener : this.onModeChangeListeners) {
                listener.onMoveToBackground();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FlutterEngineBroadcastReceiverPluginBinding implements BroadcastReceiverPluginBinding {
        private final BroadcastReceiver broadcastReceiver;

        FlutterEngineBroadcastReceiverPluginBinding(BroadcastReceiver broadcastReceiver) {
            this.broadcastReceiver = broadcastReceiver;
        }

        @Override // io.flutter.embedding.engine.plugins.broadcastreceiver.BroadcastReceiverPluginBinding
        public BroadcastReceiver getBroadcastReceiver() {
            return this.broadcastReceiver;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FlutterEngineContentProviderPluginBinding implements ContentProviderPluginBinding {
        private final ContentProvider contentProvider;

        FlutterEngineContentProviderPluginBinding(ContentProvider contentProvider) {
            this.contentProvider = contentProvider;
        }

        @Override // io.flutter.embedding.engine.plugins.contentprovider.ContentProviderPluginBinding
        public ContentProvider getContentProvider() {
            return this.contentProvider;
        }
    }
}
