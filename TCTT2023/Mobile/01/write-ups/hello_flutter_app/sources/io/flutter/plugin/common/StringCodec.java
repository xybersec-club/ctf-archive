package io.flutter.plugin.common;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
/* loaded from: classes.dex */
public final class StringCodec implements MessageCodec<String> {
    private static final Charset UTF8 = Charset.forName("UTF8");
    public static final StringCodec INSTANCE = new StringCodec();

    private StringCodec() {
    }

    @Override // io.flutter.plugin.common.MessageCodec
    public ByteBuffer encodeMessage(String message) {
        if (message == null) {
            return null;
        }
        byte[] bytes = message.getBytes(UTF8);
        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
        buffer.put(bytes);
        return buffer;
    }

    @Override // io.flutter.plugin.common.MessageCodec
    public String decodeMessage(ByteBuffer message) {
        byte[] bytes;
        int offset;
        if (message == null) {
            return null;
        }
        int length = message.remaining();
        if (message.hasArray()) {
            bytes = message.array();
            offset = message.arrayOffset();
        } else {
            bytes = new byte[length];
            message.get(bytes);
            offset = 0;
        }
        return new String(bytes, offset, length, UTF8);
    }
}
