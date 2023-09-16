package io.flutter.embedding.engine.systemchannels;

import io.flutter.FlutterInjector;
import io.flutter.Log;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.deferredcomponents.DeferredComponentManager;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMethodCodec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class DeferredComponentChannel {
    private static final String TAG = "DeferredComponentChannel";
    private final MethodChannel channel;
    private Map<String, List<MethodChannel.Result>> componentNameToResults;
    private DeferredComponentManager deferredComponentManager;
    final MethodChannel.MethodCallHandler parsingMethodHandler;

    public DeferredComponentChannel(DartExecutor dartExecutor) {
        MethodChannel.MethodCallHandler methodCallHandler = new MethodChannel.MethodCallHandler() { // from class: io.flutter.embedding.engine.systemchannels.DeferredComponentChannel.1
            @Override // io.flutter.plugin.common.MethodChannel.MethodCallHandler
            public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                if (DeferredComponentChannel.this.deferredComponentManager == null) {
                    return;
                }
                String method = call.method;
                Map<String, Object> args = (Map) call.arguments();
                Log.v(DeferredComponentChannel.TAG, "Received '" + method + "' message.");
                int loadingUnitId = ((Integer) args.get("loadingUnitId")).intValue();
                String componentName = (String) args.get("componentName");
                char c = 65535;
                switch (method.hashCode()) {
                    case -1004447972:
                        if (method.equals("uninstallDeferredComponent")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 399701758:
                        if (method.equals("getDeferredComponentInstallState")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 520962947:
                        if (method.equals("installDeferredComponent")) {
                            c = 0;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        DeferredComponentChannel.this.deferredComponentManager.installDeferredComponent(loadingUnitId, componentName);
                        if (!DeferredComponentChannel.this.componentNameToResults.containsKey(componentName)) {
                            DeferredComponentChannel.this.componentNameToResults.put(componentName, new ArrayList());
                        }
                        ((List) DeferredComponentChannel.this.componentNameToResults.get(componentName)).add(result);
                        return;
                    case 1:
                        result.success(DeferredComponentChannel.this.deferredComponentManager.getDeferredComponentInstallState(loadingUnitId, componentName));
                        return;
                    case 2:
                        DeferredComponentChannel.this.deferredComponentManager.uninstallDeferredComponent(loadingUnitId, componentName);
                        result.success(null);
                        return;
                    default:
                        result.notImplemented();
                        return;
                }
            }
        };
        this.parsingMethodHandler = methodCallHandler;
        MethodChannel methodChannel = new MethodChannel(dartExecutor, "flutter/deferredcomponent", StandardMethodCodec.INSTANCE);
        this.channel = methodChannel;
        methodChannel.setMethodCallHandler(methodCallHandler);
        this.deferredComponentManager = FlutterInjector.instance().deferredComponentManager();
        this.componentNameToResults = new HashMap();
    }

    public void setDeferredComponentManager(DeferredComponentManager deferredComponentManager) {
        this.deferredComponentManager = deferredComponentManager;
    }

    public void completeInstallSuccess(String componentName) {
        if (this.componentNameToResults.containsKey(componentName)) {
            for (MethodChannel.Result result : this.componentNameToResults.get(componentName)) {
                result.success(null);
            }
            this.componentNameToResults.get(componentName).clear();
        }
    }

    public void completeInstallError(String componentName, String errorMessage) {
        if (this.componentNameToResults.containsKey(componentName)) {
            for (MethodChannel.Result result : this.componentNameToResults.get(componentName)) {
                result.error("DeferredComponent Install failure", errorMessage, null);
            }
            this.componentNameToResults.get(componentName).clear();
        }
    }
}
