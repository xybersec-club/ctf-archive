package kotlinx.coroutines.internal;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Metadata;
import kotlin.SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.DebugKt;
/* compiled from: LockFreeLinkedList.kt */
@Metadata(d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u0000\n\u0002\b\f\b\u0017\u0018\u00002\u00020C:\u0005JKLMNB\u0007¢\u0006\u0004\b\u0001\u0010\u0002J\u0019\u0010\u0006\u001a\u00020\u00052\n\u0010\u0004\u001a\u00060\u0000j\u0002`\u0003¢\u0006\u0004\b\u0006\u0010\u0007J,\u0010\u000b\u001a\u00020\t2\n\u0010\u0004\u001a\u00060\u0000j\u0002`\u00032\u000e\b\u0004\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0086\b¢\u0006\u0004\b\u000b\u0010\fJ4\u0010\u000f\u001a\u00020\t2\n\u0010\u0004\u001a\u00060\u0000j\u0002`\u00032\u0016\u0010\u000e\u001a\u0012\u0012\b\u0012\u00060\u0000j\u0002`\u0003\u0012\u0004\u0012\u00020\t0\rH\u0086\b¢\u0006\u0004\b\u000f\u0010\u0010JD\u0010\u0011\u001a\u00020\t2\n\u0010\u0004\u001a\u00060\u0000j\u0002`\u00032\u0016\u0010\u000e\u001a\u0012\u0012\b\u0012\u00060\u0000j\u0002`\u0003\u0012\u0004\u0012\u00020\t0\r2\u000e\b\u0004\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0086\b¢\u0006\u0004\b\u0011\u0010\u0012J'\u0010\u0014\u001a\u00020\t2\n\u0010\u0004\u001a\u00060\u0000j\u0002`\u00032\n\u0010\u0013\u001a\u00060\u0000j\u0002`\u0003H\u0001¢\u0006\u0004\b\u0014\u0010\u0015J\u0019\u0010\u0016\u001a\u00020\t2\n\u0010\u0004\u001a\u00060\u0000j\u0002`\u0003¢\u0006\u0004\b\u0016\u0010\u0017J\"\u0010\u001a\u001a\n\u0018\u00010\u0000j\u0004\u0018\u0001`\u00032\b\u0010\u0019\u001a\u0004\u0018\u00010\u0018H\u0082\u0010¢\u0006\u0004\b\u001a\u0010\u001bJ)\u0010\u001e\u001a\b\u0012\u0004\u0012\u00028\u00000\u001d\"\f\b\u0000\u0010\u001c*\u00060\u0000j\u0002`\u00032\u0006\u0010\u0004\u001a\u00028\u0000¢\u0006\u0004\b\u001e\u0010\u001fJ\u0017\u0010!\u001a\f\u0012\b\u0012\u00060\u0000j\u0002`\u00030 ¢\u0006\u0004\b!\u0010\"J \u0010$\u001a\u00060\u0000j\u0002`\u00032\n\u0010#\u001a\u00060\u0000j\u0002`\u0003H\u0082\u0010¢\u0006\u0004\b$\u0010%J\u001b\u0010&\u001a\u00020\u00052\n\u0010\u0013\u001a\u00060\u0000j\u0002`\u0003H\u0002¢\u0006\u0004\b&\u0010\u0007J\r\u0010'\u001a\u00020\u0005¢\u0006\u0004\b'\u0010\u0002J\u000f\u0010(\u001a\u00020\u0005H\u0001¢\u0006\u0004\b(\u0010\u0002J,\u0010*\u001a\u00020)2\n\u0010\u0004\u001a\u00060\u0000j\u0002`\u00032\u000e\b\u0004\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0081\b¢\u0006\u0004\b*\u0010+J\u0017\u0010,\u001a\n\u0018\u00010\u0000j\u0004\u0018\u0001`\u0003H\u0014¢\u0006\u0004\b,\u0010-J\u000f\u0010.\u001a\u00020\tH\u0016¢\u0006\u0004\b.\u0010/J.\u00100\u001a\u0004\u0018\u00018\u0000\"\u0006\b\u0000\u0010\u001c\u0018\u00012\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\t0\rH\u0086\b¢\u0006\u0004\b0\u00101J\u0015\u00102\u001a\n\u0018\u00010\u0000j\u0004\u0018\u0001`\u0003¢\u0006\u0004\b2\u0010-J\u0017\u00103\u001a\n\u0018\u00010\u0000j\u0004\u0018\u0001`\u0003H\u0001¢\u0006\u0004\b3\u0010-J\u000f\u00105\u001a\u000204H\u0002¢\u0006\u0004\b5\u00106J\u000f\u00108\u001a\u000207H\u0016¢\u0006\u0004\b8\u00109J/\u0010<\u001a\u00020;2\n\u0010\u0004\u001a\u00060\u0000j\u0002`\u00032\n\u0010\u0013\u001a\u00060\u0000j\u0002`\u00032\u0006\u0010:\u001a\u00020)H\u0001¢\u0006\u0004\b<\u0010=J'\u0010A\u001a\u00020\u00052\n\u0010>\u001a\u00060\u0000j\u0002`\u00032\n\u0010\u0013\u001a\u00060\u0000j\u0002`\u0003H\u0000¢\u0006\u0004\b?\u0010@R\u0014\u0010B\u001a\u00020\t8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\bB\u0010/R\u0011\u0010\u0013\u001a\u00020C8F¢\u0006\u0006\u001a\u0004\bD\u0010ER\u0015\u0010G\u001a\u00060\u0000j\u0002`\u00038F¢\u0006\u0006\u001a\u0004\bF\u0010-R\u0015\u0010I\u001a\u00060\u0000j\u0002`\u00038F¢\u0006\u0006\u001a\u0004\bH\u0010-¨\u0006O"}, d2 = {"Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "<init>", "()V", "Lkotlinx/coroutines/internal/Node;", "node", "", "addLast", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)V", "Lkotlin/Function0;", "", "condition", "addLastIf", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlin/jvm/functions/Function0;)Z", "Lkotlin/Function1;", "predicate", "addLastIfPrev", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlin/jvm/functions/Function1;)Z", "addLastIfPrevAndIf", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;)Z", "next", "addNext", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)Z", "addOneIfEmpty", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)Z", "Lkotlinx/coroutines/internal/OpDescriptor;", "op", "correctPrev", "(Lkotlinx/coroutines/internal/OpDescriptor;)Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "T", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AddLastDesc;", "describeAddLast", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AddLastDesc;", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$RemoveFirstDesc;", "describeRemoveFirst", "()Lkotlinx/coroutines/internal/LockFreeLinkedListNode$RemoveFirstDesc;", "current", "findPrevNonRemoved", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "finishAdd", "helpRemove", "helpRemovePrev", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$CondAddOp;", "makeCondAddOp", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlin/jvm/functions/Function0;)Lkotlinx/coroutines/internal/LockFreeLinkedListNode$CondAddOp;", "nextIfRemoved", "()Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "remove", "()Z", "removeFirstIfIsInstanceOfOrPeekIf", "(Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "removeFirstOrNull", "removeOrNext", "Lkotlinx/coroutines/internal/Removed;", "removed", "()Lkotlinx/coroutines/internal/Removed;", "", "toString", "()Ljava/lang/String;", "condAdd", "", "tryCondAddNext", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode$CondAddOp;)I", "prev", "validateNode$kotlinx_coroutines_core", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)V", "validateNode", "isRemoved", "", "getNext", "()Ljava/lang/Object;", "getNextNode", "nextNode", "getPrevNode", "prevNode", "AbstractAtomicDesc", "AddLastDesc", "CondAddOp", "PrepareOp", "RemoveFirstDesc", "kotlinx-coroutines-core"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes.dex */
public class LockFreeLinkedListNode {
    static final /* synthetic */ AtomicReferenceFieldUpdater _next$FU = AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListNode.class, Object.class, "_next");
    static final /* synthetic */ AtomicReferenceFieldUpdater _prev$FU = AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListNode.class, Object.class, "_prev");
    private static final /* synthetic */ AtomicReferenceFieldUpdater _removedRef$FU = AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListNode.class, Object.class, "_removedRef");
    volatile /* synthetic */ Object _next = this;
    volatile /* synthetic */ Object _prev = this;
    private volatile /* synthetic */ Object _removedRef = null;

    /* JADX INFO: Access modifiers changed from: private */
    public final Removed removed() {
        Removed removed = (Removed) this._removedRef;
        if (removed == null) {
            Removed it = new Removed(this);
            _removedRef$FU.lazySet(this, it);
            return it;
        }
        return removed;
    }

    /* compiled from: LockFreeLinkedList.kt */
    @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\b!\u0018\u00002\f\u0012\b\u0012\u00060\u0002j\u0002`\u00030\u0001B\u0011\u0012\n\u0010\u0004\u001a\u00060\u0002j\u0002`\u0003¢\u0006\u0002\u0010\u0005J\u001e\u0010\u0007\u001a\u00020\b2\n\u0010\t\u001a\u00060\u0002j\u0002`\u00032\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0016R\u0014\u0010\u0004\u001a\u00060\u0002j\u0002`\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\n\u0018\u00010\u0002j\u0004\u0018\u0001`\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"Lkotlinx/coroutines/internal/LockFreeLinkedListNode$CondAddOp;", "Lkotlinx/coroutines/internal/AtomicOp;", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "Lkotlinx/coroutines/internal/Node;", "newNode", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)V", "oldNext", "complete", "", "affected", "failure", "", "kotlinx-coroutines-core"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* loaded from: classes.dex */
    public static abstract class CondAddOp extends AtomicOp<LockFreeLinkedListNode> {
        public final LockFreeLinkedListNode newNode;
        public LockFreeLinkedListNode oldNext;

        public CondAddOp(LockFreeLinkedListNode newNode) {
            this.newNode = newNode;
        }

        @Override // kotlinx.coroutines.internal.AtomicOp
        public void complete(LockFreeLinkedListNode affected, Object failure) {
            boolean success = failure == null;
            LockFreeLinkedListNode update = success ? this.newNode : this.oldNext;
            if (update != null && SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(LockFreeLinkedListNode._next$FU, affected, this, update) && success) {
                LockFreeLinkedListNode lockFreeLinkedListNode = this.newNode;
                LockFreeLinkedListNode lockFreeLinkedListNode2 = this.oldNext;
                Intrinsics.checkNotNull(lockFreeLinkedListNode2);
                lockFreeLinkedListNode.finishAdd(lockFreeLinkedListNode2);
            }
        }
    }

    public final CondAddOp makeCondAddOp(LockFreeLinkedListNode node, Function0<Boolean> function0) {
        return new LockFreeLinkedListNode$makeCondAddOp$1(function0, node);
    }

    public boolean isRemoved() {
        return getNext() instanceof Removed;
    }

    public final Object getNext() {
        while (true) {
            Object next = this._next;
            if (!(next instanceof OpDescriptor)) {
                return next;
            }
            ((OpDescriptor) next).perform(this);
        }
    }

    public final LockFreeLinkedListNode getNextNode() {
        return LockFreeLinkedListKt.unwrap(getNext());
    }

    public final LockFreeLinkedListNode getPrevNode() {
        LockFreeLinkedListNode correctPrev = correctPrev(null);
        return correctPrev == null ? findPrevNonRemoved((LockFreeLinkedListNode) this._prev) : correctPrev;
    }

    private final LockFreeLinkedListNode findPrevNonRemoved(LockFreeLinkedListNode current) {
        LockFreeLinkedListNode lockFreeLinkedListNode = current;
        while (lockFreeLinkedListNode.isRemoved()) {
            lockFreeLinkedListNode = (LockFreeLinkedListNode) lockFreeLinkedListNode._prev;
        }
        return lockFreeLinkedListNode;
    }

    /* JADX WARN: Incorrect condition in loop: B:4:0x000f */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final boolean addOneIfEmpty(kotlinx.coroutines.internal.LockFreeLinkedListNode r3) {
        /*
            r2 = this;
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater r0 = kotlinx.coroutines.internal.LockFreeLinkedListNode._prev$FU
            r0.lazySet(r3, r2)
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater r0 = kotlinx.coroutines.internal.LockFreeLinkedListNode._next$FU
            r0.lazySet(r3, r2)
        La:
            java.lang.Object r0 = r2.getNext()
            if (r0 == r2) goto L13
            r1 = 0
            return r1
        L13:
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater r1 = kotlinx.coroutines.internal.LockFreeLinkedListNode._next$FU
            boolean r1 = kotlin.SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(r1, r2, r2, r3)
            if (r1 == 0) goto L20
            r3.finishAdd(r2)
            r1 = 1
            return r1
        L20:
            goto La
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.internal.LockFreeLinkedListNode.addOneIfEmpty(kotlinx.coroutines.internal.LockFreeLinkedListNode):boolean");
    }

    public final void addLast(LockFreeLinkedListNode node) {
        do {
        } while (!getPrevNode().addNext(node, this));
    }

    public final <T extends LockFreeLinkedListNode> AddLastDesc<T> describeAddLast(T t) {
        return new AddLastDesc<>(this, t);
    }

    public final boolean addLastIf(LockFreeLinkedListNode node, Function0<Boolean> function0) {
        CondAddOp condAdd = new LockFreeLinkedListNode$makeCondAddOp$1(function0, node);
        while (true) {
            LockFreeLinkedListNode prev = getPrevNode();
            switch (prev.tryCondAddNext(node, this, condAdd)) {
                case 1:
                    return true;
                case 2:
                    return false;
            }
        }
    }

    public final boolean addLastIfPrev(LockFreeLinkedListNode node, Function1<? super LockFreeLinkedListNode, Boolean> function1) {
        LockFreeLinkedListNode prev;
        do {
            prev = getPrevNode();
            if (!function1.invoke(prev).booleanValue()) {
                return false;
            }
        } while (!prev.addNext(node, this));
        return true;
    }

    public final boolean addLastIfPrevAndIf(LockFreeLinkedListNode node, Function1<? super LockFreeLinkedListNode, Boolean> function1, Function0<Boolean> function0) {
        CondAddOp condAdd = new LockFreeLinkedListNode$makeCondAddOp$1(function0, node);
        while (true) {
            LockFreeLinkedListNode prev = getPrevNode();
            if (function1.invoke(prev).booleanValue()) {
                switch (prev.tryCondAddNext(node, this, condAdd)) {
                    case 1:
                        return true;
                    case 2:
                        return false;
                }
            }
            return false;
        }
    }

    public final boolean addNext(LockFreeLinkedListNode node, LockFreeLinkedListNode next) {
        _prev$FU.lazySet(node, this);
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _next$FU;
        atomicReferenceFieldUpdater.lazySet(node, next);
        if (SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(atomicReferenceFieldUpdater, this, next, node)) {
            node.finishAdd(next);
            return true;
        }
        return false;
    }

    public final int tryCondAddNext(LockFreeLinkedListNode node, LockFreeLinkedListNode next, CondAddOp condAdd) {
        _prev$FU.lazySet(node, this);
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _next$FU;
        atomicReferenceFieldUpdater.lazySet(node, next);
        condAdd.oldNext = next;
        if (SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(atomicReferenceFieldUpdater, this, next, condAdd)) {
            return condAdd.perform(this) == null ? 1 : 2;
        }
        return 0;
    }

    public boolean remove() {
        return removeOrNext() == null;
    }

    public final LockFreeLinkedListNode removeOrNext() {
        Object next;
        Removed removed;
        do {
            next = getNext();
            if (next instanceof Removed) {
                return ((Removed) next).ref;
            }
            if (next == this) {
                return (LockFreeLinkedListNode) next;
            }
            removed = ((LockFreeLinkedListNode) next).removed();
        } while (!SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(_next$FU, this, next, removed));
        ((LockFreeLinkedListNode) next).correctPrev(null);
        return null;
    }

    public final void helpRemove() {
        ((Removed) getNext()).ref.helpRemovePrev();
    }

    public final void helpRemovePrev() {
        LockFreeLinkedListNode node = this;
        while (true) {
            Object next = node.getNext();
            if (next instanceof Removed) {
                node = ((Removed) next).ref;
            } else {
                node.correctPrev(null);
                return;
            }
        }
    }

    public final LockFreeLinkedListNode removeFirstOrNull() {
        while (true) {
            LockFreeLinkedListNode first = (LockFreeLinkedListNode) getNext();
            if (first == this) {
                return null;
            }
            if (first.remove()) {
                return first;
            }
            first.helpRemove();
        }
    }

    public final RemoveFirstDesc<LockFreeLinkedListNode> describeRemoveFirst() {
        return new RemoveFirstDesc<>(this);
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [T, java.lang.Object, kotlinx.coroutines.internal.LockFreeLinkedListNode] */
    public final /* synthetic */ <T> T removeFirstIfIsInstanceOfOrPeekIf(Function1<? super T, Boolean> function1) {
        LockFreeLinkedListNode next;
        while (true) {
            LockFreeLinkedListNode lockFreeLinkedListNode = (LockFreeLinkedListNode) getNext();
            if (lockFreeLinkedListNode == this) {
                return null;
            }
            Intrinsics.reifiedOperationMarker(3, "T");
            if (!(lockFreeLinkedListNode instanceof Object)) {
                return null;
            }
            if ((function1.invoke(lockFreeLinkedListNode).booleanValue() && !lockFreeLinkedListNode.isRemoved()) || (next = lockFreeLinkedListNode.removeOrNext()) == null) {
                return lockFreeLinkedListNode;
            }
            next.helpRemovePrev();
        }
    }

    /* compiled from: LockFreeLinkedList.kt */
    @Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\b\u0016\u0018\u0000*\f\b\u0000\u0010\u0003*\u00060\u0001j\u0002`\u00022\u00020!B\u001b\u0012\n\u0010\u0004\u001a\u00060\u0001j\u0002`\u0002\u0012\u0006\u0010\u0005\u001a\u00028\u0000¢\u0006\u0004\b\u0006\u0010\u0007J'\u0010\u000b\u001a\u00020\n2\n\u0010\b\u001a\u00060\u0001j\u0002`\u00022\n\u0010\t\u001a\u00060\u0001j\u0002`\u0002H\u0014¢\u0006\u0004\b\u000b\u0010\u0007J\u0017\u0010\u000e\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\fH\u0016¢\u0006\u0004\b\u000e\u0010\u000fJ#\u0010\u0012\u001a\u00020\u00112\n\u0010\b\u001a\u00060\u0001j\u0002`\u00022\u0006\u0010\t\u001a\u00020\u0010H\u0014¢\u0006\u0004\b\u0012\u0010\u0013J\u001f\u0010\u0016\u001a\n\u0018\u00010\u0001j\u0004\u0018\u0001`\u00022\u0006\u0010\u0015\u001a\u00020\u0014H\u0004¢\u0006\u0004\b\u0016\u0010\u0017J'\u0010\u0018\u001a\u00020\u00102\n\u0010\b\u001a\u00060\u0001j\u0002`\u00022\n\u0010\t\u001a\u00060\u0001j\u0002`\u0002H\u0016¢\u0006\u0004\b\u0018\u0010\u0019R\u001c\u0010\u001c\u001a\n\u0018\u00010\u0001j\u0004\u0018\u0001`\u00028DX\u0084\u0004¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u001bR\u0014\u0010\u0005\u001a\u00028\u00008\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b\u0005\u0010\u001dR\u0018\u0010\u001f\u001a\u00060\u0001j\u0002`\u00028DX\u0084\u0004¢\u0006\u0006\u001a\u0004\b\u001e\u0010\u001bR\u0018\u0010\u0004\u001a\u00060\u0001j\u0002`\u00028\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b\u0004\u0010\u001d¨\u0006 "}, d2 = {"Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AddLastDesc;", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "Lkotlinx/coroutines/internal/Node;", "T", "queue", "node", "<init>", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)V", "affected", "next", "", "finishOnSuccess", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$PrepareOp;", "prepareOp", "finishPrepare", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode$PrepareOp;)V", "", "", "retry", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Ljava/lang/Object;)Z", "Lkotlinx/coroutines/internal/OpDescriptor;", "op", "takeAffectedNode", "(Lkotlinx/coroutines/internal/OpDescriptor;)Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "updatedNext", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)Ljava/lang/Object;", "getAffectedNode", "()Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "affectedNode", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "getOriginalNext", "originalNext", "kotlinx-coroutines-core", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AbstractAtomicDesc;"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* loaded from: classes.dex */
    public static class AddLastDesc<T extends LockFreeLinkedListNode> extends AbstractAtomicDesc {
        private static final /* synthetic */ AtomicReferenceFieldUpdater _affectedNode$FU = AtomicReferenceFieldUpdater.newUpdater(AddLastDesc.class, Object.class, "_affectedNode");
        private volatile /* synthetic */ Object _affectedNode;
        public final T node;
        public final LockFreeLinkedListNode queue;

        public AddLastDesc(LockFreeLinkedListNode queue, T t) {
            this.queue = queue;
            this.node = t;
            if (DebugKt.getASSERTIONS_ENABLED()) {
                if (!(t._next == t && t._prev == t)) {
                    throw new AssertionError();
                }
            }
            this._affectedNode = null;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final LockFreeLinkedListNode takeAffectedNode(OpDescriptor op) {
            return this.queue.correctPrev(op);
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final LockFreeLinkedListNode getAffectedNode() {
            return (LockFreeLinkedListNode) this._affectedNode;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final LockFreeLinkedListNode getOriginalNext() {
            return this.queue;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected boolean retry(LockFreeLinkedListNode affected, Object next) {
            return next != this.queue;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        public void finishPrepare(PrepareOp prepareOp) {
            SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(_affectedNode$FU, this, null, prepareOp.affected);
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        public Object updatedNext(LockFreeLinkedListNode affected, LockFreeLinkedListNode next) {
            SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(LockFreeLinkedListNode._prev$FU, this.node, this.node, affected);
            SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(LockFreeLinkedListNode._next$FU, this.node, this.node, this.queue);
            return this.node;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected void finishOnSuccess(LockFreeLinkedListNode affected, LockFreeLinkedListNode next) {
            this.node.finishAdd(this.queue);
        }
    }

    /* compiled from: LockFreeLinkedList.kt */
    @Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\b\u0016\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020(B\u0013\u0012\n\u0010\u0004\u001a\u00060\u0002j\u0002`\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u001d\u0010\t\u001a\u0004\u0018\u00010\b2\n\u0010\u0007\u001a\u00060\u0002j\u0002`\u0003H\u0014¢\u0006\u0004\b\t\u0010\nJ'\u0010\r\u001a\u00020\f2\n\u0010\u0007\u001a\u00060\u0002j\u0002`\u00032\n\u0010\u000b\u001a\u00060\u0002j\u0002`\u0003H\u0004¢\u0006\u0004\b\r\u0010\u000eJ\u0017\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u000fH\u0016¢\u0006\u0004\b\u0011\u0010\u0012J#\u0010\u0014\u001a\u00020\u00132\n\u0010\u0007\u001a\u00060\u0002j\u0002`\u00032\u0006\u0010\u000b\u001a\u00020\bH\u0004¢\u0006\u0004\b\u0014\u0010\u0015J\u001f\u0010\u0018\u001a\n\u0018\u00010\u0002j\u0004\u0018\u0001`\u00032\u0006\u0010\u0017\u001a\u00020\u0016H\u0004¢\u0006\u0004\b\u0018\u0010\u0019J%\u0010\u001a\u001a\u00020\b2\n\u0010\u0007\u001a\u00060\u0002j\u0002`\u00032\n\u0010\u000b\u001a\u00060\u0002j\u0002`\u0003¢\u0006\u0004\b\u001a\u0010\u001bR\u001c\u0010\u001e\u001a\n\u0018\u00010\u0002j\u0004\u0018\u0001`\u00038DX\u0084\u0004¢\u0006\u0006\u001a\u0004\b\u001c\u0010\u001dR\u001c\u0010 \u001a\n\u0018\u00010\u0002j\u0004\u0018\u0001`\u00038DX\u0084\u0004¢\u0006\u0006\u001a\u0004\b\u001f\u0010\u001dR\u0018\u0010\u0004\u001a\u00060\u0002j\u0002`\u00038\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b\u0004\u0010!R\u0017\u0010&\u001a\u00028\u00008F¢\u0006\f\u0012\u0004\b$\u0010%\u001a\u0004\b\"\u0010#¨\u0006'"}, d2 = {"Lkotlinx/coroutines/internal/LockFreeLinkedListNode$RemoveFirstDesc;", "T", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "Lkotlinx/coroutines/internal/Node;", "queue", "<init>", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)V", "affected", "", "failure", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)Ljava/lang/Object;", "next", "", "finishOnSuccess", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)V", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$PrepareOp;", "prepareOp", "finishPrepare", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode$PrepareOp;)V", "", "retry", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Ljava/lang/Object;)Z", "Lkotlinx/coroutines/internal/OpDescriptor;", "op", "takeAffectedNode", "(Lkotlinx/coroutines/internal/OpDescriptor;)Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "updatedNext", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)Ljava/lang/Object;", "getAffectedNode", "()Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "affectedNode", "getOriginalNext", "originalNext", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "getResult", "()Ljava/lang/Object;", "getResult$annotations", "()V", "result", "kotlinx-coroutines-core", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AbstractAtomicDesc;"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* loaded from: classes.dex */
    public static class RemoveFirstDesc<T> extends AbstractAtomicDesc {
        private static final /* synthetic */ AtomicReferenceFieldUpdater _affectedNode$FU = AtomicReferenceFieldUpdater.newUpdater(RemoveFirstDesc.class, Object.class, "_affectedNode");
        private static final /* synthetic */ AtomicReferenceFieldUpdater _originalNext$FU = AtomicReferenceFieldUpdater.newUpdater(RemoveFirstDesc.class, Object.class, "_originalNext");
        private volatile /* synthetic */ Object _affectedNode = null;
        private volatile /* synthetic */ Object _originalNext = null;
        public final LockFreeLinkedListNode queue;

        public static /* synthetic */ void getResult$annotations() {
        }

        public RemoveFirstDesc(LockFreeLinkedListNode queue) {
            this.queue = queue;
        }

        public final T getResult() {
            T t = (T) getAffectedNode();
            Intrinsics.checkNotNull(t);
            return t;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final LockFreeLinkedListNode takeAffectedNode(OpDescriptor op) {
            LockFreeLinkedListNode $this$loop$iv = this.queue;
            while (true) {
                Object next = $this$loop$iv._next;
                if (next instanceof OpDescriptor) {
                    if (op.isEarlierThan((OpDescriptor) next)) {
                        return null;
                    }
                    ((OpDescriptor) next).perform(this.queue);
                } else {
                    return (LockFreeLinkedListNode) next;
                }
            }
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final LockFreeLinkedListNode getAffectedNode() {
            return (LockFreeLinkedListNode) this._affectedNode;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final LockFreeLinkedListNode getOriginalNext() {
            return (LockFreeLinkedListNode) this._originalNext;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected Object failure(LockFreeLinkedListNode affected) {
            if (affected == this.queue) {
                return LockFreeLinkedListKt.getLIST_EMPTY();
            }
            return null;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final boolean retry(LockFreeLinkedListNode affected, Object next) {
            if (next instanceof Removed) {
                ((Removed) next).ref.helpRemovePrev();
                return true;
            }
            return false;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        public void finishPrepare(PrepareOp prepareOp) {
            SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(_affectedNode$FU, this, null, prepareOp.affected);
            SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(_originalNext$FU, this, null, prepareOp.next);
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        public final Object updatedNext(LockFreeLinkedListNode affected, LockFreeLinkedListNode next) {
            return next.removed();
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final void finishOnSuccess(LockFreeLinkedListNode affected, LockFreeLinkedListNode next) {
            next.correctPrev(null);
        }
    }

    /* compiled from: LockFreeLinkedList.kt */
    @Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B%\u0012\n\u0010\u0002\u001a\u00060\u0003j\u0002`\u0004\u0012\n\u0010\u0005\u001a\u00060\u0003j\u0002`\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\u0006\u0010\r\u001a\u00020\u000eJ\u0014\u0010\u000f\u001a\u0004\u0018\u00010\u00102\b\u0010\u0002\u001a\u0004\u0018\u00010\u0010H\u0016J\b\u0010\u0011\u001a\u00020\u0012H\u0016R\u0014\u0010\u0002\u001a\u00060\u0003j\u0002`\u00048\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\t\u001a\u0006\u0012\u0002\b\u00030\n8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u0010\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\u00060\u0003j\u0002`\u00048\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"}, d2 = {"Lkotlinx/coroutines/internal/LockFreeLinkedListNode$PrepareOp;", "Lkotlinx/coroutines/internal/OpDescriptor;", "affected", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "Lkotlinx/coroutines/internal/Node;", "next", "desc", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AbstractAtomicDesc;", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AbstractAtomicDesc;)V", "atomicOp", "Lkotlinx/coroutines/internal/AtomicOp;", "getAtomicOp", "()Lkotlinx/coroutines/internal/AtomicOp;", "finishPrepare", "", "perform", "", "toString", "", "kotlinx-coroutines-core"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* loaded from: classes.dex */
    public static final class PrepareOp extends OpDescriptor {
        public final LockFreeLinkedListNode affected;
        public final AbstractAtomicDesc desc;
        public final LockFreeLinkedListNode next;

        public PrepareOp(LockFreeLinkedListNode affected, LockFreeLinkedListNode next, AbstractAtomicDesc desc) {
            this.affected = affected;
            this.next = next;
            this.desc = desc;
        }

        @Override // kotlinx.coroutines.internal.OpDescriptor
        public AtomicOp<?> getAtomicOp() {
            return this.desc.getAtomicOp();
        }

        @Override // kotlinx.coroutines.internal.OpDescriptor
        public Object perform(Object affected) {
            Object consensus;
            Object update;
            if (DebugKt.getASSERTIONS_ENABLED()) {
                if (!(affected == this.affected)) {
                    throw new AssertionError();
                }
            }
            if (affected == null) {
                throw new NullPointerException("null cannot be cast to non-null type kotlinx.coroutines.internal.LockFreeLinkedListNode{ kotlinx.coroutines.internal.LockFreeLinkedListKt.Node }");
            }
            LockFreeLinkedListNode lockFreeLinkedListNode = (LockFreeLinkedListNode) affected;
            Object decision = this.desc.onPrepare(this);
            if (decision == LockFreeLinkedList_commonKt.REMOVE_PREPARED) {
                LockFreeLinkedListNode next = this.next;
                Removed removed = next.removed();
                if (SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(LockFreeLinkedListNode._next$FU, (LockFreeLinkedListNode) affected, this, removed)) {
                    this.desc.onRemoved((LockFreeLinkedListNode) affected);
                    next.correctPrev(null);
                }
                return LockFreeLinkedList_commonKt.REMOVE_PREPARED;
            }
            if (decision != null) {
                consensus = getAtomicOp().decide(decision);
            } else {
                consensus = getAtomicOp().getConsensus();
            }
            if (consensus == AtomicKt.NO_DECISION) {
                update = getAtomicOp();
            } else {
                update = consensus == null ? this.desc.updatedNext((LockFreeLinkedListNode) affected, this.next) : this.next;
            }
            SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(LockFreeLinkedListNode._next$FU, (LockFreeLinkedListNode) affected, this, update);
            return null;
        }

        public final void finishPrepare() {
            this.desc.finishPrepare(this);
        }

        @Override // kotlinx.coroutines.internal.OpDescriptor
        public String toString() {
            return "PrepareOp(op=" + getAtomicOp() + ')';
        }
    }

    /* compiled from: LockFreeLinkedList.kt */
    @Metadata(d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b&\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001c\u0010\n\u001a\u00020\u000b2\n\u0010\f\u001a\u0006\u0012\u0002\b\u00030\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fJ\u0016\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\n\u0010\u0010\u001a\u00060\u0004j\u0002`\u0005H\u0014J \u0010\u0011\u001a\u00020\u000b2\n\u0010\u0010\u001a\u00060\u0004j\u0002`\u00052\n\u0010\u0012\u001a\u00060\u0004j\u0002`\u0005H$J\u0010\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\u0014\u001a\u00020\u0015H&J\u0012\u0010\u0016\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J\u0014\u0010\u0017\u001a\u00020\u000b2\n\u0010\u0010\u001a\u00060\u0004j\u0002`\u0005H\u0016J\u0014\u0010\u0018\u001a\u0004\u0018\u00010\u000f2\n\u0010\f\u001a\u0006\u0012\u0002\b\u00030\rJ\u001c\u0010\u0019\u001a\u00020\u001a2\n\u0010\u0010\u001a\u00060\u0004j\u0002`\u00052\u0006\u0010\u0012\u001a\u00020\u000fH\u0014J\u0018\u0010\u001b\u001a\n\u0018\u00010\u0004j\u0004\u0018\u0001`\u00052\u0006\u0010\f\u001a\u00020\u001cH\u0014J \u0010\u001d\u001a\u00020\u000f2\n\u0010\u0010\u001a\u00060\u0004j\u0002`\u00052\n\u0010\u0012\u001a\u00060\u0004j\u0002`\u0005H&R\u001a\u0010\u0003\u001a\n\u0018\u00010\u0004j\u0004\u0018\u0001`\u0005X¤\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u001a\u0010\b\u001a\n\u0018\u00010\u0004j\u0004\u0018\u0001`\u0005X¤\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\u0007¨\u0006\u001e"}, d2 = {"Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AbstractAtomicDesc;", "Lkotlinx/coroutines/internal/AtomicDesc;", "()V", "affectedNode", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "Lkotlinx/coroutines/internal/Node;", "getAffectedNode", "()Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "originalNext", "getOriginalNext", "complete", "", "op", "Lkotlinx/coroutines/internal/AtomicOp;", "failure", "", "affected", "finishOnSuccess", "next", "finishPrepare", "prepareOp", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$PrepareOp;", "onPrepare", "onRemoved", "prepare", "retry", "", "takeAffectedNode", "Lkotlinx/coroutines/internal/OpDescriptor;", "updatedNext", "kotlinx-coroutines-core"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* loaded from: classes.dex */
    public static abstract class AbstractAtomicDesc extends AtomicDesc {
        protected abstract void finishOnSuccess(LockFreeLinkedListNode lockFreeLinkedListNode, LockFreeLinkedListNode lockFreeLinkedListNode2);

        public abstract void finishPrepare(PrepareOp prepareOp);

        protected abstract LockFreeLinkedListNode getAffectedNode();

        protected abstract LockFreeLinkedListNode getOriginalNext();

        public abstract Object updatedNext(LockFreeLinkedListNode lockFreeLinkedListNode, LockFreeLinkedListNode lockFreeLinkedListNode2);

        protected LockFreeLinkedListNode takeAffectedNode(OpDescriptor op) {
            LockFreeLinkedListNode affectedNode = getAffectedNode();
            Intrinsics.checkNotNull(affectedNode);
            return affectedNode;
        }

        protected Object failure(LockFreeLinkedListNode affected) {
            return null;
        }

        protected boolean retry(LockFreeLinkedListNode affected, Object next) {
            return false;
        }

        public Object onPrepare(PrepareOp prepareOp) {
            finishPrepare(prepareOp);
            return null;
        }

        public void onRemoved(LockFreeLinkedListNode affected) {
        }

        /* JADX WARN: Code restructure failed: missing block: B:31:0x005d, code lost:
            if (kotlinx.coroutines.DebugKt.getASSERTIONS_ENABLED() == false) goto L36;
         */
        /* JADX WARN: Code restructure failed: missing block: B:33:0x0060, code lost:
            if (r5 != null) goto L35;
         */
        /* JADX WARN: Code restructure failed: missing block: B:34:0x0062, code lost:
            r7 = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:35:0x0064, code lost:
            r7 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:36:0x0065, code lost:
            if (r7 == false) goto L31;
         */
        /* JADX WARN: Code restructure failed: missing block: B:39:0x006d, code lost:
            throw new java.lang.AssertionError();
         */
        /* JADX WARN: Code restructure failed: missing block: B:40:0x006e, code lost:
            return null;
         */
        @Override // kotlinx.coroutines.internal.AtomicDesc
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final java.lang.Object prepare(kotlinx.coroutines.internal.AtomicOp<?> r9) {
            /*
                r8 = this;
            L0:
                r0 = r9
                kotlinx.coroutines.internal.OpDescriptor r0 = (kotlinx.coroutines.internal.OpDescriptor) r0
                kotlinx.coroutines.internal.LockFreeLinkedListNode r0 = r8.takeAffectedNode(r0)
                if (r0 != 0) goto Ld
                java.lang.Object r0 = kotlinx.coroutines.internal.AtomicKt.RETRY_ATOMIC
                return r0
            Ld:
                java.lang.Object r1 = r0._next
                r2 = 0
                if (r1 != r9) goto L13
                return r2
            L13:
                boolean r3 = r9.isDecided()
                if (r3 == 0) goto L1a
                return r2
            L1a:
                boolean r3 = r1 instanceof kotlinx.coroutines.internal.OpDescriptor
                if (r3 == 0) goto L31
                r2 = r1
                kotlinx.coroutines.internal.OpDescriptor r2 = (kotlinx.coroutines.internal.OpDescriptor) r2
                boolean r2 = r9.isEarlierThan(r2)
                if (r2 == 0) goto L2a
                java.lang.Object r2 = kotlinx.coroutines.internal.AtomicKt.RETRY_ATOMIC
                return r2
            L2a:
                r2 = r1
                kotlinx.coroutines.internal.OpDescriptor r2 = (kotlinx.coroutines.internal.OpDescriptor) r2
                r2.perform(r0)
                goto L0
            L31:
                java.lang.Object r3 = r8.failure(r0)
                if (r3 == 0) goto L38
                return r3
            L38:
                boolean r4 = r8.retry(r0, r1)
                if (r4 == 0) goto L3f
                goto L0
            L3f:
                kotlinx.coroutines.internal.LockFreeLinkedListNode$PrepareOp r4 = new kotlinx.coroutines.internal.LockFreeLinkedListNode$PrepareOp
                r5 = r1
                kotlinx.coroutines.internal.LockFreeLinkedListNode r5 = (kotlinx.coroutines.internal.LockFreeLinkedListNode) r5
                r4.<init>(r0, r5, r8)
                java.util.concurrent.atomic.AtomicReferenceFieldUpdater r5 = kotlinx.coroutines.internal.LockFreeLinkedListNode._next$FU
                boolean r5 = kotlin.SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(r5, r0, r1, r4)
                if (r5 == 0) goto L76
            L50:
                java.lang.Object r5 = r4.perform(r0)     // Catch: java.lang.Throwable -> L6f
                java.lang.Object r6 = kotlinx.coroutines.internal.LockFreeLinkedList_commonKt.REMOVE_PREPARED     // Catch: java.lang.Throwable -> L6f
                if (r5 != r6) goto L59
                goto L0
            L59:
                boolean r6 = kotlinx.coroutines.DebugKt.getASSERTIONS_ENABLED()     // Catch: java.lang.Throwable -> L6f
                if (r6 == 0) goto L6e
                r6 = 0
                if (r5 != 0) goto L64
                r7 = 1
                goto L65
            L64:
                r7 = 0
            L65:
                if (r7 == 0) goto L68
                goto L6e
            L68:
                java.lang.AssertionError r2 = new java.lang.AssertionError     // Catch: java.lang.Throwable -> L6f
                r2.<init>()     // Catch: java.lang.Throwable -> L6f
                throw r2     // Catch: java.lang.Throwable -> L6f
            L6e:
                return r2
            L6f:
                r2 = move-exception
                java.util.concurrent.atomic.AtomicReferenceFieldUpdater r5 = kotlinx.coroutines.internal.LockFreeLinkedListNode._next$FU
                kotlin.SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(r5, r0, r4, r1)
                throw r2
            L76:
                goto L0
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc.prepare(kotlinx.coroutines.internal.AtomicOp):java.lang.Object");
        }

        @Override // kotlinx.coroutines.internal.AtomicDesc
        public final void complete(AtomicOp<?> atomicOp, Object failure) {
            boolean success = failure == null;
            LockFreeLinkedListNode affectedNode = getAffectedNode();
            if (affectedNode == null) {
                if (DebugKt.getASSERTIONS_ENABLED()) {
                    if (!(success ? false : true)) {
                        throw new AssertionError();
                    }
                    return;
                }
                return;
            }
            LockFreeLinkedListNode originalNext = getOriginalNext();
            if (originalNext == null) {
                if (DebugKt.getASSERTIONS_ENABLED()) {
                    if (!(success ? false : true)) {
                        throw new AssertionError();
                    }
                    return;
                }
                return;
            }
            Object update = success ? updatedNext(affectedNode, originalNext) : originalNext;
            if (SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(LockFreeLinkedListNode._next$FU, affectedNode, atomicOp, update) && success) {
                finishOnSuccess(affectedNode, originalNext);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void finishAdd(LockFreeLinkedListNode next) {
        LockFreeLinkedListNode nextPrev;
        do {
            nextPrev = (LockFreeLinkedListNode) next._prev;
            if (getNext() != next) {
                return;
            }
        } while (!SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(_prev$FU, next, nextPrev, this));
        if (isRemoved()) {
            next.correctPrev(null);
        }
    }

    protected LockFreeLinkedListNode nextIfRemoved() {
        Object next = getNext();
        Removed removed = next instanceof Removed ? (Removed) next : null;
        if (removed == null) {
            return null;
        }
        return removed.ref;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final LockFreeLinkedListNode correctPrev(OpDescriptor op) {
        while (true) {
            LockFreeLinkedListNode oldPrev = (LockFreeLinkedListNode) this._prev;
            LockFreeLinkedListNode prev = oldPrev;
            LockFreeLinkedListNode last = null;
            while (true) {
                Object prevNext = prev._next;
                if (prevNext == this) {
                    if (oldPrev == prev) {
                        return prev;
                    }
                    if (SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(_prev$FU, this, oldPrev, prev)) {
                        return prev;
                    }
                } else if (isRemoved()) {
                    return null;
                } else {
                    if (prevNext == op) {
                        return prev;
                    }
                    if (prevNext instanceof OpDescriptor) {
                        if (op != null && op.isEarlierThan((OpDescriptor) prevNext)) {
                            return null;
                        }
                        ((OpDescriptor) prevNext).perform(prev);
                    } else if (prevNext instanceof Removed) {
                        if (last != null) {
                            if (!SafePublicationLazyImpl$$ExternalSyntheticBackportWithForwarding0.m(_next$FU, last, prev, ((Removed) prevNext).ref)) {
                                break;
                            }
                            prev = last;
                            last = null;
                        } else {
                            prev = (LockFreeLinkedListNode) prev._prev;
                        }
                    } else {
                        last = prev;
                        prev = (LockFreeLinkedListNode) prevNext;
                    }
                }
            }
        }
    }

    public final void validateNode$kotlinx_coroutines_core(LockFreeLinkedListNode prev, LockFreeLinkedListNode next) {
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if ((prev == this._prev ? 1 : 0) == 0) {
                throw new AssertionError();
            }
        }
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(next == this._next)) {
                throw new AssertionError();
            }
        }
    }

    public String toString() {
        return new StringBuilder().append((Object) getClass().getSimpleName()).append('@').append((Object) Integer.toHexString(System.identityHashCode(this))).toString();
    }
}
