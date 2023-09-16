package io.flutter.plugin.common;

import io.flutter.Log;
import io.flutter.plugin.common.BinaryMessenger;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public final class EventChannel {
    private static final String TAG = "EventChannel#";
    private final MethodCodec codec;
    private final BinaryMessenger messenger;
    private final String name;
    private final BinaryMessenger.TaskQueue taskQueue;

    /* loaded from: classes.dex */
    public interface EventSink {
        void endOfStream();

        void error(String str, String str2, Object obj);

        void success(Object obj);
    }

    /* loaded from: classes.dex */
    public interface StreamHandler {
        void onCancel(Object obj);

        void onListen(Object obj, EventSink eventSink);
    }

    public EventChannel(BinaryMessenger messenger, String name) {
        this(messenger, name, StandardMethodCodec.INSTANCE);
    }

    public EventChannel(BinaryMessenger messenger, String name, MethodCodec codec) {
        this(messenger, name, codec, null);
    }

    public EventChannel(BinaryMessenger messenger, String name, MethodCodec codec, BinaryMessenger.TaskQueue taskQueue) {
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

    public void setStreamHandler(StreamHandler handler) {
        if (this.taskQueue != null) {
            this.messenger.setMessageHandler(this.name, handler != null ? new IncomingStreamRequestHandler(handler) : null, this.taskQueue);
        } else {
            this.messenger.setMessageHandler(this.name, handler != null ? new IncomingStreamRequestHandler(handler) : null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class IncomingStreamRequestHandler implements BinaryMessenger.BinaryMessageHandler {
        private final AtomicReference<EventSink> activeSink = new AtomicReference<>(null);
        private final StreamHandler handler;

        IncomingStreamRequestHandler(StreamHandler handler) {
            this.handler = handler;
        }

        @Override // io.flutter.plugin.common.BinaryMessenger.BinaryMessageHandler
        public void onMessage(ByteBuffer message, BinaryMessenger.BinaryReply reply) {
            MethodCall call = EventChannel.this.codec.decodeMethodCall(message);
            if (call.method.equals("listen")) {
                onListen(call.arguments, reply);
            } else if (call.method.equals("cancel")) {
                onCancel(call.arguments, reply);
            } else {
                reply.reply(null);
            }
        }

        private void onListen(Object arguments, BinaryMessenger.BinaryReply callback) {
            EventSink eventSink = new EventSinkImplementation();
            EventSink oldSink = this.activeSink.getAndSet(eventSink);
            if (oldSink != null) {
                try {
                    this.handler.onCancel(null);
                } catch (RuntimeException e) {
                    Log.e(EventChannel.TAG + EventChannel.this.name, "Failed to close existing event stream", e);
                }
            }
            try {
                this.handler.onListen(arguments, eventSink);
                callback.reply(EventChannel.this.codec.encodeSuccessEnvelope(null));
            } catch (RuntimeException e2) {
                this.activeSink.set(null);
                Log.e(EventChannel.TAG + EventChannel.this.name, "Failed to open event stream", e2);
                callback.reply(EventChannel.this.codec.encodeErrorEnvelope("error", e2.getMessage(), null));
            }
        }

        private void onCancel(Object arguments, BinaryMessenger.BinaryReply callback) {
            EventSink oldSink = this.activeSink.getAndSet(null);
            if (oldSink == null) {
                callback.reply(EventChannel.this.codec.encodeErrorEnvelope("error", "No active stream to cancel", null));
                return;
            }
            try {
                this.handler.onCancel(arguments);
                callback.reply(EventChannel.this.codec.encodeSuccessEnvelope(null));
            } catch (RuntimeException e) {
                Log.e(EventChannel.TAG + EventChannel.this.name, "Failed to close event stream", e);
                callback.reply(EventChannel.this.codec.encodeErrorEnvelope("error", e.getMessage(), null));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public final class EventSinkImplementation implements EventSink {
            final AtomicBoolean hasEnded;

            private EventSinkImplementation() {
                this.hasEnded = new AtomicBoolean(false);
            }

            @Override // io.flutter.plugin.common.EventChannel.EventSink
            public void success(Object event) {
                if (!this.hasEnded.get() && IncomingStreamRequestHandler.this.activeSink.get() == this) {
                    EventChannel.this.messenger.send(EventChannel.this.name, EventChannel.this.codec.encodeSuccessEnvelope(event));
                }
            }

            @Override // io.flutter.plugin.common.EventChannel.EventSink
            public void error(String errorCode, String errorMessage, Object errorDetails) {
                if (this.hasEnded.get() || IncomingStreamRequestHandler.this.activeSink.get() != this) {
                    return;
                }
                EventChannel.this.messenger.send(EventChannel.this.name, EventChannel.this.codec.encodeErrorEnvelope(errorCode, errorMessage, errorDetails));
            }

            @Override // io.flutter.plugin.common.EventChannel.EventSink
            public void endOfStream() {
                if (!this.hasEnded.getAndSet(true) && IncomingStreamRequestHandler.this.activeSink.get() == this) {
                    EventChannel.this.messenger.send(EventChannel.this.name, null);
                }
            }
        }
    }
}
