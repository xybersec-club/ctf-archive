package io.flutter.embedding.engine.systemchannels;

import io.flutter.Log;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMethodCodec;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class SpellCheckChannel {
    private static final String TAG = "SpellCheckChannel";
    public final MethodChannel channel;
    public final MethodChannel.MethodCallHandler parsingMethodHandler;
    private SpellCheckMethodHandler spellCheckMethodHandler;

    /* loaded from: classes.dex */
    public interface SpellCheckMethodHandler {
        void initiateSpellCheck(String str, String str2, MethodChannel.Result result);
    }

    public SpellCheckChannel(DartExecutor dartExecutor) {
        MethodChannel.MethodCallHandler methodCallHandler = new MethodChannel.MethodCallHandler() { // from class: io.flutter.embedding.engine.systemchannels.SpellCheckChannel.1
            @Override // io.flutter.plugin.common.MethodChannel.MethodCallHandler
            public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                if (SpellCheckChannel.this.spellCheckMethodHandler == null) {
                    Log.v(SpellCheckChannel.TAG, "No SpellCheckeMethodHandler registered, call not forwarded to spell check API.");
                    return;
                }
                String method = call.method;
                Object args = call.arguments;
                Log.v(SpellCheckChannel.TAG, "Received '" + method + "' message.");
                char c = 65535;
                switch (method.hashCode()) {
                    case -1754395641:
                        if (method.equals("SpellCheck.initiateSpellCheck")) {
                            c = 0;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        try {
                            ArrayList<String> argumentList = (ArrayList) args;
                            String locale = argumentList.get(0);
                            String text = argumentList.get(1);
                            SpellCheckChannel.this.spellCheckMethodHandler.initiateSpellCheck(locale, text, result);
                            return;
                        } catch (IllegalStateException exception) {
                            result.error("error", exception.getMessage(), null);
                            return;
                        }
                    default:
                        result.notImplemented();
                        return;
                }
            }
        };
        this.parsingMethodHandler = methodCallHandler;
        MethodChannel methodChannel = new MethodChannel(dartExecutor, "flutter/spellcheck", StandardMethodCodec.INSTANCE);
        this.channel = methodChannel;
        methodChannel.setMethodCallHandler(methodCallHandler);
    }

    public void setSpellCheckMethodHandler(SpellCheckMethodHandler spellCheckMethodHandler) {
        this.spellCheckMethodHandler = spellCheckMethodHandler;
    }
}
