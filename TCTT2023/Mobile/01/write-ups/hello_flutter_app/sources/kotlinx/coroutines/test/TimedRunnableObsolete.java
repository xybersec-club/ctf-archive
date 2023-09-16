package kotlinx.coroutines.test;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.internal.ThreadSafeHeap;
import kotlinx.coroutines.internal.ThreadSafeHeapNode;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: TestCoroutineContext.kt */
@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u00012\u00060\u0002j\u0002`\u00032\u00020\u0004B%\u0012\n\u0010\u0005\u001a\u00060\u0002j\u0002`\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007¢\u0006\u0002\u0010\tJ\u0011\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\u0000H\u0096\u0002J\b\u0010\u0005\u001a\u00020\u0018H\u0016J\b\u0010\u0019\u001a\u00020\u001aH\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\n\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u000bX\u0096\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0010\u001a\u00020\u0011X\u0096\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u0012\u0010\u0005\u001a\u00060\u0002j\u0002`\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u00020\u00078\u0000X\u0081\u0004¢\u0006\u0002\n\u0000¨\u0006\u001b"}, d2 = {"Lkotlinx/coroutines/test/TimedRunnableObsolete;", "", "Ljava/lang/Runnable;", "Lkotlinx/coroutines/Runnable;", "Lkotlinx/coroutines/internal/ThreadSafeHeapNode;", "run", "count", "", "time", "(Ljava/lang/Runnable;JJ)V", "heap", "Lkotlinx/coroutines/internal/ThreadSafeHeap;", "getHeap", "()Lkotlinx/coroutines/internal/ThreadSafeHeap;", "setHeap", "(Lkotlinx/coroutines/internal/ThreadSafeHeap;)V", "index", "", "getIndex", "()I", "setIndex", "(I)V", "compareTo", "other", "", "toString", "", "kotlinx-coroutines-core"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes.dex */
public final class TimedRunnableObsolete implements Comparable<TimedRunnableObsolete>, Runnable, ThreadSafeHeapNode {
    private final long count;
    private ThreadSafeHeap<?> heap;
    private int index;
    private final Runnable run;
    public final long time;

    public TimedRunnableObsolete(Runnable run, long count, long time) {
        this.run = run;
        this.count = count;
        this.time = time;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ TimedRunnableObsolete(java.lang.Runnable r9, long r10, long r12, int r14, kotlin.jvm.internal.DefaultConstructorMarker r15) {
        /*
            r8 = this;
            r15 = r14 & 2
            r0 = 0
            if (r15 == 0) goto L8
            r4 = r0
            goto L9
        L8:
            r4 = r10
        L9:
            r10 = r14 & 4
            if (r10 == 0) goto Lf
            r6 = r0
            goto L10
        Lf:
            r6 = r12
        L10:
            r2 = r8
            r3 = r9
            r2.<init>(r3, r4, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.test.TimedRunnableObsolete.<init>(java.lang.Runnable, long, long, int, kotlin.jvm.internal.DefaultConstructorMarker):void");
    }

    @Override // kotlinx.coroutines.internal.ThreadSafeHeapNode
    public ThreadSafeHeap<?> getHeap() {
        return this.heap;
    }

    @Override // kotlinx.coroutines.internal.ThreadSafeHeapNode
    public void setHeap(ThreadSafeHeap<?> threadSafeHeap) {
        this.heap = threadSafeHeap;
    }

    @Override // kotlinx.coroutines.internal.ThreadSafeHeapNode
    public int getIndex() {
        return this.index;
    }

    @Override // kotlinx.coroutines.internal.ThreadSafeHeapNode
    public void setIndex(int i) {
        this.index = i;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.run.run();
    }

    @Override // java.lang.Comparable
    public int compareTo(TimedRunnableObsolete other) {
        long j = this.time;
        long j2 = other.time;
        if (j == j2) {
            return Intrinsics.compare(this.count, other.count);
        }
        return Intrinsics.compare(j, j2);
    }

    public String toString() {
        return "TimedRunnable(time=" + this.time + ", run=" + this.run + ')';
    }
}
