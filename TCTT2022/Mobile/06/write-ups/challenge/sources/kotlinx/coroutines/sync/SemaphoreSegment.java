package kotlinx.coroutines.sync;

import java.util.concurrent.atomic.AtomicReferenceArray;
import kotlin.Metadata;
import kotlinx.coroutines.debug.internal.ConcurrentWeakMap$Core$$ExternalSyntheticBackportWithForwarding0;
import kotlinx.coroutines.internal.Segment;
import kotlinx.coroutines.internal.Symbol;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: Semaphore.kt */
@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u001fB!\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0000\u0012\u0006\u0010\u0005\u001a\u00020\u0004¢\u0006\u0004\b\u0006\u0010\u0007J\u0015\u0010\n\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\u0004¢\u0006\u0004\b\n\u0010\u000bJ,\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\b\u001a\u00020\u00042\b\u0010\r\u001a\u0004\u0018\u00010\f2\b\u0010\u000e\u001a\u0004\u0018\u00010\fH\u0086\b¢\u0006\u0004\b\u0010\u0010\u0011J\u001a\u0010\u0012\u001a\u0004\u0018\u00010\f2\u0006\u0010\b\u001a\u00020\u0004H\u0086\b¢\u0006\u0004\b\u0012\u0010\u0013J$\u0010\u0014\u001a\u0004\u0018\u00010\f2\u0006\u0010\b\u001a\u00020\u00042\b\u0010\u000e\u001a\u0004\u0018\u00010\fH\u0086\b¢\u0006\u0004\b\u0014\u0010\u0015J\"\u0010\u0016\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\u00042\b\u0010\u000e\u001a\u0004\u0018\u00010\fH\u0086\b¢\u0006\u0004\b\u0016\u0010\u0017J\u000f\u0010\u0019\u001a\u00020\u0018H\u0016¢\u0006\u0004\b\u0019\u0010\u001aR\u0014\u0010\u001d\u001a\u00020\u00048VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u001c¨\u0006\u001e"}, d2 = {"Lkotlinx/coroutines/sync/SemaphoreSegment;", "", "id", "prev", "", "pointers", "<init>", "(JLkotlinx/coroutines/sync/SemaphoreSegment;I)V", "index", "", "cancel", "(I)V", "", "expected", "value", "", "cas", "(ILjava/lang/Object;Ljava/lang/Object;)Z", "get", "(I)Ljava/lang/Object;", "getAndSet", "(ILjava/lang/Object;)Ljava/lang/Object;", "set", "(ILjava/lang/Object;)V", "", "toString", "()Ljava/lang/String;", "getMaxSlots", "()I", "maxSlots", "kotlinx-coroutines-core", "Lkotlinx/coroutines/internal/Segment;"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* loaded from: classes.dex */
public final class SemaphoreSegment extends Segment<SemaphoreSegment> {
    /* synthetic */ AtomicReferenceArray acquirers;

    public SemaphoreSegment(long j, SemaphoreSegment semaphoreSegment, int i) {
        super(j, semaphoreSegment, i);
        int i2;
        i2 = SemaphoreKt.SEGMENT_SIZE;
        this.acquirers = new AtomicReferenceArray(i2);
    }

    @Override // kotlinx.coroutines.internal.Segment
    public int getMaxSlots() {
        int i;
        i = SemaphoreKt.SEGMENT_SIZE;
        return i;
    }

    public final Object get(int i) {
        return this.acquirers.get(i);
    }

    public final void set(int i, Object obj) {
        this.acquirers.set(i, obj);
    }

    public final boolean cas(int i, Object obj, Object obj2) {
        return ConcurrentWeakMap$Core$$ExternalSyntheticBackportWithForwarding0.m(this.acquirers, i, obj, obj2);
    }

    public final Object getAndSet(int i, Object obj) {
        return this.acquirers.getAndSet(i, obj);
    }

    public final void cancel(int i) {
        Symbol symbol;
        symbol = SemaphoreKt.CANCELLED;
        this.acquirers.set(i, symbol);
        onSlotCleaned();
    }

    public String toString() {
        return "SemaphoreSegment[id=" + getId() + ", hashCode=" + hashCode() + ']';
    }
}
