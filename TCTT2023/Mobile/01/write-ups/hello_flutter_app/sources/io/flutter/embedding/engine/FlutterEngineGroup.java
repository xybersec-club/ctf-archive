package io.flutter.embedding.engine;

import android.content.Context;
import io.flutter.FlutterInjector;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.loader.FlutterLoader;
import io.flutter.plugin.platform.PlatformViewsController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class FlutterEngineGroup {
    final List<FlutterEngine> activeEngines;

    public FlutterEngineGroup(Context context) {
        this(context, null);
    }

    public FlutterEngineGroup(Context context, String[] dartVmArgs) {
        this.activeEngines = new ArrayList();
        FlutterLoader loader = FlutterInjector.instance().flutterLoader();
        if (!loader.initialized()) {
            loader.startInitialization(context.getApplicationContext());
            loader.ensureInitializationComplete(context.getApplicationContext(), dartVmArgs);
        }
    }

    public FlutterEngine createAndRunDefaultEngine(Context context) {
        return createAndRunEngine(context, null);
    }

    public FlutterEngine createAndRunEngine(Context context, DartExecutor.DartEntrypoint dartEntrypoint) {
        return createAndRunEngine(context, dartEntrypoint, null);
    }

    public FlutterEngine createAndRunEngine(Context context, DartExecutor.DartEntrypoint dartEntrypoint, String initialRoute) {
        return createAndRunEngine(new Options(context).setDartEntrypoint(dartEntrypoint).setInitialRoute(initialRoute));
    }

    public FlutterEngine createAndRunEngine(Options options) {
        DartExecutor.DartEntrypoint dartEntrypoint;
        FlutterEngine engine;
        Context context = options.getContext();
        DartExecutor.DartEntrypoint dartEntrypoint2 = options.getDartEntrypoint();
        String initialRoute = options.getInitialRoute();
        List<String> dartEntrypointArgs = options.getDartEntrypointArgs();
        PlatformViewsController platformViewsController = options.getPlatformViewsController();
        PlatformViewsController platformViewsController2 = platformViewsController != null ? platformViewsController : new PlatformViewsController();
        boolean automaticallyRegisterPlugins = options.getAutomaticallyRegisterPlugins();
        boolean waitForRestorationData = options.getWaitForRestorationData();
        if (dartEntrypoint2 != null) {
            dartEntrypoint = dartEntrypoint2;
        } else {
            dartEntrypoint = DartExecutor.DartEntrypoint.createDefault();
        }
        if (this.activeEngines.size() == 0) {
            engine = createEngine(context, platformViewsController2, automaticallyRegisterPlugins, waitForRestorationData);
            if (initialRoute != null) {
                engine.getNavigationChannel().setInitialRoute(initialRoute);
            }
            engine.getDartExecutor().executeDartEntrypoint(dartEntrypoint, dartEntrypointArgs);
        } else {
            engine = this.activeEngines.get(0).spawn(context, dartEntrypoint, initialRoute, dartEntrypointArgs, platformViewsController2, automaticallyRegisterPlugins, waitForRestorationData);
        }
        this.activeEngines.add(engine);
        final FlutterEngine engineToCleanUpOnDestroy = engine;
        engine.addEngineLifecycleListener(new FlutterEngine.EngineLifecycleListener() { // from class: io.flutter.embedding.engine.FlutterEngineGroup.1
            @Override // io.flutter.embedding.engine.FlutterEngine.EngineLifecycleListener
            public void onPreEngineRestart() {
            }

            @Override // io.flutter.embedding.engine.FlutterEngine.EngineLifecycleListener
            public void onEngineWillDestroy() {
                FlutterEngineGroup.this.activeEngines.remove(engineToCleanUpOnDestroy);
            }
        });
        return engine;
    }

    FlutterEngine createEngine(Context context, PlatformViewsController platformViewsController, boolean automaticallyRegisterPlugins, boolean waitForRestorationData) {
        return new FlutterEngine(context, null, null, platformViewsController, null, automaticallyRegisterPlugins, waitForRestorationData, this);
    }

    /* loaded from: classes.dex */
    public static class Options {
        private Context context;
        private DartExecutor.DartEntrypoint dartEntrypoint;
        private List<String> dartEntrypointArgs;
        private String initialRoute;
        private PlatformViewsController platformViewsController;
        private boolean automaticallyRegisterPlugins = true;
        private boolean waitForRestorationData = false;

        public Options(Context context) {
            this.context = context;
        }

        public Context getContext() {
            return this.context;
        }

        public DartExecutor.DartEntrypoint getDartEntrypoint() {
            return this.dartEntrypoint;
        }

        public String getInitialRoute() {
            return this.initialRoute;
        }

        public List<String> getDartEntrypointArgs() {
            return this.dartEntrypointArgs;
        }

        public PlatformViewsController getPlatformViewsController() {
            return this.platformViewsController;
        }

        public boolean getAutomaticallyRegisterPlugins() {
            return this.automaticallyRegisterPlugins;
        }

        public boolean getWaitForRestorationData() {
            return this.waitForRestorationData;
        }

        public Options setDartEntrypoint(DartExecutor.DartEntrypoint dartEntrypoint) {
            this.dartEntrypoint = dartEntrypoint;
            return this;
        }

        public Options setInitialRoute(String initialRoute) {
            this.initialRoute = initialRoute;
            return this;
        }

        public Options setDartEntrypointArgs(List<String> dartEntrypointArgs) {
            this.dartEntrypointArgs = dartEntrypointArgs;
            return this;
        }

        public Options setPlatformViewsController(PlatformViewsController platformViewsController) {
            this.platformViewsController = platformViewsController;
            return this;
        }

        public Options setAutomaticallyRegisterPlugins(boolean automaticallyRegisterPlugins) {
            this.automaticallyRegisterPlugins = automaticallyRegisterPlugins;
            return this;
        }

        public Options setWaitForRestorationData(boolean waitForRestorationData) {
            this.waitForRestorationData = waitForRestorationData;
            return this;
        }
    }
}
