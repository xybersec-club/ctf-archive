package io.flutter.embedding.engine.dart;

import io.flutter.FlutterInjector;
import io.flutter.Log;
import io.flutter.embedding.engine.FlutterJNI;
import io.flutter.embedding.engine.dart.DartMessenger;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.util.TraceSection;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DartMessenger implements BinaryMessenger, PlatformMessageHandler {
    private static final String TAG = "DartMessenger";
    private Map<String, List<BufferedMessageInfo>> bufferedMessages;
    private WeakHashMap<BinaryMessenger.TaskQueue, DartMessengerTaskQueue> createdTaskQueues;
    private final AtomicBoolean enableBufferingIncomingMessages;
    private final FlutterJNI flutterJNI;
    private final Object handlersLock;
    private final Map<String, HandlerInfo> messageHandlers;
    private int nextReplyId;
    private final Map<Integer, BinaryMessenger.BinaryReply> pendingReplies;
    private final DartMessengerTaskQueue platformTaskQueue;
    private TaskQueueFactory taskQueueFactory;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface DartMessengerTaskQueue {
        void dispatch(Runnable runnable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface TaskQueueFactory {
        DartMessengerTaskQueue makeBackgroundTaskQueue(BinaryMessenger.TaskQueueOptions taskQueueOptions);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public /* synthetic */ BinaryMessenger.TaskQueue makeBackgroundTaskQueue() {
        BinaryMessenger.TaskQueue makeBackgroundTaskQueue;
        makeBackgroundTaskQueue = makeBackgroundTaskQueue(new BinaryMessenger.TaskQueueOptions());
        return makeBackgroundTaskQueue;
    }

    DartMessenger(FlutterJNI flutterJNI, TaskQueueFactory taskQueueFactory) {
        this.messageHandlers = new HashMap();
        this.bufferedMessages = new HashMap();
        this.handlersLock = new Object();
        this.enableBufferingIncomingMessages = new AtomicBoolean(false);
        this.pendingReplies = new HashMap();
        this.nextReplyId = 1;
        this.platformTaskQueue = new PlatformTaskQueue();
        this.createdTaskQueues = new WeakHashMap<>();
        this.flutterJNI = flutterJNI;
        this.taskQueueFactory = taskQueueFactory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DartMessenger(FlutterJNI flutterJNI) {
        this(flutterJNI, new DefaultTaskQueueFactory());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TaskQueueToken implements BinaryMessenger.TaskQueue {
        private TaskQueueToken() {
        }
    }

    /* loaded from: classes.dex */
    private static class DefaultTaskQueueFactory implements TaskQueueFactory {
        ExecutorService executorService = FlutterInjector.instance().executorService();

        DefaultTaskQueueFactory() {
        }

        @Override // io.flutter.embedding.engine.dart.DartMessenger.TaskQueueFactory
        public DartMessengerTaskQueue makeBackgroundTaskQueue(BinaryMessenger.TaskQueueOptions options) {
            if (options.getIsSerial()) {
                return new SerialTaskQueue(this.executorService);
            }
            return new ConcurrentTaskQueue(this.executorService);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class HandlerInfo {
        public final BinaryMessenger.BinaryMessageHandler handler;
        public final DartMessengerTaskQueue taskQueue;

        HandlerInfo(BinaryMessenger.BinaryMessageHandler handler, DartMessengerTaskQueue taskQueue) {
            this.handler = handler;
            this.taskQueue = taskQueue;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BufferedMessageInfo {
        public final ByteBuffer message;
        long messageData;
        int replyId;

        BufferedMessageInfo(ByteBuffer message, int replyId, long messageData) {
            this.message = message;
            this.replyId = replyId;
            this.messageData = messageData;
        }
    }

    /* loaded from: classes.dex */
    static class ConcurrentTaskQueue implements DartMessengerTaskQueue {
        private final ExecutorService executor;

        ConcurrentTaskQueue(ExecutorService executor) {
            this.executor = executor;
        }

        @Override // io.flutter.embedding.engine.dart.DartMessenger.DartMessengerTaskQueue
        public void dispatch(Runnable runnable) {
            this.executor.execute(runnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SerialTaskQueue implements DartMessengerTaskQueue {
        private final ExecutorService executor;
        private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
        private final AtomicBoolean isRunning = new AtomicBoolean(false);

        SerialTaskQueue(ExecutorService executor) {
            this.executor = executor;
        }

        @Override // io.flutter.embedding.engine.dart.DartMessenger.DartMessengerTaskQueue
        public void dispatch(Runnable runnable) {
            this.queue.add(runnable);
            this.executor.execute(new Runnable() { // from class: io.flutter.embedding.engine.dart.DartMessenger$SerialTaskQueue$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DartMessenger.SerialTaskQueue.this.m21xf9910f8();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: flush */
        public void m22xf60083a7() {
            if (this.isRunning.compareAndSet(false, true)) {
                try {
                    Runnable runnable = this.queue.poll();
                    if (runnable != null) {
                        runnable.run();
                    }
                } finally {
                    this.isRunning.set(false);
                    if (!this.queue.isEmpty()) {
                        this.executor.execute(new Runnable() { // from class: io.flutter.embedding.engine.dart.DartMessenger$SerialTaskQueue$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                DartMessenger.SerialTaskQueue.this.m22xf60083a7();
                            }
                        });
                    }
                }
            }
        }
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public BinaryMessenger.TaskQueue makeBackgroundTaskQueue(BinaryMessenger.TaskQueueOptions options) {
        DartMessengerTaskQueue taskQueue = this.taskQueueFactory.makeBackgroundTaskQueue(options);
        TaskQueueToken token = new TaskQueueToken();
        this.createdTaskQueues.put(token, taskQueue);
        return token;
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void setMessageHandler(String channel, BinaryMessenger.BinaryMessageHandler handler) {
        setMessageHandler(channel, handler, null);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void setMessageHandler(String channel, BinaryMessenger.BinaryMessageHandler handler, BinaryMessenger.TaskQueue taskQueue) {
        if (handler == null) {
            Log.v(TAG, "Removing handler for channel '" + channel + "'");
            synchronized (this.handlersLock) {
                this.messageHandlers.remove(channel);
            }
            return;
        }
        DartMessengerTaskQueue dartMessengerTaskQueue = null;
        if (taskQueue != null) {
            DartMessengerTaskQueue dartMessengerTaskQueue2 = this.createdTaskQueues.get(taskQueue);
            dartMessengerTaskQueue = dartMessengerTaskQueue2;
            if (dartMessengerTaskQueue == null) {
                throw new IllegalArgumentException("Unrecognized TaskQueue, use BinaryMessenger to create your TaskQueue (ex makeBackgroundTaskQueue).");
            }
        }
        Log.v(TAG, "Setting handler for channel '" + channel + "'");
        synchronized (this.handlersLock) {
            this.messageHandlers.put(channel, new HandlerInfo(handler, dartMessengerTaskQueue));
            List<BufferedMessageInfo> list = this.bufferedMessages.remove(channel);
            if (list == null) {
                return;
            }
            for (BufferedMessageInfo info : list) {
                dispatchMessageToQueue(channel, this.messageHandlers.get(channel), info.message, info.replyId, info.messageData);
            }
        }
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void enableBufferingIncomingMessages() {
        this.enableBufferingIncomingMessages.set(true);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void disableBufferingIncomingMessages() {
        Map<String, List<BufferedMessageInfo>> pendingMessages;
        synchronized (this.handlersLock) {
            this.enableBufferingIncomingMessages.set(false);
            pendingMessages = this.bufferedMessages;
            this.bufferedMessages = new HashMap();
        }
        for (Map.Entry<String, List<BufferedMessageInfo>> channel : pendingMessages.entrySet()) {
            for (BufferedMessageInfo info : channel.getValue()) {
                dispatchMessageToQueue(channel.getKey(), null, info.message, info.replyId, info.messageData);
            }
        }
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void send(String channel, ByteBuffer message) {
        Log.v(TAG, "Sending message over channel '" + channel + "'");
        send(channel, message, null);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void send(String channel, ByteBuffer message, BinaryMessenger.BinaryReply callback) {
        TraceSection.begin("DartMessenger#send on " + channel);
        try {
            Log.v(TAG, "Sending message with callback over channel '" + channel + "'");
            int replyId = this.nextReplyId;
            this.nextReplyId = replyId + 1;
            if (callback != null) {
                this.pendingReplies.put(Integer.valueOf(replyId), callback);
            }
            if (message == null) {
                this.flutterJNI.dispatchEmptyPlatformMessage(channel, replyId);
            } else {
                this.flutterJNI.dispatchPlatformMessage(channel, message, message.position(), replyId);
            }
        } finally {
            TraceSection.end();
        }
    }

    private void invokeHandler(HandlerInfo handlerInfo, ByteBuffer message, int replyId) {
        if (handlerInfo != null) {
            try {
                Log.v(TAG, "Deferring to registered handler to process message.");
                handlerInfo.handler.onMessage(message, new Reply(this.flutterJNI, replyId));
                return;
            } catch (Error err) {
                handleError(err);
                return;
            } catch (Exception ex) {
                Log.e(TAG, "Uncaught exception in binary message listener", ex);
                this.flutterJNI.invokePlatformMessageEmptyResponseCallback(replyId);
                return;
            }
        }
        Log.v(TAG, "No registered handler for message. Responding to Dart with empty reply message.");
        this.flutterJNI.invokePlatformMessageEmptyResponseCallback(replyId);
    }

    private void dispatchMessageToQueue(final String channel, final HandlerInfo handlerInfo, final ByteBuffer message, final int replyId, final long messageData) {
        DartMessengerTaskQueue taskQueue = handlerInfo != null ? handlerInfo.taskQueue : null;
        TraceSection.beginAsyncSection("PlatformChannel ScheduleHandler on " + channel, replyId);
        Runnable myRunnable = new Runnable() { // from class: io.flutter.embedding.engine.dart.DartMessenger$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DartMessenger.this.m20x5c171975(channel, replyId, handlerInfo, message, messageData);
            }
        };
        DartMessengerTaskQueue nonnullTaskQueue = taskQueue == null ? this.platformTaskQueue : taskQueue;
        nonnullTaskQueue.dispatch(myRunnable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$dispatchMessageToQueue$0$io-flutter-embedding-engine-dart-DartMessenger  reason: not valid java name */
    public /* synthetic */ void m20x5c171975(String channel, int replyId, HandlerInfo handlerInfo, ByteBuffer message, long messageData) {
        TraceSection.endAsyncSection("PlatformChannel ScheduleHandler on " + channel, replyId);
        TraceSection.begin("DartMessenger#handleMessageFromDart on " + channel);
        try {
            invokeHandler(handlerInfo, message, replyId);
            if (message != null && message.isDirect()) {
                message.limit(0);
            }
        } finally {
            this.flutterJNI.cleanupMessageData(messageData);
            TraceSection.end();
        }
    }

    @Override // io.flutter.embedding.engine.dart.PlatformMessageHandler
    public void handleMessageFromDart(String channel, ByteBuffer message, int replyId, long messageData) {
        HandlerInfo handlerInfo;
        boolean messageDeferred;
        Log.v(TAG, "Received message from Dart over channel '" + channel + "'");
        synchronized (this.handlersLock) {
            handlerInfo = this.messageHandlers.get(channel);
            messageDeferred = this.enableBufferingIncomingMessages.get() && handlerInfo == null;
            if (messageDeferred) {
                if (!this.bufferedMessages.containsKey(channel)) {
                    this.bufferedMessages.put(channel, new LinkedList());
                }
                List<BufferedMessageInfo> buffer = this.bufferedMessages.get(channel);
                buffer.add(new BufferedMessageInfo(message, replyId, messageData));
            }
        }
        if (!messageDeferred) {
            dispatchMessageToQueue(channel, handlerInfo, message, replyId, messageData);
        }
    }

    @Override // io.flutter.embedding.engine.dart.PlatformMessageHandler
    public void handlePlatformMessageResponse(int replyId, ByteBuffer reply) {
        Log.v(TAG, "Received message reply from Dart.");
        BinaryMessenger.BinaryReply callback = this.pendingReplies.remove(Integer.valueOf(replyId));
        if (callback != null) {
            try {
                Log.v(TAG, "Invoking registered callback for reply from Dart.");
                callback.reply(reply);
                if (reply != null && reply.isDirect()) {
                    reply.limit(0);
                }
            } catch (Error err) {
                handleError(err);
            } catch (Exception ex) {
                Log.e(TAG, "Uncaught exception in binary message reply handler", ex);
            }
        }
    }

    public int getPendingChannelResponseCount() {
        return this.pendingReplies.size();
    }

    private static void handleError(Error err) {
        Thread currentThread = Thread.currentThread();
        if (currentThread.getUncaughtExceptionHandler() == null) {
            throw err;
        }
        currentThread.getUncaughtExceptionHandler().uncaughtException(currentThread, err);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Reply implements BinaryMessenger.BinaryReply {
        private final AtomicBoolean done = new AtomicBoolean(false);
        private final FlutterJNI flutterJNI;
        private final int replyId;

        Reply(FlutterJNI flutterJNI, int replyId) {
            this.flutterJNI = flutterJNI;
            this.replyId = replyId;
        }

        @Override // io.flutter.plugin.common.BinaryMessenger.BinaryReply
        public void reply(ByteBuffer reply) {
            if (this.done.getAndSet(true)) {
                throw new IllegalStateException("Reply already submitted");
            }
            if (reply == null) {
                this.flutterJNI.invokePlatformMessageEmptyResponseCallback(this.replyId);
            } else {
                this.flutterJNI.invokePlatformMessageResponseCallback(this.replyId, reply, reply.position());
            }
        }
    }
}
