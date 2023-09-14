package kotlinx.coroutines.sync;

import androidx.concurrent.futures.AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.CancellableContinuationImplKt;
import kotlinx.coroutines.CancellableContinuationKt;
import kotlinx.coroutines.DisposableHandle;
import kotlinx.coroutines.internal.AtomicDesc;
import kotlinx.coroutines.internal.AtomicKt;
import kotlinx.coroutines.internal.AtomicOp;
import kotlinx.coroutines.internal.LockFreeLinkedListHead;
import kotlinx.coroutines.internal.LockFreeLinkedListNode;
import kotlinx.coroutines.internal.OpDescriptor;
import kotlinx.coroutines.internal.Symbol;
import kotlinx.coroutines.intrinsics.CancellableKt;
import kotlinx.coroutines.intrinsics.UndispatchedKt;
import kotlinx.coroutines.selects.SelectClause2;
import kotlinx.coroutines.selects.SelectInstance;
import kotlinx.coroutines.selects.SelectKt;
/* compiled from: Mutex.kt */
@Metadata(d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\n\b\u0000\u0018\u00002\u00020\u00112\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u0005\u0012\u0004\u0012\u00020\u00110 :\u0006$%&'()B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0001¢\u0006\u0004\b\u0003\u0010\u0004J\u0017\u0010\u0007\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u0005H\u0016¢\u0006\u0004\b\u0007\u0010\bJ\u001d\u0010\n\u001a\u00020\t2\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u0096@ø\u0001\u0000¢\u0006\u0004\b\n\u0010\u000bJ\u001d\u0010\f\u001a\u00020\t2\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u0082@ø\u0001\u0000¢\u0006\u0004\b\f\u0010\u000bJT\u0010\u0014\u001a\u00020\t\"\u0004\b\u0000\u0010\r2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00028\u00000\u000e2\b\u0010\u0006\u001a\u0004\u0018\u00010\u00052\"\u0010\u0013\u001a\u001e\b\u0001\u0012\u0004\u0012\u00020\u0011\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u0012\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0010H\u0016ø\u0001\u0000¢\u0006\u0004\b\u0014\u0010\u0015J\u000f\u0010\u0017\u001a\u00020\u0016H\u0016¢\u0006\u0004\b\u0017\u0010\u0018J\u0019\u0010\u0019\u001a\u00020\u00012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u0016¢\u0006\u0004\b\u0019\u0010\bJ\u0019\u0010\u001a\u001a\u00020\t2\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u0016¢\u0006\u0004\b\u001a\u0010\u001bR\u0014\u0010\u001c\u001a\u00020\u00018VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001c\u0010\u001dR\u0014\u0010\u001f\u001a\u00020\u00018@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b\u001e\u0010\u001dR\"\u0010#\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u0005\u0012\u0004\u0012\u00020\u00110 8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b!\u0010\"\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006*"}, d2 = {"Lkotlinx/coroutines/sync/MutexImpl;", "", "locked", "<init>", "(Z)V", "", "owner", "holdsLock", "(Ljava/lang/Object;)Z", "", "lock", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "lockSuspend", "R", "Lkotlinx/coroutines/selects/SelectInstance;", "select", "Lkotlin/Function2;", "Lkotlinx/coroutines/sync/Mutex;", "Lkotlin/coroutines/Continuation;", "block", "registerSelectClause2", "(Lkotlinx/coroutines/selects/SelectInstance;Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)V", "", "toString", "()Ljava/lang/String;", "tryLock", "unlock", "(Ljava/lang/Object;)V", "isLocked", "()Z", "isLockedEmptyQueueState$kotlinx_coroutines_core", "isLockedEmptyQueueState", "Lkotlinx/coroutines/selects/SelectClause2;", "getOnLock", "()Lkotlinx/coroutines/selects/SelectClause2;", "onLock", "LockCont", "LockSelect", "LockWaiter", "LockedQueue", "TryLockDesc", "UnlockOp", "kotlinx-coroutines-core"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* loaded from: classes.dex */
public final class MutexImpl implements Mutex, SelectClause2<Object, Mutex> {
    static final /* synthetic */ AtomicReferenceFieldUpdater _state$FU = AtomicReferenceFieldUpdater.newUpdater(MutexImpl.class, Object.class, "_state");
    volatile /* synthetic */ Object _state;

    public MutexImpl(boolean z) {
        this._state = z ? MutexKt.EMPTY_LOCKED : MutexKt.EMPTY_UNLOCKED;
    }

    public final boolean isLockedEmptyQueueState$kotlinx_coroutines_core() {
        Object obj = this._state;
        return (obj instanceof LockedQueue) && ((LockedQueue) obj).isEmpty();
    }

    @Override // kotlinx.coroutines.sync.Mutex
    public Object lock(Object obj, Continuation<? super Unit> continuation) {
        Object lockSuspend;
        return (!tryLock(obj) && (lockSuspend = lockSuspend(obj, continuation)) == IntrinsicsKt.getCOROUTINE_SUSPENDED()) ? lockSuspend : Unit.INSTANCE;
    }

    @Override // kotlinx.coroutines.sync.Mutex
    public SelectClause2<Object, Mutex> getOnLock() {
        return this;
    }

    @Override // kotlinx.coroutines.selects.SelectClause2
    public <R> void registerSelectClause2(SelectInstance<? super R> selectInstance, Object obj, Function2<? super Mutex, ? super Continuation<? super R>, ? extends Object> function2) {
        Symbol symbol;
        Symbol symbol2;
        while (!selectInstance.isSelected()) {
            Object obj2 = this._state;
            if (obj2 instanceof Empty) {
                Empty empty = (Empty) obj2;
                Object obj3 = empty.locked;
                symbol = MutexKt.UNLOCKED;
                if (obj3 != symbol) {
                    AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_state$FU, this, obj2, new LockedQueue(empty.locked));
                } else {
                    Object performAtomicTrySelect = selectInstance.performAtomicTrySelect(new TryLockDesc(this, obj));
                    if (performAtomicTrySelect == null) {
                        UndispatchedKt.startCoroutineUnintercepted(function2, this, selectInstance.getCompletion());
                        return;
                    } else if (performAtomicTrySelect == SelectKt.getALREADY_SELECTED()) {
                        return;
                    } else {
                        symbol2 = MutexKt.LOCK_FAIL;
                        if (performAtomicTrySelect != symbol2 && performAtomicTrySelect != AtomicKt.RETRY_ATOMIC) {
                            throw new IllegalStateException(Intrinsics.stringPlus("performAtomicTrySelect(TryLockDesc) returned ", performAtomicTrySelect).toString());
                        }
                    }
                }
            } else if (obj2 instanceof LockedQueue) {
                LockedQueue lockedQueue = (LockedQueue) obj2;
                if (!(lockedQueue.owner != obj)) {
                    throw new IllegalStateException(Intrinsics.stringPlus("Already locked by ", obj).toString());
                }
                LockSelect lockSelect = new LockSelect(obj, selectInstance, function2);
                lockedQueue.addLast(lockSelect);
                if (this._state == obj2 || !lockSelect.take()) {
                    selectInstance.disposeOnSelect(lockSelect);
                    return;
                }
            } else if (!(obj2 instanceof OpDescriptor)) {
                throw new IllegalStateException(Intrinsics.stringPlus("Illegal state ", obj2).toString());
            } else {
                ((OpDescriptor) obj2).perform(this);
            }
        }
    }

    /* compiled from: Mutex.kt */
    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0002\u0018\u00002\u00020\u0001:\u0001\rB\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0002\u0010\u0006J\u001e\u0010\u0007\u001a\u00020\b2\n\u0010\t\u001a\u0006\u0012\u0002\b\u00030\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0005H\u0016J\u0016\u0010\f\u001a\u0004\u0018\u00010\u00052\n\u0010\t\u001a\u0006\u0012\u0002\b\u00030\nH\u0016R\u0010\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lkotlinx/coroutines/sync/MutexImpl$TryLockDesc;", "Lkotlinx/coroutines/internal/AtomicDesc;", "mutex", "Lkotlinx/coroutines/sync/MutexImpl;", "owner", "", "(Lkotlinx/coroutines/sync/MutexImpl;Ljava/lang/Object;)V", "complete", "", "op", "Lkotlinx/coroutines/internal/AtomicOp;", "failure", "prepare", "PrepareOp", "kotlinx-coroutines-core"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* loaded from: classes.dex */
    private static final class TryLockDesc extends AtomicDesc {
        public final MutexImpl mutex;
        public final Object owner;

        public TryLockDesc(MutexImpl mutexImpl, Object obj) {
            this.mutex = mutexImpl;
            this.owner = obj;
        }

        /* compiled from: Mutex.kt */
        @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0011\u0012\n\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003¢\u0006\u0002\u0010\u0004J\u0014\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\u0010\t\u001a\u0004\u0018\u00010\bH\u0016R\u0018\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\n"}, d2 = {"Lkotlinx/coroutines/sync/MutexImpl$TryLockDesc$PrepareOp;", "Lkotlinx/coroutines/internal/OpDescriptor;", "atomicOp", "Lkotlinx/coroutines/internal/AtomicOp;", "(Lkotlinx/coroutines/sync/MutexImpl$TryLockDesc;Lkotlinx/coroutines/internal/AtomicOp;)V", "getAtomicOp", "()Lkotlinx/coroutines/internal/AtomicOp;", "perform", "", "affected", "kotlinx-coroutines-core"}, k = 1, mv = {1, 6, 0}, xi = 48)
        /* loaded from: classes.dex */
        private final class PrepareOp extends OpDescriptor {
            private final AtomicOp<?> atomicOp;

            public PrepareOp(AtomicOp<?> atomicOp) {
                this.atomicOp = atomicOp;
            }

            @Override // kotlinx.coroutines.internal.OpDescriptor
            public AtomicOp<?> getAtomicOp() {
                return this.atomicOp;
            }

            @Override // kotlinx.coroutines.internal.OpDescriptor
            public Object perform(Object obj) {
                Object atomicOp = getAtomicOp().isDecided() ? MutexKt.EMPTY_UNLOCKED : getAtomicOp();
                Objects.requireNonNull(obj, "null cannot be cast to non-null type kotlinx.coroutines.sync.MutexImpl");
                AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(MutexImpl._state$FU, (MutexImpl) obj, this, atomicOp);
                return null;
            }
        }

        @Override // kotlinx.coroutines.internal.AtomicDesc
        public Object prepare(AtomicOp<?> atomicOp) {
            Empty empty;
            Symbol symbol;
            PrepareOp prepareOp = new PrepareOp(atomicOp);
            MutexImpl mutexImpl = this.mutex;
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = MutexImpl._state$FU;
            empty = MutexKt.EMPTY_UNLOCKED;
            if (!AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(atomicReferenceFieldUpdater, mutexImpl, empty, prepareOp)) {
                symbol = MutexKt.LOCK_FAIL;
                return symbol;
            }
            return prepareOp.perform(this.mutex);
        }

        @Override // kotlinx.coroutines.internal.AtomicDesc
        public void complete(AtomicOp<?> atomicOp, Object obj) {
            Empty empty;
            if (obj != null) {
                empty = MutexKt.EMPTY_UNLOCKED;
            } else {
                empty = this.owner == null ? MutexKt.EMPTY_LOCKED : new Empty(this.owner);
            }
            AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(MutexImpl._state$FU, this.mutex, atomicOp, empty);
        }
    }

    @Override // kotlinx.coroutines.sync.Mutex
    public boolean holdsLock(Object obj) {
        Object obj2 = this._state;
        if (obj2 instanceof Empty) {
            if (((Empty) obj2).locked == obj) {
                return true;
            }
        } else if ((obj2 instanceof LockedQueue) && ((LockedQueue) obj2).owner == obj) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Mutex.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0016R\u0012\u0010\u0002\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006\u0007"}, d2 = {"Lkotlinx/coroutines/sync/MutexImpl$LockedQueue;", "Lkotlinx/coroutines/internal/LockFreeLinkedListHead;", "owner", "", "(Ljava/lang/Object;)V", "toString", "", "kotlinx-coroutines-core"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* loaded from: classes.dex */
    public static final class LockedQueue extends LockFreeLinkedListHead {
        public Object owner;

        public LockedQueue(Object obj) {
            this.owner = obj;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode
        public String toString() {
            return "LockedQueue[" + this.owner + ']';
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Mutex.kt */
    @Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\b¢\u0004\u0018\u00002\u00020\u000f2\u00020\u0010B\u0011\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0001¢\u0006\u0004\b\u0003\u0010\u0004J\u000f\u0010\u0006\u001a\u00020\u0005H&¢\u0006\u0004\b\u0006\u0010\u0007J\r\u0010\b\u001a\u00020\u0005¢\u0006\u0004\b\b\u0010\u0007J\r\u0010\n\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u000f\u0010\f\u001a\u00020\tH&¢\u0006\u0004\b\f\u0010\u000bR\u0016\u0010\u0002\u001a\u0004\u0018\u00010\u00018\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b\u0002\u0010\r¨\u0006\u000e"}, d2 = {"Lkotlinx/coroutines/sync/MutexImpl$LockWaiter;", "", "owner", "<init>", "(Lkotlinx/coroutines/sync/MutexImpl;Ljava/lang/Object;)V", "", "completeResumeLockWaiter", "()V", "dispose", "", "take", "()Z", "tryResumeLockWaiter", "Ljava/lang/Object;", "kotlinx-coroutines-core", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "Lkotlinx/coroutines/DisposableHandle;"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* loaded from: classes.dex */
    public abstract class LockWaiter extends LockFreeLinkedListNode implements DisposableHandle {
        private static final /* synthetic */ AtomicIntegerFieldUpdater isTaken$FU = AtomicIntegerFieldUpdater.newUpdater(LockWaiter.class, "isTaken");
        private volatile /* synthetic */ int isTaken = 0;
        public final Object owner;

        public abstract void completeResumeLockWaiter();

        public abstract boolean tryResumeLockWaiter();

        public LockWaiter(Object obj) {
            this.owner = obj;
        }

        public final boolean take() {
            return isTaken$FU.compareAndSet(this, 0, 1);
        }

        @Override // kotlinx.coroutines.DisposableHandle
        public final void dispose() {
            mo1638remove();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Mutex.kt */
    @Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0082\u0004\u0018\u00002\u00060\u0001R\u00020\u0002B\u001d\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\u0002\u0010\bJ\b\u0010\t\u001a\u00020\u0007H\u0016J\b\u0010\n\u001a\u00020\u000bH\u0016J\b\u0010\f\u001a\u00020\rH\u0016R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lkotlinx/coroutines/sync/MutexImpl$LockCont;", "Lkotlinx/coroutines/sync/MutexImpl$LockWaiter;", "Lkotlinx/coroutines/sync/MutexImpl;", "owner", "", "cont", "Lkotlinx/coroutines/CancellableContinuation;", "", "(Lkotlinx/coroutines/sync/MutexImpl;Ljava/lang/Object;Lkotlinx/coroutines/CancellableContinuation;)V", "completeResumeLockWaiter", "toString", "", "tryResumeLockWaiter", "", "kotlinx-coroutines-core"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* loaded from: classes.dex */
    public final class LockCont extends LockWaiter {
        private final CancellableContinuation<Unit> cont;

        /* JADX WARN: Multi-variable type inference failed */
        public LockCont(Object obj, CancellableContinuation<? super Unit> cancellableContinuation) {
            super(obj);
            this.cont = cancellableContinuation;
        }

        @Override // kotlinx.coroutines.sync.MutexImpl.LockWaiter
        public boolean tryResumeLockWaiter() {
            if (take()) {
                CancellableContinuation<Unit> cancellableContinuation = this.cont;
                Unit unit = Unit.INSTANCE;
                final MutexImpl mutexImpl = MutexImpl.this;
                return cancellableContinuation.tryResume(unit, null, new Function1<Throwable, Unit>() { // from class: kotlinx.coroutines.sync.MutexImpl$LockCont$tryResumeLockWaiter$1
                    /* JADX INFO: Access modifiers changed from: package-private */
                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    {
                        super(1);
                    }

                    @Override // kotlin.jvm.functions.Function1
                    public /* bridge */ /* synthetic */ Unit invoke(Throwable th) {
                        invoke2(th);
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke  reason: avoid collision after fix types in other method */
                    public final void invoke2(Throwable th) {
                        MutexImpl.this.unlock(this.owner);
                    }
                }) != null;
            }
            return false;
        }

        @Override // kotlinx.coroutines.sync.MutexImpl.LockWaiter
        public void completeResumeLockWaiter() {
            this.cont.completeResume(CancellableContinuationImplKt.RESUME_TOKEN);
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode
        public String toString() {
            return "LockCont[" + this.owner + ", " + this.cont + "] for " + MutexImpl.this;
        }
    }

    /* compiled from: Mutex.kt */
    @Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0082\u0004\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00060\u0002R\u00020\u0003BD\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00028\u00000\u0007\u0012\"\u0010\b\u001a\u001e\b\u0001\u0012\u0004\u0012\u00020\n\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u000b\u0012\u0006\u0012\u0004\u0018\u00010\u00050\tø\u0001\u0000¢\u0006\u0002\u0010\fJ\b\u0010\u000e\u001a\u00020\u000fH\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\u0013H\u0016R1\u0010\b\u001a\u001e\b\u0001\u0012\u0004\u0012\u00020\n\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u000b\u0012\u0006\u0012\u0004\u0018\u00010\u00050\t8\u0006X\u0087\u0004ø\u0001\u0000¢\u0006\u0004\n\u0002\u0010\rR\u0016\u0010\u0006\u001a\b\u0012\u0004\u0012\u00028\u00000\u00078\u0006X\u0087\u0004¢\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0014"}, d2 = {"Lkotlinx/coroutines/sync/MutexImpl$LockSelect;", "R", "Lkotlinx/coroutines/sync/MutexImpl$LockWaiter;", "Lkotlinx/coroutines/sync/MutexImpl;", "owner", "", "select", "Lkotlinx/coroutines/selects/SelectInstance;", "block", "Lkotlin/Function2;", "Lkotlinx/coroutines/sync/Mutex;", "Lkotlin/coroutines/Continuation;", "(Lkotlinx/coroutines/sync/MutexImpl;Ljava/lang/Object;Lkotlinx/coroutines/selects/SelectInstance;Lkotlin/jvm/functions/Function2;)V", "Lkotlin/jvm/functions/Function2;", "completeResumeLockWaiter", "", "toString", "", "tryResumeLockWaiter", "", "kotlinx-coroutines-core"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* loaded from: classes.dex */
    private final class LockSelect<R> extends LockWaiter {
        public final Function2<Mutex, Continuation<? super R>, Object> block;
        public final SelectInstance<R> select;

        /* JADX WARN: Multi-variable type inference failed */
        public LockSelect(Object obj, SelectInstance<? super R> selectInstance, Function2<? super Mutex, ? super Continuation<? super R>, ? extends Object> function2) {
            super(obj);
            this.select = selectInstance;
            this.block = function2;
        }

        @Override // kotlinx.coroutines.sync.MutexImpl.LockWaiter
        public boolean tryResumeLockWaiter() {
            return take() && this.select.trySelect();
        }

        @Override // kotlinx.coroutines.sync.MutexImpl.LockWaiter
        public void completeResumeLockWaiter() {
            Function2<Mutex, Continuation<? super R>, Object> function2 = this.block;
            MutexImpl mutexImpl = MutexImpl.this;
            Continuation<R> completion = this.select.getCompletion();
            final MutexImpl mutexImpl2 = MutexImpl.this;
            CancellableKt.startCoroutineCancellable(function2, mutexImpl, completion, new Function1<Throwable, Unit>() { // from class: kotlinx.coroutines.sync.MutexImpl$LockSelect$completeResumeLockWaiter$1
                /* JADX INFO: Access modifiers changed from: package-private */
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(1);
                }

                @Override // kotlin.jvm.functions.Function1
                public /* bridge */ /* synthetic */ Unit invoke(Throwable th) {
                    invoke2(th);
                    return Unit.INSTANCE;
                }

                /* renamed from: invoke  reason: avoid collision after fix types in other method */
                public final void invoke2(Throwable th) {
                    MutexImpl.this.unlock(this.owner);
                }
            });
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode
        public String toString() {
            return "LockSelect[" + this.owner + ", " + this.select + "] for " + MutexImpl.this;
        }
    }

    /* compiled from: Mutex.kt */
    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005J\u001a\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00022\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0016J\u0012\u0010\u000b\u001a\u0004\u0018\u00010\n2\u0006\u0010\b\u001a\u00020\u0002H\u0016R\u0010\u0010\u0003\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"Lkotlinx/coroutines/sync/MutexImpl$UnlockOp;", "Lkotlinx/coroutines/internal/AtomicOp;", "Lkotlinx/coroutines/sync/MutexImpl;", "queue", "Lkotlinx/coroutines/sync/MutexImpl$LockedQueue;", "(Lkotlinx/coroutines/sync/MutexImpl$LockedQueue;)V", "complete", "", "affected", "failure", "", "prepare", "kotlinx-coroutines-core"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* loaded from: classes.dex */
    private static final class UnlockOp extends AtomicOp<MutexImpl> {
        public final LockedQueue queue;

        public UnlockOp(LockedQueue lockedQueue) {
            this.queue = lockedQueue;
        }

        @Override // kotlinx.coroutines.internal.AtomicOp
        public Object prepare(MutexImpl mutexImpl) {
            Symbol symbol;
            if (this.queue.isEmpty()) {
                return null;
            }
            symbol = MutexKt.UNLOCK_FAIL;
            return symbol;
        }

        @Override // kotlinx.coroutines.internal.AtomicOp
        public void complete(MutexImpl mutexImpl, Object obj) {
            AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(MutexImpl._state$FU, mutexImpl, this, obj == null ? MutexKt.EMPTY_UNLOCKED : this.queue);
        }
    }

    @Override // kotlinx.coroutines.sync.Mutex
    public boolean isLocked() {
        Symbol symbol;
        while (true) {
            Object obj = this._state;
            if (obj instanceof Empty) {
                Object obj2 = ((Empty) obj).locked;
                symbol = MutexKt.UNLOCKED;
                return obj2 != symbol;
            } else if (obj instanceof LockedQueue) {
                return true;
            } else {
                if (!(obj instanceof OpDescriptor)) {
                    throw new IllegalStateException(Intrinsics.stringPlus("Illegal state ", obj).toString());
                }
                ((OpDescriptor) obj).perform(this);
            }
        }
    }

    @Override // kotlinx.coroutines.sync.Mutex
    public boolean tryLock(Object obj) {
        Symbol symbol;
        while (true) {
            Object obj2 = this._state;
            if (obj2 instanceof Empty) {
                Object obj3 = ((Empty) obj2).locked;
                symbol = MutexKt.UNLOCKED;
                if (obj3 != symbol) {
                    return false;
                }
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_state$FU, this, obj2, obj == null ? MutexKt.EMPTY_LOCKED : new Empty(obj))) {
                    return true;
                }
            } else if (obj2 instanceof LockedQueue) {
                if (((LockedQueue) obj2).owner != obj) {
                    return false;
                }
                throw new IllegalStateException(Intrinsics.stringPlus("Already locked by ", obj).toString());
            } else if (!(obj2 instanceof OpDescriptor)) {
                throw new IllegalStateException(Intrinsics.stringPlus("Illegal state ", obj2).toString());
            } else {
                ((OpDescriptor) obj2).perform(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object lockSuspend(final Object obj, Continuation<? super Unit> continuation) {
        Symbol symbol;
        LockCont lockCont;
        CancellableContinuationImpl orCreateCancellableContinuation = CancellableContinuationKt.getOrCreateCancellableContinuation(IntrinsicsKt.intercepted(continuation));
        CancellableContinuationImpl cancellableContinuationImpl = orCreateCancellableContinuation;
        LockCont lockCont2 = new LockCont(obj, cancellableContinuationImpl);
        while (true) {
            Object obj2 = this._state;
            if (obj2 instanceof Empty) {
                Empty empty = (Empty) obj2;
                Object obj3 = empty.locked;
                symbol = MutexKt.UNLOCKED;
                if (obj3 != symbol) {
                    AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_state$FU, this, obj2, new LockedQueue(empty.locked));
                } else {
                    if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_state$FU, this, obj2, obj == null ? MutexKt.EMPTY_LOCKED : new Empty(obj))) {
                        cancellableContinuationImpl.resume(Unit.INSTANCE, new Function1<Throwable, Unit>() { // from class: kotlinx.coroutines.sync.MutexImpl$lockSuspend$2$1$1
                            /* JADX INFO: Access modifiers changed from: package-private */
                            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                            {
                                super(1);
                            }

                            @Override // kotlin.jvm.functions.Function1
                            public /* bridge */ /* synthetic */ Unit invoke(Throwable th) {
                                invoke2(th);
                                return Unit.INSTANCE;
                            }

                            /* renamed from: invoke  reason: avoid collision after fix types in other method */
                            public final void invoke2(Throwable th) {
                                MutexImpl.this.unlock(obj);
                            }
                        });
                        break;
                    }
                }
            } else if (obj2 instanceof LockedQueue) {
                LockedQueue lockedQueue = (LockedQueue) obj2;
                if (!(lockedQueue.owner != obj)) {
                    throw new IllegalStateException(Intrinsics.stringPlus("Already locked by ", obj).toString());
                }
                lockCont = lockCont2;
                lockedQueue.addLast(lockCont);
                if (this._state == obj2 || !lockCont2.take()) {
                    break;
                }
                lockCont2 = new LockCont(obj, cancellableContinuationImpl);
            } else if (!(obj2 instanceof OpDescriptor)) {
                throw new IllegalStateException(Intrinsics.stringPlus("Illegal state ", obj2).toString());
            } else {
                ((OpDescriptor) obj2).perform(this);
            }
        }
        CancellableContinuationKt.removeOnCancellation(cancellableContinuationImpl, lockCont);
        Object result = orCreateCancellableContinuation.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? result : Unit.INSTANCE;
    }

    @Override // kotlinx.coroutines.sync.Mutex
    public void unlock(Object obj) {
        Empty empty;
        Symbol symbol;
        while (true) {
            Object obj2 = this._state;
            if (obj2 instanceof Empty) {
                if (obj == null) {
                    Object obj3 = ((Empty) obj2).locked;
                    symbol = MutexKt.UNLOCKED;
                    if (!(obj3 != symbol)) {
                        throw new IllegalStateException("Mutex is not locked".toString());
                    }
                } else {
                    Empty empty2 = (Empty) obj2;
                    if (!(empty2.locked == obj)) {
                        throw new IllegalStateException(("Mutex is locked by " + empty2.locked + " but expected " + obj).toString());
                    }
                }
                AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _state$FU;
                empty = MutexKt.EMPTY_UNLOCKED;
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(atomicReferenceFieldUpdater, this, obj2, empty)) {
                    return;
                }
            } else if (obj2 instanceof OpDescriptor) {
                ((OpDescriptor) obj2).perform(this);
            } else if (obj2 instanceof LockedQueue) {
                if (obj != null) {
                    LockedQueue lockedQueue = (LockedQueue) obj2;
                    if (!(lockedQueue.owner == obj)) {
                        throw new IllegalStateException(("Mutex is locked by " + lockedQueue.owner + " but expected " + obj).toString());
                    }
                }
                LockedQueue lockedQueue2 = (LockedQueue) obj2;
                LockFreeLinkedListNode removeFirstOrNull = lockedQueue2.removeFirstOrNull();
                if (removeFirstOrNull == null) {
                    UnlockOp unlockOp = new UnlockOp(lockedQueue2);
                    if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(_state$FU, this, obj2, unlockOp) && unlockOp.perform(this) == null) {
                        return;
                    }
                } else {
                    LockWaiter lockWaiter = (LockWaiter) removeFirstOrNull;
                    if (lockWaiter.tryResumeLockWaiter()) {
                        Object obj4 = lockWaiter.owner;
                        if (obj4 == null) {
                            obj4 = MutexKt.LOCKED;
                        }
                        lockedQueue2.owner = obj4;
                        lockWaiter.completeResumeLockWaiter();
                        return;
                    }
                }
            } else {
                throw new IllegalStateException(Intrinsics.stringPlus("Illegal state ", obj2).toString());
            }
        }
    }

    public String toString() {
        while (true) {
            Object obj = this._state;
            if (obj instanceof Empty) {
                return "Mutex[" + ((Empty) obj).locked + ']';
            }
            if (!(obj instanceof OpDescriptor)) {
                if (obj instanceof LockedQueue) {
                    return "Mutex[" + ((LockedQueue) obj).owner + ']';
                }
                throw new IllegalStateException(Intrinsics.stringPlus("Illegal state ", obj).toString());
            }
            ((OpDescriptor) obj).perform(this);
        }
    }
}
