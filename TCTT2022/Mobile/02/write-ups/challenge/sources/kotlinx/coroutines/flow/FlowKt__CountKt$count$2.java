package kotlinx.coroutines.flow;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.SuspendFunction;
import kotlin.jvm.internal.Ref;
/* compiled from: Count.kt */
@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\u0006\u0010\u0003\u001a\u0002H\u0002H\u008a@¢\u0006\u0004\b\u0004\u0010\u0005"}, d2 = {"<anonymous>", "", "T", "it", "emit", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 6, 0}, xi = 48)
/* loaded from: classes.dex */
final class FlowKt__CountKt$count$2<T> implements FlowCollector, SuspendFunction {
    final /* synthetic */ Ref.IntRef $i;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FlowKt__CountKt$count$2(Ref.IntRef intRef) {
        this.$i = intRef;
    }

    @Override // kotlinx.coroutines.flow.FlowCollector
    public final Object emit(T t, Continuation<? super Unit> continuation) {
        this.$i.element++;
        int i = this.$i.element;
        return Unit.INSTANCE;
    }
}
