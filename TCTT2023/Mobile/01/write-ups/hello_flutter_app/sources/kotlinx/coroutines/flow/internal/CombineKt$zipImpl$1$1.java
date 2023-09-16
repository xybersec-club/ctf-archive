package kotlinx.coroutines.flow.internal;

import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlinx.coroutines.CompletableJob;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.JobKt__JobKt;
import kotlinx.coroutines.channels.ProduceKt;
import kotlinx.coroutines.channels.ReceiveChannel;
import kotlinx.coroutines.channels.SendChannel;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.internal.ThreadContextKt;
/* compiled from: Combine.kt */
@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003\"\u0004\b\u0002\u0010\u0004*\u00020\u0005H\u008a@"}, d2 = {"<anonymous>", "", "T1", "T2", "R", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 5, 1}, xi = 48)
@DebugMetadata(c = "kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1", f = "Combine.kt", i = {0}, l = {129}, m = "invokeSuspend", n = {"second"}, s = {"L$0"})
/* loaded from: classes.dex */
final class CombineKt$zipImpl$1$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    final /* synthetic */ Flow<T1> $flow;
    final /* synthetic */ Flow<T2> $flow2;
    final /* synthetic */ FlowCollector<R> $this_unsafeFlow;
    final /* synthetic */ Function3<T1, T2, Continuation<? super R>, Object> $transform;
    private /* synthetic */ Object L$0;
    int label;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public CombineKt$zipImpl$1$1(FlowCollector<? super R> flowCollector, Flow<? extends T2> flow, Flow<? extends T1> flow2, Function3<? super T1, ? super T2, ? super Continuation<? super R>, ? extends Object> function3, Continuation<? super CombineKt$zipImpl$1$1> continuation) {
        super(2, continuation);
        this.$this_unsafeFlow = flowCollector;
        this.$flow2 = flow;
        this.$flow = flow2;
        this.$transform = function3;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        CombineKt$zipImpl$1$1 combineKt$zipImpl$1$1 = new CombineKt$zipImpl$1$1(this.$this_unsafeFlow, this.$flow2, this.$flow, this.$transform, continuation);
        combineKt$zipImpl$1$1.L$0 = obj;
        return combineKt$zipImpl$1$1;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
        return ((CombineKt$zipImpl$1$1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object $result) {
        CombineKt$zipImpl$1$1 combineKt$zipImpl$1$1;
        final CompletableJob collectJob;
        ReceiveChannel second;
        CoroutineContext scopeContext;
        Object cnt;
        CoroutineContext plus;
        Unit unit;
        Flow<T1> flow;
        FlowCollector<R> flowCollector;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        CancellationException cancellationException = null;
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                combineKt$zipImpl$1$1 = this;
                CoroutineScope $this$coroutineScope = (CoroutineScope) combineKt$zipImpl$1$1.L$0;
                ReceiveChannel second2 = ProduceKt.produce$default($this$coroutineScope, null, 0, new CombineKt$zipImpl$1$1$second$1(combineKt$zipImpl$1$1.$flow2, null), 3, null);
                collectJob = JobKt__JobKt.Job$default((Job) null, 1, (Object) null);
                final FlowCollector<R> flowCollector2 = combineKt$zipImpl$1$1.$this_unsafeFlow;
                ((SendChannel) second2).invokeOnClose(new Function1<Throwable, Unit>() { // from class: kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1.1
                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    /* JADX WARN: Multi-variable type inference failed */
                    {
                        super(1);
                    }

                    @Override // kotlin.jvm.functions.Function1
                    public /* bridge */ /* synthetic */ Unit invoke(Throwable th) {
                        invoke2(th);
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke  reason: avoid collision after fix types in other method */
                    public final void invoke2(Throwable it) {
                        if (CompletableJob.this.isActive()) {
                            CompletableJob.this.cancel((CancellationException) new AbortFlowException(flowCollector2));
                        }
                    }
                });
                try {
                    try {
                        scopeContext = $this$coroutineScope.getCoroutineContext();
                        cnt = ThreadContextKt.threadContextElements(scopeContext);
                        plus = $this$coroutineScope.getCoroutineContext().plus(collectJob);
                        unit = Unit.INSTANCE;
                        flow = combineKt$zipImpl$1$1.$flow;
                        flowCollector = combineKt$zipImpl$1$1.$this_unsafeFlow;
                    } catch (AbortFlowException e) {
                        e = e;
                        second = second2;
                        try {
                            FlowExceptions_commonKt.checkOwnership(e, combineKt$zipImpl$1$1.$this_unsafeFlow);
                            ReceiveChannel.DefaultImpls.cancel$default(second, (CancellationException) null, 1, (Object) null);
                            return Unit.INSTANCE;
                        } catch (Throwable th) {
                            th = th;
                            cancellationException = null;
                            ReceiveChannel.DefaultImpls.cancel$default(second, cancellationException, 1, (Object) cancellationException);
                            throw th;
                        }
                    }
                    try {
                        combineKt$zipImpl$1$1.L$0 = second2;
                        combineKt$zipImpl$1$1.label = 1;
                        if (ChannelFlowKt.withContextUndispatched$default(plus, unit, null, new AnonymousClass2(flow, scopeContext, cnt, second2, flowCollector, combineKt$zipImpl$1$1.$transform, null), combineKt$zipImpl$1$1, 4, null) == coroutine_suspended) {
                            return coroutine_suspended;
                        }
                        second = second2;
                        ReceiveChannel.DefaultImpls.cancel$default(second, (CancellationException) null, 1, (Object) null);
                        return Unit.INSTANCE;
                    } catch (Throwable th2) {
                        th = th2;
                        second = second2;
                        cancellationException = null;
                        ReceiveChannel.DefaultImpls.cancel$default(second, cancellationException, 1, (Object) cancellationException);
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    second = second2;
                }
            case 1:
                combineKt$zipImpl$1$1 = this;
                second = (ReceiveChannel) combineKt$zipImpl$1$1.L$0;
                try {
                    ResultKt.throwOnFailure($result);
                } catch (AbortFlowException e2) {
                    e = e2;
                    FlowExceptions_commonKt.checkOwnership(e, combineKt$zipImpl$1$1.$this_unsafeFlow);
                    ReceiveChannel.DefaultImpls.cancel$default(second, (CancellationException) null, 1, (Object) null);
                    return Unit.INSTANCE;
                } catch (Throwable th4) {
                    th = th4;
                    ReceiveChannel.DefaultImpls.cancel$default(second, cancellationException, 1, (Object) cancellationException);
                    throw th;
                }
                ReceiveChannel.DefaultImpls.cancel$default(second, (CancellationException) null, 1, (Object) null);
                return Unit.INSTANCE;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: Combine.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003\"\u0004\b\u0002\u0010\u00042\u0006\u0010\u0005\u001a\u00020\u0001H\u008a@"}, d2 = {"<anonymous>", "", "T1", "T2", "R", "it"}, k = 3, mv = {1, 5, 1}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2", f = "Combine.kt", i = {}, l = {147}, m = "invokeSuspend", n = {}, s = {})
    /* renamed from: kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2  reason: invalid class name */
    /* loaded from: classes.dex */
    public static final class AnonymousClass2 extends SuspendLambda implements Function2<Unit, Continuation<? super Unit>, Object> {
        final /* synthetic */ Object $cnt;
        final /* synthetic */ Flow<T1> $flow;
        final /* synthetic */ CoroutineContext $scopeContext;
        final /* synthetic */ ReceiveChannel<Object> $second;
        final /* synthetic */ FlowCollector<R> $this_unsafeFlow;
        final /* synthetic */ Function3<T1, T2, Continuation<? super R>, Object> $transform;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        AnonymousClass2(Flow<? extends T1> flow, CoroutineContext coroutineContext, Object obj, ReceiveChannel<? extends Object> receiveChannel, FlowCollector<? super R> flowCollector, Function3<? super T1, ? super T2, ? super Continuation<? super R>, ? extends Object> function3, Continuation<? super AnonymousClass2> continuation) {
            super(2, continuation);
            this.$flow = flow;
            this.$scopeContext = coroutineContext;
            this.$cnt = obj;
            this.$second = receiveChannel;
            this.$this_unsafeFlow = flowCollector;
            this.$transform = function3;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return new AnonymousClass2(this.$flow, this.$scopeContext, this.$cnt, this.$second, this.$this_unsafeFlow, this.$transform, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(Unit unit, Continuation<? super Unit> continuation) {
            return ((AnonymousClass2) create(unit, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object $result) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case 0:
                    ResultKt.throwOnFailure($result);
                    Flow $this$collect$iv = this.$flow;
                    final CoroutineContext coroutineContext = this.$scopeContext;
                    final Object obj = this.$cnt;
                    final ReceiveChannel<Object> receiveChannel = this.$second;
                    final FlowCollector<R> flowCollector = this.$this_unsafeFlow;
                    final Function3<T1, T2, Continuation<? super R>, Object> function3 = this.$transform;
                    this.label = 1;
                    if ($this$collect$iv.collect(new FlowCollector<T1>() { // from class: kotlinx.coroutines.flow.internal.CombineKt$zipImpl$1$1$2$invokeSuspend$$inlined$collect$1
                        @Override // kotlinx.coroutines.flow.FlowCollector
                        public Object emit(T1 t1, Continuation<? super Unit> continuation) {
                            Object withContextUndispatched = ChannelFlowKt.withContextUndispatched(CoroutineContext.this, Unit.INSTANCE, obj, new CombineKt$zipImpl$1$1$2$1$1(receiveChannel, flowCollector, function3, t1, null), continuation);
                            return withContextUndispatched == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? withContextUndispatched : Unit.INSTANCE;
                        }
                    }, this) != coroutine_suspended) {
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
}
