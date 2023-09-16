package io.flutter.plugin.common;

import java.nio.ByteBuffer;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
/* loaded from: classes.dex */
public final class JSONMessageCodec implements MessageCodec<Object> {
    public static final JSONMessageCodec INSTANCE = new JSONMessageCodec();

    private JSONMessageCodec() {
    }

    @Override // io.flutter.plugin.common.MessageCodec
    public ByteBuffer encodeMessage(Object message) {
        if (message == null) {
            return null;
        }
        Object wrapped = JSONUtil.wrap(message);
        if (wrapped instanceof String) {
            return StringCodec.INSTANCE.encodeMessage(JSONObject.quote((String) wrapped));
        }
        return StringCodec.INSTANCE.encodeMessage(wrapped.toString());
    }

    @Override // io.flutter.plugin.common.MessageCodec
    public Object decodeMessage(ByteBuffer message) {
        if (message == null) {
            return null;
        }
        try {
            String json = StringCodec.INSTANCE.decodeMessage(message);
            JSONTokener tokener = new JSONTokener(json);
            Object value = tokener.nextValue();
            if (tokener.more()) {
                throw new IllegalArgumentException("Invalid JSON");
            }
            return value;
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid JSON", e);
        }
    }
}
