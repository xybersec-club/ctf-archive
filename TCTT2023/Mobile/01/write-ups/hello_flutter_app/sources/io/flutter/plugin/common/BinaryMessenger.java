package io.flutter.plugin.common;

import com.android.tools.r8.annotations.SynthesizedClassV2;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public interface BinaryMessenger {

    /* loaded from: classes.dex */
    public interface BinaryMessageHandler {
        void onMessage(ByteBuffer byteBuffer, BinaryReply binaryReply);
    }

    /* loaded from: classes.dex */
    public interface BinaryReply {
        void reply(ByteBuffer byteBuffer);
    }

    /* loaded from: classes.dex */
    public interface TaskQueue {
    }

    void disableBufferingIncomingMessages();

    void enableBufferingIncomingMessages();

    TaskQueue makeBackgroundTaskQueue();

    TaskQueue makeBackgroundTaskQueue(TaskQueueOptions taskQueueOptions);

    void send(String str, ByteBuffer byteBuffer);

    void send(String str, ByteBuffer byteBuffer, BinaryReply binaryReply);

    void setMessageHandler(String str, BinaryMessageHandler binaryMessageHandler);

    void setMessageHandler(String str, BinaryMessageHandler binaryMessageHandler, TaskQueue taskQueue);

    /* loaded from: classes.dex */
    public static class TaskQueueOptions {
        private boolean isSerial = true;

        public boolean getIsSerial() {
            return this.isSerial;
        }

        public TaskQueueOptions setIsSerial(boolean isSerial) {
            this.isSerial = isSerial;
            return this;
        }
    }

    @SynthesizedClassV2(kind = 7, versionHash = "15f1483824cf4085ddca5a8529d873fc59a8ced2cbce67fb2b3dd9033ea03442")
    /* renamed from: io.flutter.plugin.common.BinaryMessenger$-CC  reason: invalid class name */
    /* loaded from: classes.dex */
    public final /* synthetic */ class CC {
        public static TaskQueue $default$makeBackgroundTaskQueue(BinaryMessenger _this, TaskQueueOptions options) {
            throw new UnsupportedOperationException("makeBackgroundTaskQueue not implemented.");
        }

        public static void $default$setMessageHandler(BinaryMessenger _this, String channel, BinaryMessageHandler handler, TaskQueue taskQueue) {
            if (taskQueue != null) {
                throw new UnsupportedOperationException("setMessageHandler called with nonnull taskQueue is not supported.");
            }
            _this.setMessageHandler(channel, handler);
        }

        public static void $default$enableBufferingIncomingMessages(BinaryMessenger _this) {
            throw new UnsupportedOperationException("enableBufferingIncomingMessages not implemented.");
        }

        public static void $default$disableBufferingIncomingMessages(BinaryMessenger _this) {
            throw new UnsupportedOperationException("disableBufferingIncomingMessages not implemented.");
        }
    }
}
