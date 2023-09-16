package kotlinx.coroutines.sync;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Metadata;
import kotlin.SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CancelHandlerBase;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.CancellableContinuationKt;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.debug.internal.ConcurrentWeakMap$Core$$ExternalSyntheticBackportWithForwarding0;
import kotlinx.coroutines.internal.ConcurrentLinkedListKt;
import kotlinx.coroutines.internal.ConcurrentLinkedListNode;
import kotlinx.coroutines.internal.Segment;
import kotlinx.coroutines.internal.SegmentOrClosed;
import kotlinx.coroutines.internal.Symbol;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: Semaphore.kt */
@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0010\u0003\n\u0002\b\u0004\n\u0002\u0018\u0002\b\u0002\u0018\u00002\u00020\u001eB\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0001¢\u0006\u0004\b\u0004\u0010\u0005J\u0013\u0010\u0007\u001a\u00020\u0006H\u0096@ø\u0001\u0000¢\u0006\u0004\b\u0007\u0010\bJ\u0013\u0010\t\u001a\u00020\u0006H\u0082@ø\u0001\u0000¢\u0006\u0004\b\t\u0010\bJ\u001d\u0010\r\u001a\u00020\f2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\nH\u0002¢\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u000f\u001a\u00020\u0006H\u0016¢\u0006\u0004\b\u000f\u0010\u0010J\u000f\u0010\u0011\u001a\u00020\fH\u0016¢\u0006\u0004\b\u0011\u0010\u0012J\u000f\u0010\u0013\u001a\u00020\fH\u0002¢\u0006\u0004\b\u0013\u0010\u0012J\u0019\u0010\u0014\u001a\u00020\f*\b\u0012\u0004\u0012\u00020\u00060\nH\u0002¢\u0006\u0004\b\u0014\u0010\u000eR\u0014\u0010\u0017\u001a\u00020\u00018VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016R \u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u00060\u00188\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u001a\u0010\u001bR\u0014\u0010\u0002\u001a\u00020\u00018\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0002\u0010\u001c\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u001d"}, d2 = {"Lkotlinx/coroutines/sync/SemaphoreImpl;", "", "permits", "acquiredPermits", "<init>", "(II)V", "", "acquire", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "acquireSlowPath", "Lkotlinx/coroutines/CancellableContinuation;", "cont", "", "addAcquireToQueue", "(Lkotlinx/coroutines/CancellableContinuation;)Z", "release", "()V", "tryAcquire", "()Z", "tryResumeNextFromQueue", "tryResumeAcquire", "getAvailablePermits", "()I", "availablePermits", "Lkotlin/Function1;", "", "onCancellationRelease", "Lkotlin/jvm/functions/Function1;", "I", "kotlinx-coroutines-core", "Lkotlinx/coroutines/sync/Semaphore;"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes.dex */
public final class SemaphoreImpl implements Semaphore {
    volatile /* synthetic */ int _availablePermits;
    private volatile /* synthetic */ long deqIdx = 0;
    private volatile /* synthetic */ long enqIdx = 0;
    private volatile /* synthetic */ Object head;
    private final Function1<Throwable, Unit> onCancellationRelease;
    private final int permits;
    private volatile /* synthetic */ Object tail;
    private static final /* synthetic */ AtomicReferenceFieldUpdater head$FU = AtomicReferenceFieldUpdater.newUpdater(SemaphoreImpl.class, Object.class, "head");
    private static final /* synthetic */ AtomicLongFieldUpdater deqIdx$FU = AtomicLongFieldUpdater.newUpdater(SemaphoreImpl.class, "deqIdx");
    private static final /* synthetic */ AtomicReferenceFieldUpdater tail$FU = AtomicReferenceFieldUpdater.newUpdater(SemaphoreImpl.class, Object.class, "tail");
    private static final /* synthetic */ AtomicLongFieldUpdater enqIdx$FU = AtomicLongFieldUpdater.newUpdater(SemaphoreImpl.class, "enqIdx");
    static final /* synthetic */ AtomicIntegerFieldUpdater _availablePermits$FU = AtomicIntegerFieldUpdater.newUpdater(SemaphoreImpl.class, "_availablePermits");

    public SemaphoreImpl(int permits, int acquiredPermits) {
        this.permits = permits;
        boolean z = true;
        if (!(permits > 0)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("Semaphore should have at least 1 permit, but had ", Integer.valueOf(permits)).toString());
        }
        if (!((acquiredPermits < 0 || acquiredPermits > permits) ? false : false)) {
            throw new IllegalArgumentException(Intrinsics.stringPlus("The number of acquired permits should be in 0..", Integer.valueOf(permits)).toString());
        }
        SemaphoreSegment s = new SemaphoreSegment(0L, null, 2);
        this.head = s;
        this.tail = s;
        this._availablePermits = permits - acquiredPermits;
        this.onCancellationRelease = new Function1<Throwable, Unit>() { // from class: kotlinx.coroutines.sync.SemaphoreImpl$onCancellationRelease$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(Throwable th) {
                invoke2(th);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke  reason: avoid collision after fix types in other method */
            public final void invoke2(Throwable $noName_0) {
                SemaphoreImpl.this.release();
            }
        };
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public int getAvailablePermits() {
        return Math.max(this._availablePermits, 0);
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public boolean tryAcquire() {
        int p;
        do {
            p = this._availablePermits;
            if (p <= 0) {
                return false;
            }
        } while (!_availablePermits$FU.compareAndSet(this, p, p - 1));
        return true;
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public Object acquire(Continuation<? super Unit> continuation) {
        Object acquireSlowPath;
        int p = _availablePermits$FU.getAndDecrement(this);
        return (p <= 0 && (acquireSlowPath = acquireSlowPath(continuation)) == IntrinsicsKt.getCOROUTINE_SUSPENDED()) ? acquireSlowPath : Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object acquireSlowPath(Continuation<? super Unit> continuation) {
        CancellableContinuationImpl cancellable$iv = CancellableContinuationKt.getOrCreateCancellableContinuation(IntrinsicsKt.intercepted(continuation));
        CancellableContinuationImpl cont = cancellable$iv;
        while (true) {
            if (!addAcquireToQueue(cont)) {
                int p = _availablePermits$FU.getAndDecrement(this);
                if (p > 0) {
                    cont.resume(Unit.INSTANCE, this.onCancellationRelease);
                    break;
                }
            } else {
                break;
            }
        }
        Object result = cancellable$iv.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? result : Unit.INSTANCE;
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public void release() {
        int cur$iv;
        int upd$iv;
        do {
            do {
                cur$iv = this._availablePermits;
                int i = this.permits;
                if (!(cur$iv < i)) {
                    throw new IllegalStateException(Intrinsics.stringPlus("The number of released permits cannot be greater than ", Integer.valueOf(i)).toString());
                }
                upd$iv = cur$iv + 1;
            } while (!_availablePermits$FU.compareAndSet(this, cur$iv, upd$iv));
            if (cur$iv >= 0) {
                return;
            }
        } while (!tryResumeNextFromQueue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public final boolean addAcquireToQueue(CancellableContinuation<? super Unit> cancellableContinuation) {
        int i;
        SemaphoreSegment curTail;
        Object m1576constructorimpl;
        Segment createSegment;
        Object s$iv;
        int i2;
        Object expected$iv;
        Object value$iv;
        Symbol symbol;
        boolean z;
        SemaphoreSegment s$iv2 = (SemaphoreSegment) this.tail;
        long enqIdx = enqIdx$FU.getAndIncrement(this);
        i = SemaphoreKt.SEGMENT_SIZE;
        long id$iv = enqIdx / i;
        while (true) {
            SemaphoreSegment $this$findSegmentInternal$iv$iv = s$iv2;
            Segment cur$iv$iv = $this$findSegmentInternal$iv$iv;
            while (true) {
                if (cur$iv$iv.getId() < id$iv || cur$iv$iv.getRemoved()) {
                    ConcurrentLinkedListNode this_$iv$iv$iv = cur$iv$iv;
                    Object it$iv$iv$iv = this_$iv$iv$iv.getNextOrClosed();
                    curTail = s$iv2;
                    if (it$iv$iv$iv == ConcurrentLinkedListKt.CLOSED) {
                        m1576constructorimpl = SegmentOrClosed.m1576constructorimpl(ConcurrentLinkedListKt.CLOSED);
                        break;
                    }
                    Segment next$iv$iv = (Segment) ((ConcurrentLinkedListNode) it$iv$iv$iv);
                    if (next$iv$iv != null) {
                        cur$iv$iv = next$iv$iv;
                        s$iv2 = curTail;
                    } else {
                        long p0 = cur$iv$iv.getId() + 1;
                        SemaphoreSegment p1 = (SemaphoreSegment) cur$iv$iv;
                        createSegment = SemaphoreKt.createSegment(p0, p1);
                        Segment newTail$iv$iv = createSegment;
                        if (!cur$iv$iv.trySetNext(newTail$iv$iv)) {
                            s$iv2 = curTail;
                        } else {
                            if (cur$iv$iv.getRemoved()) {
                                cur$iv$iv.remove();
                            }
                            cur$iv$iv = newTail$iv$iv;
                            s$iv2 = curTail;
                        }
                    }
                } else {
                    m1576constructorimpl = SegmentOrClosed.m1576constructorimpl(cur$iv$iv);
                    curTail = s$iv2;
                    break;
                }
            }
            s$iv = m1576constructorimpl;
            if (SegmentOrClosed.m1581isClosedimpl(s$iv)) {
                break;
            }
            Segment to$iv$iv = SegmentOrClosed.m1579getSegmentimpl(s$iv);
            while (true) {
                Segment cur$iv$iv2 = (Segment) this.tail;
                if (cur$iv$iv2.getId() >= to$iv$iv.getId()) {
                    z = true;
                    break;
                } else if (!to$iv$iv.tryIncPointers$kotlinx_coroutines_core()) {
                    z = false;
                    break;
                } else if (SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(tail$FU, this, cur$iv$iv2, to$iv$iv)) {
                    if (cur$iv$iv2.decPointers$kotlinx_coroutines_core()) {
                        cur$iv$iv2.remove();
                    }
                    z = true;
                } else if (to$iv$iv.decPointers$kotlinx_coroutines_core()) {
                    to$iv$iv.remove();
                }
            }
            if (z) {
                break;
            }
            s$iv2 = curTail;
        }
        SemaphoreSegment segment = (SemaphoreSegment) SegmentOrClosed.m1579getSegmentimpl(s$iv);
        i2 = SemaphoreKt.SEGMENT_SIZE;
        int i3 = (int) (enqIdx % i2);
        if (!ConcurrentWeakMap$Core$$ExternalSyntheticBackportWithForwarding0.m(segment.acquirers, i3, null, cancellableContinuation)) {
            expected$iv = SemaphoreKt.PERMIT;
            value$iv = SemaphoreKt.TAKEN;
            if (ConcurrentWeakMap$Core$$ExternalSyntheticBackportWithForwarding0.m(segment.acquirers, i3, expected$iv, value$iv)) {
                cancellableContinuation.resume(Unit.INSTANCE, this.onCancellationRelease);
                return true;
            } else if (!DebugKt.getASSERTIONS_ENABLED()) {
                return false;
            } else {
                Object obj = segment.acquirers.get(i3);
                symbol = SemaphoreKt.BROKEN;
                if (obj == symbol) {
                    return false;
                }
                throw new AssertionError();
            }
        }
        CancelHandlerBase $this$asHandler$iv = new CancelSemaphoreAcquisitionHandler(segment, i3);
        cancellableContinuation.invokeOnCancellation($this$asHandler$iv);
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final boolean tryResumeNextFromQueue() {
        int i;
        Object m1576constructorimpl;
        Segment createSegment;
        Object s$iv;
        int i2;
        Symbol symbol;
        Symbol symbol2;
        int i3;
        Object expected$iv;
        Object value$iv;
        Symbol symbol3;
        boolean z;
        Segment curHead = (SemaphoreSegment) this.head;
        long deqIdx = deqIdx$FU.getAndIncrement(this);
        i = SemaphoreKt.SEGMENT_SIZE;
        long id = deqIdx / i;
        do {
            Segment $this$findSegmentInternal$iv$iv = curHead;
            Segment cur$iv$iv = $this$findSegmentInternal$iv$iv;
            while (true) {
                if (cur$iv$iv.getId() < id || cur$iv$iv.getRemoved()) {
                    ConcurrentLinkedListNode this_$iv$iv$iv = cur$iv$iv;
                    Object it$iv$iv$iv = this_$iv$iv$iv.getNextOrClosed();
                    if (it$iv$iv$iv == ConcurrentLinkedListKt.CLOSED) {
                        m1576constructorimpl = SegmentOrClosed.m1576constructorimpl(ConcurrentLinkedListKt.CLOSED);
                        break;
                    }
                    Segment next$iv$iv = (Segment) ((ConcurrentLinkedListNode) it$iv$iv$iv);
                    if (next$iv$iv != null) {
                        cur$iv$iv = next$iv$iv;
                    } else {
                        long p0 = cur$iv$iv.getId() + 1;
                        SemaphoreSegment p1 = (SemaphoreSegment) cur$iv$iv;
                        createSegment = SemaphoreKt.createSegment(p0, p1);
                        Segment newTail$iv$iv = createSegment;
                        if (cur$iv$iv.trySetNext(newTail$iv$iv)) {
                            if (cur$iv$iv.getRemoved()) {
                                cur$iv$iv.remove();
                            }
                            cur$iv$iv = newTail$iv$iv;
                        }
                    }
                } else {
                    m1576constructorimpl = SegmentOrClosed.m1576constructorimpl(cur$iv$iv);
                    break;
                }
            }
            s$iv = m1576constructorimpl;
            if (SegmentOrClosed.m1581isClosedimpl(s$iv)) {
                break;
            }
            Segment to$iv$iv = SegmentOrClosed.m1579getSegmentimpl(s$iv);
            while (true) {
                Segment cur$iv$iv2 = (Segment) this.head;
                if (cur$iv$iv2.getId() >= to$iv$iv.getId()) {
                    z = true;
                    continue;
                    break;
                } else if (!to$iv$iv.tryIncPointers$kotlinx_coroutines_core()) {
                    z = false;
                    continue;
                    break;
                } else if (SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(head$FU, this, cur$iv$iv2, to$iv$iv)) {
                    if (cur$iv$iv2.decPointers$kotlinx_coroutines_core()) {
                        cur$iv$iv2.remove();
                    }
                    z = true;
                    continue;
                } else if (to$iv$iv.decPointers$kotlinx_coroutines_core()) {
                    to$iv$iv.remove();
                }
            }
        } while (!z);
        SemaphoreSegment segment = (SemaphoreSegment) SegmentOrClosed.m1579getSegmentimpl(s$iv);
        segment.cleanPrev();
        if (segment.getId() > id) {
            return false;
        }
        i2 = SemaphoreKt.SEGMENT_SIZE;
        int i4 = (int) (deqIdx % i2);
        symbol = SemaphoreKt.PERMIT;
        Object value$iv2 = segment.acquirers.getAndSet(i4, symbol);
        if (value$iv2 == null) {
            i3 = SemaphoreKt.MAX_SPIN_CYCLES;
            for (int i5 = 0; i5 < i3; i5++) {
                Object obj = segment.acquirers.get(i4);
                symbol3 = SemaphoreKt.TAKEN;
                if (obj == symbol3) {
                    return true;
                }
            }
            expected$iv = SemaphoreKt.PERMIT;
            value$iv = SemaphoreKt.BROKEN;
            return !ConcurrentWeakMap$Core$$ExternalSyntheticBackportWithForwarding0.m(segment.acquirers, i4, expected$iv, value$iv);
        }
        symbol2 = SemaphoreKt.CANCELLED;
        if (value$iv2 == symbol2) {
            return false;
        }
        return tryResumeAcquire((CancellableContinuation) value$iv2);
    }

    private final boolean tryResumeAcquire(CancellableContinuation<? super Unit> cancellableContinuation) {
        Object token = cancellableContinuation.tryResume(Unit.INSTANCE, null, this.onCancellationRelease);
        if (token == null) {
            return false;
        }
        cancellableContinuation.completeResume(token);
        return true;
    }
}
