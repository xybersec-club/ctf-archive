package kotlinx.coroutines.flow;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: Add missing generic type declarations: [T] */
/* compiled from: Errors.kt */
@Metadata(d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u008a@"}, d2 = {"<anonymous>", "", "T", "Lkotlinx/coroutines/flow/FlowCollector;", "e", ""}, k = 3, mv = {1, 5, 1}, xi = 48)
@DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__ErrorsKt$onErrorCollect$2", f = "Errors.kt", i = {}, l = {89}, m = "invokeSuspend", n = {}, s = {})
/* loaded from: classes.dex */
public final class FlowKt__ErrorsKt$onErrorCollect$2<T> extends SuspendLambda implements Function3<FlowCollector<? super T>, Throwable, Continuation<? super Unit>, Object> {
    final /* synthetic */ Flow<T> $fallback;
    final /* synthetic */ Function1<Throwable, Boolean> $predicate;
    private /* synthetic */ Object L$0;
    /* synthetic */ Object L$1;
    int label;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public FlowKt__ErrorsKt$onErrorCollect$2(Function1<? super Throwable, Boolean> function1, Flow<? extends T> flow, Continuation<? super FlowKt__ErrorsKt$onErrorCollect$2> continuation) {
        super(3, continuation);
        this.$predicate = function1;
        this.$fallback = flow;
    }

    @Override // kotlin.jvm.functions.Function3
    public /* bridge */ /* synthetic */ Object invoke(Object obj, Throwable th, Continuation<? super Unit> continuation) {
        return invoke((FlowCollector) ((FlowCollector) obj), th, continuation);
    }

    public final Object invoke(FlowCollector<? super T> flowCollector, Throwable th, Continuation<? super Unit> continuation) {
        FlowKt__ErrorsKt$onErrorCollect$2 flowKt__ErrorsKt$onErrorCollect$2 = new FlowKt__ErrorsKt$onErrorCollect$2(this.$predicate, this.$fallback, continuation);
        flowKt__ErrorsKt$onErrorCollect$2.L$0 = flowCollector;
        flowKt__ErrorsKt$onErrorCollect$2.L$1 = th;
        return flowKt__ErrorsKt$onErrorCollect$2.invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object $result) {
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                FlowCollector $this$catch = (FlowCollector) this.L$0;
                Throwable e = (Throwable) this.L$1;
                if (!this.$predicate.invoke(e).booleanValue()) {
                    throw e;
                }
                this.L$0 = null;
                this.label = 1;
                if (FlowKt.emitAll($this$catch, this.$fallback, this) != coroutine_suspended) {
                    break;
                } else {
                    return coroutine_suspended;
                }
            case 1:
                ResultKt.throwOnFailure($result);
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        return Unit.INSTANCE;
    }
}
