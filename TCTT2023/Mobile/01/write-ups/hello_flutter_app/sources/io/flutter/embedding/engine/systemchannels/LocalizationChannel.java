package io.flutter.embedding.engine.systemchannels;

import android.os.Build;
import io.flutter.Log;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.JSONMethodCodec;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class LocalizationChannel {
    private static final String TAG = "LocalizationChannel";
    public final MethodChannel channel;
    public final MethodChannel.MethodCallHandler handler;
    private LocalizationMessageHandler localizationMessageHandler;

    /* loaded from: classes.dex */
    public interface LocalizationMessageHandler {
        String getStringResource(String str, String str2);
    }

    public LocalizationChannel(DartExecutor dartExecutor) {
        MethodChannel.MethodCallHandler methodCallHandler = new MethodChannel.MethodCallHandler() { // from class: io.flutter.embedding.engine.systemchannels.LocalizationChannel.1
            @Override // io.flutter.plugin.common.MethodChannel.MethodCallHandler
            public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                if (LocalizationChannel.this.localizationMessageHandler == null) {
                    return;
                }
                String method = call.method;
                char c = 65535;
                switch (method.hashCode()) {
                    case -259484608:
                        if (method.equals("Localization.getStringResource")) {
                            c = 0;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        JSONObject arguments = (JSONObject) call.arguments();
                        try {
                            String key = arguments.getString("key");
                            String localeString = null;
                            if (arguments.has("locale")) {
                                localeString = arguments.getString("locale");
                            }
                            result.success(LocalizationChannel.this.localizationMessageHandler.getStringResource(key, localeString));
                            return;
                        } catch (JSONException exception) {
                            result.error("error", exception.getMessage(), null);
                            return;
                        }
                    default:
                        result.notImplemented();
                        return;
                }
            }
        };
        this.handler = methodCallHandler;
        MethodChannel methodChannel = new MethodChannel(dartExecutor, "flutter/localization", JSONMethodCodec.INSTANCE);
        this.channel = methodChannel;
        methodChannel.setMethodCallHandler(methodCallHandler);
    }

    public void setLocalizationMessageHandler(LocalizationMessageHandler localizationMessageHandler) {
        this.localizationMessageHandler = localizationMessageHandler;
    }

    public void sendLocales(List<Locale> locales) {
        Log.v(TAG, "Sending Locales to Flutter.");
        List<String> data = new ArrayList<>();
        for (Locale locale : locales) {
            Log.v(TAG, "Locale (Language: " + locale.getLanguage() + ", Country: " + locale.getCountry() + ", Variant: " + locale.getVariant() + ")");
            data.add(locale.getLanguage());
            data.add(locale.getCountry());
            data.add(Build.VERSION.SDK_INT >= 21 ? locale.getScript() : "");
            data.add(locale.getVariant());
        }
        this.channel.invokeMethod("setLocale", data);
    }
}
