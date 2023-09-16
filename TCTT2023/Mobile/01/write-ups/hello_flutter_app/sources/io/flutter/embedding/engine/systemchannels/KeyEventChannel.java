package io.flutter.embedding.engine.systemchannels;

import android.view.KeyEvent;
import io.flutter.Log;
import io.flutter.embedding.engine.systemchannels.KeyEventChannel;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.JSONMessageCodec;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class KeyEventChannel {
    private static final String TAG = "KeyEventChannel";
    public final BasicMessageChannel<Object> channel;

    /* loaded from: classes.dex */
    public interface EventResponseHandler {
        void onFrameworkResponse(boolean z);
    }

    public KeyEventChannel(BinaryMessenger binaryMessenger) {
        this.channel = new BasicMessageChannel<>(binaryMessenger, "flutter/keyevent", JSONMessageCodec.INSTANCE);
    }

    public void sendFlutterKeyEvent(FlutterKeyEvent keyEvent, boolean isKeyUp, EventResponseHandler responseHandler) {
        this.channel.send(encodeKeyEvent(keyEvent, isKeyUp), createReplyHandler(responseHandler));
    }

    private Map<String, Object> encodeKeyEvent(FlutterKeyEvent keyEvent, boolean isKeyUp) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", isKeyUp ? "keyup" : "keydown");
        message.put("keymap", "android");
        message.put("flags", Integer.valueOf(keyEvent.event.getFlags()));
        message.put("plainCodePoint", Integer.valueOf(keyEvent.event.getUnicodeChar(0)));
        message.put("codePoint", Integer.valueOf(keyEvent.event.getUnicodeChar()));
        message.put("keyCode", Integer.valueOf(keyEvent.event.getKeyCode()));
        message.put("scanCode", Integer.valueOf(keyEvent.event.getScanCode()));
        message.put("metaState", Integer.valueOf(keyEvent.event.getMetaState()));
        if (keyEvent.complexCharacter != null) {
            message.put("character", keyEvent.complexCharacter.toString());
        }
        message.put("source", Integer.valueOf(keyEvent.event.getSource()));
        message.put("deviceId", Integer.valueOf(keyEvent.event.getDeviceId()));
        message.put("repeatCount", Integer.valueOf(keyEvent.event.getRepeatCount()));
        return message;
    }

    private static BasicMessageChannel.Reply<Object> createReplyHandler(final EventResponseHandler responseHandler) {
        return new BasicMessageChannel.Reply() { // from class: io.flutter.embedding.engine.systemchannels.KeyEventChannel$$ExternalSyntheticLambda0
            @Override // io.flutter.plugin.common.BasicMessageChannel.Reply
            public final void reply(Object obj) {
                KeyEventChannel.lambda$createReplyHandler$0(KeyEventChannel.EventResponseHandler.this, obj);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$createReplyHandler$0(EventResponseHandler responseHandler, Object message) {
        boolean isEventHandled = false;
        if (message != null) {
            try {
                JSONObject annotatedEvent = (JSONObject) message;
                isEventHandled = annotatedEvent.getBoolean("handled");
            } catch (JSONException e) {
                Log.e(TAG, "Unable to unpack JSON message: " + e);
            }
        }
        responseHandler.onFrameworkResponse(isEventHandled);
    }

    /* loaded from: classes.dex */
    public static class FlutterKeyEvent {
        public final Character complexCharacter;
        public final KeyEvent event;

        public FlutterKeyEvent(KeyEvent androidKeyEvent) {
            this(androidKeyEvent, null);
        }

        public FlutterKeyEvent(KeyEvent androidKeyEvent, Character complexCharacter) {
            this.event = androidKeyEvent;
            this.complexCharacter = complexCharacter;
        }
    }
}
