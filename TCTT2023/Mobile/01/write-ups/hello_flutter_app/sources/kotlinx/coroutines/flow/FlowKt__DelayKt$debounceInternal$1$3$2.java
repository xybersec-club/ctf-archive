package kotlinx.coroutines.flow;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Ref;
import kotlinx.coroutines.channels.ChannelResult;
import kotlinx.coroutines.flow.internal.NullSurrogateKt;
import kotlinx.coroutines.internal.Symbol;
/* compiled from: Delay.kt */
@Metadata(d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u008a@"}, d2 = {"<anonymous>", "", "T", "value", "Lkotlinx/coroutines/channels/ChannelResult;", ""}, k = 3, mv = {1, 5, 1}, xi = 48)
@DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__DelayKt$debounceInternal$1$3$2", f = "Delay.kt", i = {0}, l = {245}, m = "invokeSuspend", n = {"$this$onFailure$iv"}, s = {"L$0"})
/* loaded from: classes.dex */
final class FlowKt__DelayKt$debounceInternal$1$3$2 extends SuspendLambda implements Function2<ChannelResult<? extends Object>, Continuation<? super Unit>, Object> {
    final /* synthetic */ FlowCollector<T> $downstream;
    final /* synthetic */ Ref.ObjectRef<Object> $lastValue;
    /* synthetic */ Object L$0;
    Object L$1;
    int label;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public FlowKt__DelayKt$debounceInternal$1$3$2(Ref.ObjectRef<Object> objectRef, FlowCollector<? super T> flowCollector, Continuation<? super FlowKt__DelayKt$debounceInternal$1$3$2> continuation) {
        super(2, continuation);
        this.$lastValue = objectRef;
        this.$downstream = flowCollector;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        FlowKt__DelayKt$debounceInternal$1$3$2 flowKt__DelayKt$debounceInternal$1$3$2 = new FlowKt__DelayKt$debounceInternal$1$3$2(this.$lastValue, this.$downstream, continuation);
        flowKt__DelayKt$debounceInternal$1$3$2.L$0 = obj;
        return flowKt__DelayKt$debounceInternal$1$3$2;
    }

    @Override // kotlin.jvm.functions.Function2
    public /* bridge */ /* synthetic */ Object invoke(ChannelResult<? extends Object> channelResult, Continuation<? super Unit> continuation) {
        return m1557invokeWpGqRn0(channelResult.m1546unboximpl(), continuation);
    }

    /* renamed from: invoke-WpGqRn0  reason: not valid java name */
    public final Object m1557invokeWpGqRn0(Object obj, Continuation<? super Unit> continuation) {
        return ((FlowKt__DelayKt$debounceInternal$1$3$2) create(ChannelResult.m1534boximpl(obj), continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Type inference failed for: r0v4, types: [kotlinx.coroutines.internal.Symbol, T] */
    /* JADX WARN: Type inference failed for: r2v2, types: [T, java.lang.Object] */
    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object $result) {
        Ref.ObjectRef<Object> objectRef;
        FlowKt__DelayKt$debounceInternal$1$3$2 flowKt__DelayKt$debounceInternal$1$3$2;
        boolean z;
        boolean z2;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                ?? m1546unboximpl = ((ChannelResult) this.L$0).m1546unboximpl();
                Ref.ObjectRef<Object> objectRef2 = this.$lastValue;
                if (!(m1546unboximpl instanceof ChannelResult.Failed)) {
                    objectRef2.element = m1546unboximpl;
                }
                objectRef = this.$lastValue;
                FlowCollector<T> flowCollector = this.$downstream;
                if (m1546unboximpl instanceof ChannelResult.Failed) {
                    Throwable it = ChannelResult.m1538exceptionOrNullimpl(m1546unboximpl);
                    if (it != null) {
                        throw it;
                    }
                    if (objectRef.element != null) {
                        Symbol this_$iv = NullSurrogateKt.NULL;
                        Object value$iv = objectRef.element;
                        if (value$iv == this_$iv) {
                            value$iv = null;
                        }
                        this.L$0 = m1546unboximpl;
                        this.L$1 = objectRef;
                        this.label = 1;
                        if (flowCollector.emit(value$iv, this) == coroutine_suspended) {
                            return coroutine_suspended;
                        }
                        flowKt__DelayKt$debounceInternal$1$3$2 = this;
                        z = false;
                        z2 = false;
                    }
                    objectRef.element = NullSurrogateKt.DONE;
                    break;
                }
                break;
            case 1:
                flowKt__DelayKt$debounceInternal$1$3$2 = this;
                z = false;
                z2 = false;
                objectRef = (Ref.ObjectRef) flowKt__DelayKt$debounceInternal$1$3$2.L$1;
                Object obj = flowKt__DelayKt$debounceInternal$1$3$2.L$0;
                ResultKt.throwOnFailure($result);
                objectRef.element = NullSurrogateKt.DONE;
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        return Unit.INSTANCE;
    }
}
