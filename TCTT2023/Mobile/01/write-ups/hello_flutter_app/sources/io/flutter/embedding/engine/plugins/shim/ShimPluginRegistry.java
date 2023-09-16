package io.flutter.embedding.engine.plugins.shim;

import io.flutter.Log;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.PluginRegistry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public class ShimPluginRegistry implements PluginRegistry {
    private static final String TAG = "ShimPluginRegistry";
    private final FlutterEngine flutterEngine;
    private final Map<String, Object> pluginMap = new HashMap();
    private final ShimRegistrarAggregate shimRegistrarAggregate;

    public ShimPluginRegistry(FlutterEngine flutterEngine) {
        this.flutterEngine = flutterEngine;
        ShimRegistrarAggregate shimRegistrarAggregate = new ShimRegistrarAggregate();
        this.shimRegistrarAggregate = shimRegistrarAggregate;
        flutterEngine.getPlugins().add(shimRegistrarAggregate);
    }

    @Override // io.flutter.plugin.common.PluginRegistry
    public PluginRegistry.Registrar registrarFor(String pluginKey) {
        Log.v(TAG, "Creating plugin Registrar for '" + pluginKey + "'");
        if (this.pluginMap.containsKey(pluginKey)) {
            throw new IllegalStateException("Plugin key " + pluginKey + " is already in use");
        }
        this.pluginMap.put(pluginKey, null);
        ShimRegistrar registrar = new ShimRegistrar(pluginKey, this.pluginMap);
        this.shimRegistrarAggregate.addPlugin(registrar);
        return registrar;
    }

    @Override // io.flutter.plugin.common.PluginRegistry
    public boolean hasPlugin(String pluginKey) {
        return this.pluginMap.containsKey(pluginKey);
    }

    @Override // io.flutter.plugin.common.PluginRegistry
    public <T> T valuePublishedByPlugin(String pluginKey) {
        return (T) this.pluginMap.get(pluginKey);
    }

    /* loaded from: classes.dex */
    private static class ShimRegistrarAggregate implements FlutterPlugin, ActivityAware {
        private ActivityPluginBinding activityPluginBinding;
        private FlutterPlugin.FlutterPluginBinding flutterPluginBinding;
        private final Set<ShimRegistrar> shimRegistrars;

        private ShimRegistrarAggregate() {
            this.shimRegistrars = new HashSet();
        }

        public void addPlugin(ShimRegistrar shimRegistrar) {
            this.shimRegistrars.add(shimRegistrar);
            FlutterPlugin.FlutterPluginBinding flutterPluginBinding = this.flutterPluginBinding;
            if (flutterPluginBinding != null) {
                shimRegistrar.onAttachedToEngine(flutterPluginBinding);
            }
            ActivityPluginBinding activityPluginBinding = this.activityPluginBinding;
            if (activityPluginBinding != null) {
                shimRegistrar.onAttachedToActivity(activityPluginBinding);
            }
        }

        @Override // io.flutter.embedding.engine.plugins.FlutterPlugin
        public void onAttachedToEngine(FlutterPlugin.FlutterPluginBinding binding) {
            this.flutterPluginBinding = binding;
            for (ShimRegistrar shimRegistrar : this.shimRegistrars) {
                shimRegistrar.onAttachedToEngine(binding);
            }
        }

        @Override // io.flutter.embedding.engine.plugins.FlutterPlugin
        public void onDetachedFromEngine(FlutterPlugin.FlutterPluginBinding binding) {
            for (ShimRegistrar shimRegistrar : this.shimRegistrars) {
                shimRegistrar.onDetachedFromEngine(binding);
            }
            this.flutterPluginBinding = null;
            this.activityPluginBinding = null;
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityAware
        public void onAttachedToActivity(ActivityPluginBinding binding) {
            this.activityPluginBinding = binding;
            for (ShimRegistrar shimRegistrar : this.shimRegistrars) {
                shimRegistrar.onAttachedToActivity(binding);
            }
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityAware
        public void onDetachedFromActivityForConfigChanges() {
            for (ShimRegistrar shimRegistrar : this.shimRegistrars) {
                shimRegistrar.onDetachedFromActivity();
            }
            this.activityPluginBinding = null;
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityAware
        public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
            this.activityPluginBinding = binding;
            for (ShimRegistrar shimRegistrar : this.shimRegistrars) {
                shimRegistrar.onReattachedToActivityForConfigChanges(binding);
            }
        }

        @Override // io.flutter.embedding.engine.plugins.activity.ActivityAware
        public void onDetachedFromActivity() {
            for (ShimRegistrar shimRegistrar : this.shimRegistrars) {
                shimRegistrar.onDetachedFromActivity();
            }
            this.activityPluginBinding = null;
        }
    }
}
