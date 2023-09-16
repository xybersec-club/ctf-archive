package io.flutter.view;

import android.content.Context;
import android.os.Handler;
import io.flutter.FlutterInjector;
import io.flutter.embedding.engine.loader.FlutterLoader;
@Deprecated
/* loaded from: classes.dex */
public class FlutterMain {

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

    public static void startInitialization(Context applicationContext) {
        FlutterInjector.instance().flutterLoader().startInitialization(applicationContext);
    }

    public static void startInitialization(Context applicationContext, Settings settings) {
        FlutterLoader.Settings newSettings = new FlutterLoader.Settings();
        newSettings.setLogTag(settings.getLogTag());
        FlutterInjector.instance().flutterLoader().startInitialization(applicationContext, newSettings);
    }

    public static void ensureInitializationComplete(Context applicationContext, String[] args) {
        FlutterInjector.instance().flutterLoader().ensureInitializationComplete(applicationContext, args);
    }

    public static void ensureInitializationCompleteAsync(Context applicationContext, String[] args, Handler callbackHandler, Runnable callback) {
        FlutterInjector.instance().flutterLoader().ensureInitializationCompleteAsync(applicationContext, args, callbackHandler, callback);
    }

    public static String findAppBundlePath() {
        return FlutterInjector.instance().flutterLoader().findAppBundlePath();
    }

    @Deprecated
    public static String findAppBundlePath(Context applicationContext) {
        return FlutterInjector.instance().flutterLoader().findAppBundlePath();
    }

    public static String getLookupKeyForAsset(String asset) {
        return FlutterInjector.instance().flutterLoader().getLookupKeyForAsset(asset);
    }

    public static String getLookupKeyForAsset(String asset, String packageName) {
        return FlutterInjector.instance().flutterLoader().getLookupKeyForAsset(asset, packageName);
    }
}
