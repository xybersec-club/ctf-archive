package io.flutter.plugin.common;

import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public final class BinaryCodec implements MessageCodec<ByteBuffer> {
    public static final BinaryCodec INSTANCE = new BinaryCodec();
    public static final BinaryCodec INSTANCE_DIRECT = new BinaryCodec(true);
    private final boolean returnsDirectByteBufferFromDecoding;

    private BinaryCodec() {
        this.returnsDirectByteBufferFromDecoding = false;
    }

    private BinaryCodec(boolean returnsDirectByteBufferFromDecoding) {
        this.returnsDirectByteBufferFromDecoding = returnsDirectByteBufferFromDecoding;
    }

    @Override // io.flutter.plugin.common.MessageCodec
    public ByteBuffer encodeMessage(ByteBuffer message) {
        return message;
    }

    @Override // io.flutter.plugin.common.MessageCodec
    public ByteBuffer decodeMessage(ByteBuffer message) {
        if (message == null) {
            return message;
        }
        if (this.returnsDirectByteBufferFromDecoding) {
            return message;
        }
        ByteBuffer result = ByteBuffer.allocate(message.capacity());
        result.put(message);
        result.rewind();
        return result;
    }
}
