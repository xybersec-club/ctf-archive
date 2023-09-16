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
import kotlinx.coroutines.CompletableDeferred;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.JobKt;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: Share.kt */
@Metadata(d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u00020\u0003H\u008a@"}, d2 = {"<anonymous>", "", "T", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 5, 1}, xi = 48)
@DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__ShareKt$launchSharingDeferred$1", f = "Share.kt", i = {}, l = {418}, m = "invokeSuspend", n = {}, s = {})
/* loaded from: classes.dex */
public final class FlowKt__ShareKt$launchSharingDeferred$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    final /* synthetic */ CompletableDeferred<StateFlow<T>> $result;
    final /* synthetic */ Flow<T> $upstream;
    private /* synthetic */ Object L$0;
    int label;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public FlowKt__ShareKt$launchSharingDeferred$1(Flow<? extends T> flow, CompletableDeferred<StateFlow<T>> completableDeferred, Continuation<? super FlowKt__ShareKt$launchSharingDeferred$1> continuation) {
        super(2, continuation);
        this.$upstream = flow;
        this.$result = completableDeferred;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        FlowKt__ShareKt$launchSharingDeferred$1 flowKt__ShareKt$launchSharingDeferred$1 = new FlowKt__ShareKt$launchSharingDeferred$1(this.$upstream, this.$result, continuation);
        flowKt__ShareKt$launchSharingDeferred$1.L$0 = obj;
        return flowKt__ShareKt$launchSharingDeferred$1;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
        return ((FlowKt__ShareKt$launchSharingDeferred$1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object $result) {
        Throwable e;
        FlowKt__ShareKt$launchSharingDeferred$1 flowKt__ShareKt$launchSharingDeferred$1;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                final CoroutineScope $this$launch = (CoroutineScope) this.L$0;
                try {
                    final Ref.ObjectRef state = new Ref.ObjectRef();
                    Flow $this$collect$iv = this.$upstream;
                    final CompletableDeferred<StateFlow<T>> completableDeferred = this.$result;
                    this.label = 1;
                    return $this$collect$iv.collect(new FlowCollector<T>() { // from class: kotlinx.coroutines.flow.FlowKt__ShareKt$launchSharingDeferred$1$invokeSuspend$$inlined$collect$1
                        @Override // kotlinx.coroutines.flow.FlowCollector
                        public Object emit(T t, Continuation<? super Unit> continuation) {
                            Unit unit;
                            MutableStateFlow it = (MutableStateFlow) Ref.ObjectRef.this.element;
                            if (it == null) {
                                unit = null;
                            } else {
                                it.setValue(t);
                                unit = Unit.INSTANCE;
                            }
                            if (unit == null) {
                                CoroutineScope $this$invokeSuspend_u24lambda_u2d3_u24lambda_u2d2 = $this$launch;
                                Ref.ObjectRef objectRef = Ref.ObjectRef.this;
                                T t2 = (T) StateFlowKt.MutableStateFlow(t);
                                completableDeferred.complete(new ReadonlyStateFlow((StateFlow) t2, JobKt.getJob($this$invokeSuspend_u24lambda_u2d3_u24lambda_u2d2.getCoroutineContext())));
                                Unit unit2 = Unit.INSTANCE;
                                objectRef.element = t2;
                            }
                            Object value = Unit.INSTANCE;
                            return value;
                        }
                    }, this) == coroutine_suspended ? coroutine_suspended : Unit.INSTANCE;
                } catch (Throwable th) {
                    e = th;
                    flowKt__ShareKt$launchSharingDeferred$1 = this;
                    flowKt__ShareKt$launchSharingDeferred$1.$result.completeExceptionally(e);
                    throw e;
                }
            case 1:
                flowKt__ShareKt$launchSharingDeferred$1 = this;
                try {
                    ResultKt.throwOnFailure($result);
                } catch (Throwable th2) {
                    e = th2;
                    flowKt__ShareKt$launchSharingDeferred$1.$result.completeExceptionally(e);
                    throw e;
                }
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }
}
