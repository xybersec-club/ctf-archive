package io.flutter.plugin.common;

import io.flutter.Log;
import io.flutter.plugin.common.StandardMessageCodec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/* loaded from: classes.dex */
public final class StandardMethodCodec implements MethodCodec {
    public static final StandardMethodCodec INSTANCE = new StandardMethodCodec(StandardMessageCodec.INSTANCE);
    private final StandardMessageCodec messageCodec;

    public StandardMethodCodec(StandardMessageCodec messageCodec) {
        this.messageCodec = messageCodec;
    }

    @Override // io.flutter.plugin.common.MethodCodec
    public ByteBuffer encodeMethodCall(MethodCall methodCall) {
        StandardMessageCodec.ExposedByteArrayOutputStream stream = new StandardMessageCodec.ExposedByteArrayOutputStream();
        this.messageCodec.writeValue(stream, methodCall.method);
        this.messageCodec.writeValue(stream, methodCall.arguments);
        ByteBuffer buffer = ByteBuffer.allocateDirect(stream.size());
        buffer.put(stream.buffer(), 0, stream.size());
        return buffer;
    }

    @Override // io.flutter.plugin.common.MethodCodec
    public MethodCall decodeMethodCall(ByteBuffer methodCall) {
        methodCall.order(ByteOrder.nativeOrder());
        Object method = this.messageCodec.readValue(methodCall);
        Object arguments = this.messageCodec.readValue(methodCall);
        if ((method instanceof String) && !methodCall.hasRemaining()) {
            return new MethodCall((String) method, arguments);
        }
        throw new IllegalArgumentException("Method call corrupted");
    }

    @Override // io.flutter.plugin.common.MethodCodec
    public ByteBuffer encodeSuccessEnvelope(Object result) {
        StandardMessageCodec.ExposedByteArrayOutputStream stream = new StandardMessageCodec.ExposedByteArrayOutputStream();
        stream.write(0);
        this.messageCodec.writeValue(stream, result);
        ByteBuffer buffer = ByteBuffer.allocateDirect(stream.size());
        buffer.put(stream.buffer(), 0, stream.size());
        return buffer;
    }

    @Override // io.flutter.plugin.common.MethodCodec
    public ByteBuffer encodeErrorEnvelope(String errorCode, String errorMessage, Object errorDetails) {
        StandardMessageCodec.ExposedByteArrayOutputStream stream = new StandardMessageCodec.ExposedByteArrayOutputStream();
        stream.write(1);
        this.messageCodec.writeValue(stream, errorCode);
        this.messageCodec.writeValue(stream, errorMessage);
        if (errorDetails instanceof Throwable) {
            this.messageCodec.writeValue(stream, Log.getStackTraceString((Throwable) errorDetails));
        } else {
            this.messageCodec.writeValue(stream, errorDetails);
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(stream.size());
        buffer.put(stream.buffer(), 0, stream.size());
        return buffer;
    }

    @Override // io.flutter.plugin.common.MethodCodec
    public ByteBuffer encodeErrorEnvelopeWithStacktrace(String errorCode, String errorMessage, Object errorDetails, String errorStacktrace) {
        StandardMessageCodec.ExposedByteArrayOutputStream stream = new StandardMessageCodec.ExposedByteArrayOutputStream();
        stream.write(1);
        this.messageCodec.writeValue(stream, errorCode);
        this.messageCodec.writeValue(stream, errorMessage);
        if (errorDetails instanceof Throwable) {
            this.messageCodec.writeValue(stream, Log.getStackTraceString((Throwable) errorDetails));
        } else {
            this.messageCodec.writeValue(stream, errorDetails);
        }
        this.messageCodec.writeValue(stream, errorStacktrace);
        ByteBuffer buffer = ByteBuffer.allocateDirect(stream.size());
        buffer.put(stream.buffer(), 0, stream.size());
        return buffer;
    }

    @Override // io.flutter.plugin.common.MethodCodec
    public Object decodeEnvelope(ByteBuffer envelope) {
        Object code;
        Object message;
        Object details;
        envelope.order(ByteOrder.nativeOrder());
        byte flag = envelope.get();
        switch (flag) {
            case 0:
                Object result = this.messageCodec.readValue(envelope);
                if (!envelope.hasRemaining()) {
                    return result;
                }
                code = this.messageCodec.readValue(envelope);
                message = this.messageCodec.readValue(envelope);
                details = this.messageCodec.readValue(envelope);
                if ((code instanceof String) && ((message == null || (message instanceof String)) && !envelope.hasRemaining())) {
                    throw new FlutterException((String) code, (String) message, details);
                }
                throw new IllegalArgumentException("Envelope corrupted");
            case 1:
                code = this.messageCodec.readValue(envelope);
                message = this.messageCodec.readValue(envelope);
                details = this.messageCodec.readValue(envelope);
                if (code instanceof String) {
                    throw new FlutterException((String) code, (String) message, details);
                }
                throw new IllegalArgumentException("Envelope corrupted");
            default:
                throw new IllegalArgumentException("Envelope corrupted");
        }
    }
}
