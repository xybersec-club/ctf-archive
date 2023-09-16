package io.flutter.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import io.flutter.Log;
import io.flutter.embedding.engine.FlutterShellArgs;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.platform.PlatformPlugin;
import io.flutter.util.Preconditions;
import io.flutter.view.FlutterMain;
import io.flutter.view.FlutterNativeView;
import io.flutter.view.FlutterRunArguments;
import io.flutter.view.FlutterView;
import java.util.ArrayList;
@Deprecated
/* loaded from: classes.dex */
public final class FlutterActivityDelegate implements FlutterActivityEvents, FlutterView.Provider, PluginRegistry {
    private static final String SPLASH_SCREEN_META_DATA_KEY = "io.flutter.app.android.SplashScreenUntilFirstFrame";
    private static final String TAG = "FlutterActivityDelegate";
    private static final WindowManager.LayoutParams matchParent = new WindowManager.LayoutParams(-1, -1);
    private final Activity activity;
    private FlutterView flutterView;
    private View launchView;
    private final ViewFactory viewFactory;

    /* loaded from: classes.dex */
    public interface ViewFactory {
        FlutterNativeView createFlutterNativeView();

        FlutterView createFlutterView(Context context);

        boolean retainFlutterNativeView();
    }

    public FlutterActivityDelegate(Activity activity, ViewFactory viewFactory) {
        this.activity = (Activity) Preconditions.checkNotNull(activity);
        this.viewFactory = (ViewFactory) Preconditions.checkNotNull(viewFactory);
    }

    @Override // io.flutter.view.FlutterView.Provider
    public FlutterView getFlutterView() {
        return this.flutterView;
    }

    @Override // io.flutter.plugin.common.PluginRegistry
    public boolean hasPlugin(String key) {
        return this.flutterView.getPluginRegistry().hasPlugin(key);
    }

    @Override // io.flutter.plugin.common.PluginRegistry
    public <T> T valuePublishedByPlugin(String pluginKey) {
        return (T) this.flutterView.getPluginRegistry().valuePublishedByPlugin(pluginKey);
    }

    @Override // io.flutter.plugin.common.PluginRegistry
    public PluginRegistry.Registrar registrarFor(String pluginKey) {
        return this.flutterView.getPluginRegistry().registrarFor(pluginKey);
    }

    @Override // io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        return this.flutterView.getPluginRegistry().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override // io.flutter.plugin.common.PluginRegistry.ActivityResultListener
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return this.flutterView.getPluginRegistry().onActivityResult(requestCode, resultCode, data);
    }

    @Override // io.flutter.app.FlutterActivityEvents
    public void onCreate(Bundle savedInstanceState) {
        String appBundlePath;
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.activity.getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(1073741824);
            window.getDecorView().setSystemUiVisibility(PlatformPlugin.DEFAULT_SYSTEM_UI);
        }
        String[] args = getArgsFromIntent(this.activity.getIntent());
        FlutterMain.ensureInitializationComplete(this.activity.getApplicationContext(), args);
        FlutterView createFlutterView = this.viewFactory.createFlutterView(this.activity);
        this.flutterView = createFlutterView;
        if (createFlutterView == null) {
            FlutterNativeView nativeView = this.viewFactory.createFlutterNativeView();
            FlutterView flutterView = new FlutterView(this.activity, null, nativeView);
            this.flutterView = flutterView;
            flutterView.setLayoutParams(matchParent);
            this.activity.setContentView(this.flutterView);
            View createLaunchView = createLaunchView();
            this.launchView = createLaunchView;
            if (createLaunchView != null) {
                addLaunchView();
            }
        }
        if (!loadIntent(this.activity.getIntent()) && (appBundlePath = FlutterMain.findAppBundlePath()) != null) {
            runBundle(appBundlePath);
        }
    }

    @Override // io.flutter.app.FlutterActivityEvents
    public void onNewIntent(Intent intent) {
        if (!isDebuggable() || !loadIntent(intent)) {
            this.flutterView.getPluginRegistry().onNewIntent(intent);
        }
    }

    private boolean isDebuggable() {
        return (this.activity.getApplicationInfo().flags & 2) != 0;
    }

    @Override // io.flutter.app.FlutterActivityEvents
    public void onPause() {
        Application app = (Application) this.activity.getApplicationContext();
        if (app instanceof FlutterApplication) {
            FlutterApplication flutterApp = (FlutterApplication) app;
            if (this.activity.equals(flutterApp.getCurrentActivity())) {
                flutterApp.setCurrentActivity(null);
            }
        }
        FlutterView flutterView = this.flutterView;
        if (flutterView != null) {
            flutterView.onPause();
        }
    }

    @Override // io.flutter.app.FlutterActivityEvents
    public void onStart() {
        FlutterView flutterView = this.flutterView;
        if (flutterView != null) {
            flutterView.onStart();
        }
    }

    @Override // io.flutter.app.FlutterActivityEvents
    public void onResume() {
        Application app = (Application) this.activity.getApplicationContext();
        if (app instanceof FlutterApplication) {
            FlutterApplication flutterApp = (FlutterApplication) app;
            flutterApp.setCurrentActivity(this.activity);
        }
    }

    @Override // io.flutter.app.FlutterActivityEvents
    public void onStop() {
        this.flutterView.onStop();
    }

    @Override // io.flutter.app.FlutterActivityEvents
    public void onPostResume() {
        FlutterView flutterView = this.flutterView;
        if (flutterView != null) {
            flutterView.onPostResume();
        }
    }

    @Override // io.flutter.app.FlutterActivityEvents
    public void onDestroy() {
        Application app = (Application) this.activity.getApplicationContext();
        if (app instanceof FlutterApplication) {
            FlutterApplication flutterApp = (FlutterApplication) app;
            if (this.activity.equals(flutterApp.getCurrentActivity())) {
                flutterApp.setCurrentActivity(null);
            }
        }
        FlutterView flutterView = this.flutterView;
        if (flutterView != null) {
            boolean detach = flutterView.getPluginRegistry().onViewDestroy(this.flutterView.getFlutterNativeView());
            if (detach || this.viewFactory.retainFlutterNativeView()) {
                this.flutterView.detach();
            } else {
                this.flutterView.destroy();
            }
        }
    }

    @Override // io.flutter.app.FlutterActivityEvents
    public boolean onBackPressed() {
        FlutterView flutterView = this.flutterView;
        if (flutterView != null) {
            flutterView.popRoute();
            return true;
        }
        return false;
    }

    @Override // io.flutter.app.FlutterActivityEvents
    public void onUserLeaveHint() {
        this.flutterView.getPluginRegistry().onUserLeaveHint();
    }

    @Override // android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        if (level == 10) {
            this.flutterView.onMemoryPressure();
        }
    }

    @Override // android.content.ComponentCallbacks
    public void onLowMemory() {
        this.flutterView.onMemoryPressure();
    }

    @Override // android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
    }

    private static String[] getArgsFromIntent(Intent intent) {
        ArrayList<String> args = new ArrayList<>();
        if (intent.getBooleanExtra(FlutterShellArgs.ARG_KEY_TRACE_STARTUP, false)) {
            args.add(FlutterShellArgs.ARG_TRACE_STARTUP);
        }
        if (intent.getBooleanExtra(FlutterShellArgs.ARG_KEY_START_PAUSED, false)) {
            args.add(FlutterShellArgs.ARG_START_PAUSED);
        }
        if (intent.getBooleanExtra(FlutterShellArgs.ARG_KEY_DISABLE_SERVICE_AUTH_CODES, false)) {
            args.add(FlutterShellArgs.ARG_DISABLE_SERVICE_AUTH_CODES);
        }
        if (intent.getBooleanExtra(FlutterShellArgs.ARG_KEY_USE_TEST_FONTS, false)) {
            args.add(FlutterShellArgs.ARG_USE_TEST_FONTS);
        }
        if (intent.getBooleanExtra(FlutterShellArgs.ARG_KEY_ENABLE_DART_PROFILING, false)) {
            args.add(FlutterShellArgs.ARG_ENABLE_DART_PROFILING);
        }
        if (intent.getBooleanExtra(FlutterShellArgs.ARG_KEY_ENABLE_SOFTWARE_RENDERING, false)) {
            args.add(FlutterShellArgs.ARG_ENABLE_SOFTWARE_RENDERING);
        }
        if (intent.getBooleanExtra(FlutterShellArgs.ARG_KEY_SKIA_DETERMINISTIC_RENDERING, false)) {
            args.add(FlutterShellArgs.ARG_SKIA_DETERMINISTIC_RENDERING);
        }
        if (intent.getBooleanExtra(FlutterShellArgs.ARG_KEY_TRACE_SKIA, false)) {
            args.add(FlutterShellArgs.ARG_TRACE_SKIA);
        }
        if (intent.getBooleanExtra(FlutterShellArgs.ARG_KEY_TRACE_SYSTRACE, false)) {
            args.add(FlutterShellArgs.ARG_TRACE_SYSTRACE);
        }
        if (intent.getBooleanExtra(FlutterShellArgs.ARG_KEY_DUMP_SHADER_SKP_ON_SHADER_COMPILATION, false)) {
            args.add(FlutterShellArgs.ARG_DUMP_SHADER_SKP_ON_SHADER_COMPILATION);
        }
        if (intent.getBooleanExtra(FlutterShellArgs.ARG_KEY_CACHE_SKSL, false)) {
            args.add(FlutterShellArgs.ARG_CACHE_SKSL);
        }
        if (intent.getBooleanExtra(FlutterShellArgs.ARG_KEY_PURGE_PERSISTENT_CACHE, false)) {
            args.add(FlutterShellArgs.ARG_PURGE_PERSISTENT_CACHE);
        }
        if (intent.getBooleanExtra(FlutterShellArgs.ARG_KEY_VERBOSE_LOGGING, false)) {
            args.add(FlutterShellArgs.ARG_VERBOSE_LOGGING);
        }
        int vmServicePort = intent.getIntExtra(FlutterShellArgs.ARG_KEY_VM_SERVICE_PORT, 0);
        if (vmServicePort > 0) {
            args.add(FlutterShellArgs.ARG_VM_SERVICE_PORT + Integer.toString(vmServicePort));
        } else {
            int vmServicePort2 = intent.getIntExtra(FlutterShellArgs.ARG_KEY_OBSERVATORY_PORT, 0);
            if (vmServicePort2 > 0) {
                args.add(FlutterShellArgs.ARG_VM_SERVICE_PORT + Integer.toString(vmServicePort2));
            }
        }
        if (intent.getBooleanExtra(FlutterShellArgs.ARG_KEY_ENDLESS_TRACE_BUFFER, false)) {
            args.add(FlutterShellArgs.ARG_ENDLESS_TRACE_BUFFER);
        }
        if (intent.hasExtra(FlutterShellArgs.ARG_KEY_DART_FLAGS)) {
            args.add("--dart-flags=" + intent.getStringExtra(FlutterShellArgs.ARG_KEY_DART_FLAGS));
        }
        if (!args.isEmpty()) {
            String[] argsArray = new String[args.size()];
            return (String[]) args.toArray(argsArray);
        }
        return null;
    }

    private boolean loadIntent(Intent intent) {
        String action = intent.getAction();
        if ("android.intent.action.RUN".equals(action)) {
            String route = intent.getStringExtra("route");
            String appBundlePath = intent.getDataString();
            if (appBundlePath == null) {
                appBundlePath = FlutterMain.findAppBundlePath();
            }
            if (route != null) {
                this.flutterView.setInitialRoute(route);
            }
            runBundle(appBundlePath);
            return true;
        }
        return false;
    }

    private void runBundle(String appBundlePath) {
        if (!this.flutterView.getFlutterNativeView().isApplicationRunning()) {
            FlutterRunArguments args = new FlutterRunArguments();
            args.bundlePath = appBundlePath;
            args.entrypoint = "main";
            this.flutterView.runFromBundle(args);
        }
    }

    private View createLaunchView() {
        Drawable launchScreenDrawable;
        if (showSplashScreenUntilFirstFrame().booleanValue() && (launchScreenDrawable = getLaunchScreenDrawableFromActivityTheme()) != null) {
            View view = new View(this.activity);
            view.setLayoutParams(matchParent);
            view.setBackground(launchScreenDrawable);
            return view;
        }
        return null;
    }

    private Drawable getLaunchScreenDrawableFromActivityTheme() {
        TypedValue typedValue = new TypedValue();
        if (this.activity.getTheme().resolveAttribute(16842836, typedValue, true) && typedValue.resourceId != 0) {
            try {
                return this.activity.getResources().getDrawable(typedValue.resourceId);
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Referenced launch screen windowBackground resource does not exist");
                return null;
            }
        }
        return null;
    }

    private Boolean showSplashScreenUntilFirstFrame() {
        try {
            ActivityInfo activityInfo = this.activity.getPackageManager().getActivityInfo(this.activity.getComponentName(), 128);
            Bundle metadata = activityInfo.metaData;
            return Boolean.valueOf(metadata != null && metadata.getBoolean(SPLASH_SCREEN_META_DATA_KEY));
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void addLaunchView() {
        View view = this.launchView;
        if (view == null) {
            return;
        }
        this.activity.addContentView(view, matchParent);
        this.flutterView.addFirstFrameListener(new FlutterView.FirstFrameListener() { // from class: io.flutter.app.FlutterActivityDelegate.1
            @Override // io.flutter.view.FlutterView.FirstFrameListener
            public void onFirstFrame() {
                FlutterActivityDelegate.this.launchView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: io.flutter.app.FlutterActivityDelegate.1.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        ((ViewGroup) FlutterActivityDelegate.this.launchView.getParent()).removeView(FlutterActivityDelegate.this.launchView);
                        FlutterActivityDelegate.this.launchView = null;
                    }
                });
                FlutterActivityDelegate.this.flutterView.removeFirstFrameListener(this);
            }
        });
        this.activity.setTheme(16973833);
    }
}
