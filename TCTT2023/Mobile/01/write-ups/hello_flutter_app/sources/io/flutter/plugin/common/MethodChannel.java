package io.flutter.plugin.common;

import io.flutter.Log;
import io.flutter.plugin.common.BinaryMessenger;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class MethodChannel {
    private static final String TAG = "MethodChannel#";
    private final MethodCodec codec;
    private final BinaryMessenger messenger;
    private final String name;
    private final BinaryMessenger.TaskQueue taskQueue;

    /* loaded from: classes.dex */
    public interface MethodCallHandler {
        void onMethodCall(MethodCall methodCall, Result result);
    }

    /* loaded from: classes.dex */
    public interface Result {
        void error(String str, String str2, Object obj);

        void notImplemented();

        void success(Object obj);
    }

    public MethodChannel(BinaryMessenger messenger, String name) {
        this(messenger, name, StandardMethodCodec.INSTANCE);
    }

    public MethodChannel(BinaryMessenger messenger, String name, MethodCodec codec) {
        this(messenger, name, codec, null);
    }

    public MethodChannel(BinaryMessenger messenger, String name, MethodCodec codec, BinaryMessenger.TaskQueue taskQueue) {
        if (messenger == null) {
            Log.e(TAG, "Parameter messenger must not be null.");
        }
        if (name == null) {
            Log.e(TAG, "Parameter name must not be null.");
        }
        if (codec == null) {
            Log.e(TAG, "Parameter codec must not be null.");
        }
        this.messenger = messenger;
        this.name = name;
        this.codec = codec;
        this.taskQueue = taskQueue;
    }

    public void invokeMethod(String method, Object arguments) {
        invokeMethod(method, arguments, null);
    }

    public void invokeMethod(String method, Object arguments, Result callback) {
        this.messenger.send(this.name, this.codec.encodeMethodCall(new MethodCall(method, arguments)), callback == null ? null : new IncomingResultHandler(callback));
    }

    public void setMethodCallHandler(MethodCallHandler handler) {
        if (this.taskQueue != null) {
            this.messenger.setMessageHandler(this.name, handler != null ? new IncomingMethodCallHandler(handler) : null, this.taskQueue);
        } else {
            this.messenger.setMessageHandler(this.name, handler != null ? new IncomingMethodCallHandler(handler) : null);
        }
    }

    public void resizeChannelBuffer(int newSize) {
        BasicMessageChannel.resizeChannelBuffer(this.messenger, this.name, newSize);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class IncomingResultHandler implements BinaryMessenger.BinaryReply {
        private final Result callback;

        IncomingResultHandler(Result callback) {
            this.callback = callback;
        }

        @Override // io.flutter.plugin.common.BinaryMessenger.BinaryReply
        public void reply(ByteBuffer reply) {
            try {
                if (reply != null) {
                    try {
                        this.callback.success(MethodChannel.this.codec.decodeEnvelope(reply));
                    } catch (FlutterException e) {
                        this.callback.error(e.code, e.getMessage(), e.details);
                    }
                } else {
                    this.callback.notImplemented();
                }
            } catch (RuntimeException e2) {
                Log.e(MethodChannel.TAG + MethodChannel.this.name, "Failed to handle method call result", e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class IncomingMethodCallHandler implements BinaryMessenger.BinaryMessageHandler {
        private final MethodCallHandler handler;

        IncomingMethodCallHandler(MethodCallHandler handler) {
            this.handler = handler;
        }

        @Override // io.flutter.plugin.common.BinaryMessenger.BinaryMessageHandler
        public void onMessage(ByteBuffer message, final BinaryMessenger.BinaryReply reply) {
            MethodCall call = MethodChannel.this.codec.decodeMethodCall(message);
            try {
                this.handler.onMethodCall(call, new Result() { // from class: io.flutter.plugin.common.MethodChannel.IncomingMethodCallHandler.1
                    @Override // io.flutter.plugin.common.MethodChannel.Result
                    public void success(Object result) {
                        reply.reply(MethodChannel.this.codec.encodeSuccessEnvelope(result));
                    }

                    @Override // io.flutter.plugin.common.MethodChannel.Result
                    public void error(String errorCode, String errorMessage, Object errorDetails) {
                        reply.reply(MethodChannel.this.codec.encodeErrorEnvelope(errorCode, errorMessage, errorDetails));
                    }

                    @Override // io.flutter.plugin.common.MethodChannel.Result
                    public void notImplemented() {
                        reply.reply(null);
                    }
                });
            } catch (RuntimeException e) {
                Log.e(MethodChannel.TAG + MethodChannel.this.name, "Failed to handle method call", e);
                reply.reply(MethodChannel.this.codec.encodeErrorEnvelopeWithStacktrace("error", e.getMessage(), null, Log.getStackTraceString(e)));
            }
        }
    }
}
