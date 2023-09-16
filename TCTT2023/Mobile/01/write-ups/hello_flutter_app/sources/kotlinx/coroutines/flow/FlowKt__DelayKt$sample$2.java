package kotlinx.coroutines.flow;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Ref;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.channels.ProduceKt;
import kotlinx.coroutines.channels.ReceiveChannel;
import kotlinx.coroutines.flow.internal.NullSurrogateKt;
import kotlinx.coroutines.selects.SelectBuilderImpl;
/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: Add missing generic type declarations: [T] */
/* compiled from: Delay.kt */
@Metadata(d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0005H\u008a@"}, d2 = {"<anonymous>", "", "T", "Lkotlinx/coroutines/CoroutineScope;", "downstream", "Lkotlinx/coroutines/flow/FlowCollector;"}, k = 3, mv = {1, 5, 1}, xi = 48)
@DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__DelayKt$sample$2", f = "Delay.kt", i = {0, 0, 0, 0}, l = {355}, m = "invokeSuspend", n = {"downstream", "values", "lastValue", "ticker"}, s = {"L$0", "L$1", "L$2", "L$3"})
/* loaded from: classes.dex */
public final class FlowKt__DelayKt$sample$2<T> extends SuspendLambda implements Function3<CoroutineScope, FlowCollector<? super T>, Continuation<? super Unit>, Object> {
    final /* synthetic */ long $periodMillis;
    final /* synthetic */ Flow<T> $this_sample;
    private /* synthetic */ Object L$0;
    /* synthetic */ Object L$1;
    Object L$2;
    Object L$3;
    int label;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public FlowKt__DelayKt$sample$2(long j, Flow<? extends T> flow, Continuation<? super FlowKt__DelayKt$sample$2> continuation) {
        super(3, continuation);
        this.$periodMillis = j;
        this.$this_sample = flow;
    }

    @Override // kotlin.jvm.functions.Function3
    public /* bridge */ /* synthetic */ Object invoke(CoroutineScope coroutineScope, Object obj, Continuation<? super Unit> continuation) {
        return invoke(coroutineScope, (FlowCollector) ((FlowCollector) obj), continuation);
    }

    public final Object invoke(CoroutineScope coroutineScope, FlowCollector<? super T> flowCollector, Continuation<? super Unit> continuation) {
        FlowKt__DelayKt$sample$2 flowKt__DelayKt$sample$2 = new FlowKt__DelayKt$sample$2(this.$periodMillis, this.$this_sample, continuation);
        flowKt__DelayKt$sample$2.L$0 = coroutineScope;
        flowKt__DelayKt$sample$2.L$1 = flowCollector;
        return flowKt__DelayKt$sample$2.invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object $result) {
        FlowKt__DelayKt$sample$2 flowKt__DelayKt$sample$2;
        ReceiveChannel ticker;
        FlowCollector downstream;
        ReceiveChannel values;
        Ref.ObjectRef lastValue;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                flowKt__DelayKt$sample$2 = this;
                CoroutineScope $this$scopedFlow = (CoroutineScope) flowKt__DelayKt$sample$2.L$0;
                FlowCollector downstream2 = (FlowCollector) flowKt__DelayKt$sample$2.L$1;
                ReceiveChannel values2 = ProduceKt.produce$default($this$scopedFlow, null, -1, new FlowKt__DelayKt$sample$2$values$1(flowKt__DelayKt$sample$2.$this_sample, null), 1, null);
                Ref.ObjectRef lastValue2 = new Ref.ObjectRef();
                ticker = FlowKt__DelayKt.fixedPeriodTicker$default($this$scopedFlow, flowKt__DelayKt$sample$2.$periodMillis, 0L, 2, null);
                downstream = downstream2;
                values = values2;
                lastValue = lastValue2;
                break;
            case 1:
                flowKt__DelayKt$sample$2 = this;
                ticker = (ReceiveChannel) flowKt__DelayKt$sample$2.L$3;
                lastValue = (Ref.ObjectRef) flowKt__DelayKt$sample$2.L$2;
                values = (ReceiveChannel) flowKt__DelayKt$sample$2.L$1;
                downstream = (FlowCollector) flowKt__DelayKt$sample$2.L$0;
                ResultKt.throwOnFailure($result);
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        while (lastValue.element != NullSurrogateKt.DONE) {
            flowKt__DelayKt$sample$2.L$0 = downstream;
            flowKt__DelayKt$sample$2.L$1 = values;
            flowKt__DelayKt$sample$2.L$2 = lastValue;
            flowKt__DelayKt$sample$2.L$3 = ticker;
            flowKt__DelayKt$sample$2.label = 1;
            FlowKt__DelayKt$sample$2 uCont$iv = flowKt__DelayKt$sample$2;
            SelectBuilderImpl scope$iv = new SelectBuilderImpl(uCont$iv);
            try {
                SelectBuilderImpl $this$invokeSuspend_u24lambda_u2d0 = scope$iv;
                $this$invokeSuspend_u24lambda_u2d0.invoke(values.getOnReceiveCatching(), new FlowKt__DelayKt$sample$2$1$1(lastValue, ticker, null));
                $this$invokeSuspend_u24lambda_u2d0.invoke(ticker.getOnReceive(), new FlowKt__DelayKt$sample$2$1$2(lastValue, downstream, null));
            } catch (Throwable e$iv) {
                scope$iv.handleBuilderException(e$iv);
            }
            Object result = scope$iv.getResult();
            if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                DebugProbesKt.probeCoroutineSuspended(flowKt__DelayKt$sample$2);
                continue;
            }
            if (result == coroutine_suspended) {
                return coroutine_suspended;
            }
        }
        return Unit.INSTANCE;
    }
}
