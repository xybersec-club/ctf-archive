package io.flutter.embedding.engine.plugins.util;

import io.flutter.Log;
import io.flutter.embedding.engine.FlutterEngine;
import java.lang.reflect.Method;
/* loaded from: classes.dex */
public class GeneratedPluginRegister {
    private static final String TAG = "GeneratedPluginsRegister";

    public static void registerGeneratedPlugins(FlutterEngine flutterEngine) {
        try {
            Class<?> generatedPluginRegistrant = Class.forName("io.flutter.plugins.GeneratedPluginRegistrant");
            Method registrationMethod = generatedPluginRegistrant.getDeclaredMethod("registerWith", FlutterEngine.class);
            registrationMethod.invoke(null, flutterEngine);
        } catch (Exception e) {
            Log.e(TAG, "Tried to automatically register plugins with FlutterEngine (" + flutterEngine + ") but could not find or invoke the GeneratedPluginRegistrant.");
            Log.e(TAG, "Received exception while registering", e);
        }
    }
}
