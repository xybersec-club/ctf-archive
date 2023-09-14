package kotlinx.coroutines;

import java.util.concurrent.locks.LockSupport;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: Builders.kt */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0002\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\u0002\u0010\tJ\u0012\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0014J\u000b\u0010\u0011\u001a\u00028\u0000¢\u0006\u0002\u0010\u0012R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\u00020\u000b8TX\u0094\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\f¨\u0006\u0013"}, d2 = {"Lkotlinx/coroutines/BlockingCoroutine;", "T", "Lkotlinx/coroutines/AbstractCoroutine;", "parentContext", "Lkotlin/coroutines/CoroutineContext;", "blockedThread", "Ljava/lang/Thread;", "eventLoop", "Lkotlinx/coroutines/EventLoop;", "(Lkotlin/coroutines/CoroutineContext;Ljava/lang/Thread;Lkotlinx/coroutines/EventLoop;)V", "isScopedCoroutine", "", "()Z", "afterCompletion", "", "state", "", "joinBlocking", "()Ljava/lang/Object;", "kotlinx-coroutines-core"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* loaded from: classes.dex */
public final class BlockingCoroutine<T> extends AbstractCoroutine<T> {
    private final Thread blockedThread;
    private final EventLoop eventLoop;

    @Override // kotlinx.coroutines.JobSupport
    protected boolean isScopedCoroutine() {
        return true;
    }

    public BlockingCoroutine(CoroutineContext coroutineContext, Thread thread, EventLoop eventLoop) {
        super(coroutineContext, true, true);
        this.blockedThread = thread;
        this.eventLoop = eventLoop;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // kotlinx.coroutines.JobSupport
    public void afterCompletion(Object obj) {
        Unit unit;
        if (Intrinsics.areEqual(Thread.currentThread(), this.blockedThread)) {
            return;
        }
        Thread thread = this.blockedThread;
        AbstractTimeSource timeSource = AbstractTimeSourceKt.getTimeSource();
        if (timeSource == null) {
            unit = null;
        } else {
            timeSource.unpark(thread);
            unit = Unit.INSTANCE;
        }
        if (unit == null) {
            LockSupport.unpark(thread);
        }
    }

    public final T joinBlocking() {
        Unit unit;
        AbstractTimeSource timeSource = AbstractTimeSourceKt.getTimeSource();
        if (timeSource != null) {
            timeSource.registerTimeLoopThread();
        }
        try {
            EventLoop eventLoop = this.eventLoop;
            if (eventLoop != null) {
                EventLoop.incrementUseCount$default(eventLoop, false, 1, null);
            }
            while (!Thread.interrupted()) {
                EventLoop eventLoop2 = this.eventLoop;
                long processNextEvent = eventLoop2 == null ? Long.MAX_VALUE : eventLoop2.processNextEvent();
                if (!isCompleted()) {
                    AbstractTimeSource timeSource2 = AbstractTimeSourceKt.getTimeSource();
                    if (timeSource2 == null) {
                        unit = null;
                    } else {
                        timeSource2.parkNanos(this, processNextEvent);
                        unit = Unit.INSTANCE;
                    }
                    if (unit == null) {
                        LockSupport.parkNanos(this, processNextEvent);
                    }
                } else {
                    EventLoop eventLoop3 = this.eventLoop;
                    if (eventLoop3 != null) {
                        EventLoop.decrementUseCount$default(eventLoop3, false, 1, null);
                    }
                    T t = (T) JobSupportKt.unboxState(getState$kotlinx_coroutines_core());
                    CompletedExceptionally completedExceptionally = t instanceof CompletedExceptionally ? (CompletedExceptionally) t : null;
                    if (completedExceptionally == null) {
                        return t;
                    }
                    throw completedExceptionally.cause;
                }
            }
            InterruptedException interruptedException = new InterruptedException();
            cancelCoroutine(interruptedException);
            throw interruptedException;
        } finally {
            AbstractTimeSource timeSource3 = AbstractTimeSourceKt.getTimeSource();
            if (timeSource3 != null) {
                timeSource3.unregisterTimeLoopThread();
            }
        }
    }
}
