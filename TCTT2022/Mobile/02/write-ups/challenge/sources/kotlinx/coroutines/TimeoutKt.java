package kotlinx.coroutines;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.intrinsics.UndispatchedKt;
/* compiled from: Timeout.kt */
@Metadata(d1 = {"\u0000>\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0000\u001a_\u0010\u0006\u001a\u0004\u0018\u00010\u0007\"\u0004\b\u0000\u0010\b\"\b\b\u0001\u0010\t*\u0002H\b2\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u0002H\b\u0012\u0004\u0012\u0002H\t0\n2'\u0010\u000b\u001a#\b\u0001\u0012\u0004\u0012\u00020\r\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\t0\u000e\u0012\u0006\u0012\u0004\u0018\u00010\u00070\f¢\u0006\u0002\b\u000fH\u0002ø\u0001\u0000¢\u0006\u0002\u0010\u0010\u001aU\u0010\u0011\u001a\u0002H\t\"\u0004\b\u0000\u0010\t2\u0006\u0010\u0012\u001a\u00020\u00032'\u0010\u000b\u001a#\b\u0001\u0012\u0004\u0012\u00020\r\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\t0\u000e\u0012\u0006\u0012\u0004\u0018\u00010\u00070\f¢\u0006\u0002\b\u000fH\u0086@ø\u0001\u0000\u0082\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0002 \u0001¢\u0006\u0002\u0010\u0013\u001a]\u0010\u0011\u001a\u0002H\t\"\u0004\b\u0000\u0010\t2\u0006\u0010\u0014\u001a\u00020\u00152'\u0010\u000b\u001a#\b\u0001\u0012\u0004\u0012\u00020\r\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\t0\u000e\u0012\u0006\u0012\u0004\u0018\u00010\u00070\f¢\u0006\u0002\b\u000fH\u0086@ø\u0001\u0000ø\u0001\u0000ø\u0001\u0001\u0082\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0002 \u0001¢\u0006\u0004\b\u0016\u0010\u0013\u001aJ\u0010\u0017\u001a\u0004\u0018\u0001H\t\"\u0004\b\u0000\u0010\t2\u0006\u0010\u0012\u001a\u00020\u00032'\u0010\u000b\u001a#\b\u0001\u0012\u0004\u0012\u00020\r\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\t0\u000e\u0012\u0006\u0012\u0004\u0018\u00010\u00070\f¢\u0006\u0002\b\u000fH\u0086@ø\u0001\u0000¢\u0006\u0002\u0010\u0013\u001aR\u0010\u0017\u001a\u0004\u0018\u0001H\t\"\u0004\b\u0000\u0010\t2\u0006\u0010\u0014\u001a\u00020\u00152'\u0010\u000b\u001a#\b\u0001\u0012\u0004\u0012\u00020\r\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\t0\u000e\u0012\u0006\u0012\u0004\u0018\u00010\u00070\f¢\u0006\u0002\b\u000fH\u0086@ø\u0001\u0000ø\u0001\u0000ø\u0001\u0001¢\u0006\u0004\b\u0018\u0010\u0013\u0082\u0002\u000b\n\u0002\b\u0019\n\u0005\b¡\u001e0\u0001¨\u0006\u0019"}, d2 = {"TimeoutCancellationException", "Lkotlinx/coroutines/TimeoutCancellationException;", "time", "", "coroutine", "Lkotlinx/coroutines/Job;", "setupTimeout", "", "U", "T", "Lkotlinx/coroutines/TimeoutCoroutine;", "block", "Lkotlin/Function2;", "Lkotlinx/coroutines/CoroutineScope;", "Lkotlin/coroutines/Continuation;", "Lkotlin/ExtensionFunctionType;", "(Lkotlinx/coroutines/TimeoutCoroutine;Lkotlin/jvm/functions/Function2;)Ljava/lang/Object;", "withTimeout", "timeMillis", "(JLkotlin/jvm/functions/Function2;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "timeout", "Lkotlin/time/Duration;", "withTimeout-KLykuaI", "withTimeoutOrNull", "withTimeoutOrNull-KLykuaI", "kotlinx-coroutines-core"}, k = 2, mv = {1, 6, 0}, xi = 48)
/* loaded from: classes.dex */
public final class TimeoutKt {
    public static final <T> Object withTimeout(long j, Function2<? super CoroutineScope, ? super Continuation<? super T>, ? extends Object> function2, Continuation<? super T> continuation) {
        if (j <= 0) {
            throw new TimeoutCancellationException("Timed out immediately");
        }
        Object obj = setupTimeout(new TimeoutCoroutine(j, continuation), function2);
        if (obj == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return obj;
    }

    /* renamed from: withTimeout-KLykuaI  reason: not valid java name */
    public static final <T> Object m1585withTimeoutKLykuaI(long j, Function2<? super CoroutineScope, ? super Continuation<? super T>, ? extends Object> function2, Continuation<? super T> continuation) {
        return withTimeout(DelayKt.m1576toDelayMillisLRDsOJo(j), function2, continuation);
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0025  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x003f  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x007c A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x007d  */
    /* JADX WARN: Type inference failed for: r4v1, types: [kotlinx.coroutines.TimeoutCoroutine, T] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final <T> java.lang.Object withTimeoutOrNull(long r7, kotlin.jvm.functions.Function2<? super kotlinx.coroutines.CoroutineScope, ? super kotlin.coroutines.Continuation<? super T>, ? extends java.lang.Object> r9, kotlin.coroutines.Continuation<? super T> r10) {
        /*
            boolean r0 = r10 instanceof kotlinx.coroutines.TimeoutKt$withTimeoutOrNull$1
            if (r0 == 0) goto L14
            r0 = r10
            kotlinx.coroutines.TimeoutKt$withTimeoutOrNull$1 r0 = (kotlinx.coroutines.TimeoutKt$withTimeoutOrNull$1) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L14
            int r10 = r0.label
            int r10 = r10 - r2
            r0.label = r10
            goto L19
        L14:
            kotlinx.coroutines.TimeoutKt$withTimeoutOrNull$1 r0 = new kotlinx.coroutines.TimeoutKt$withTimeoutOrNull$1
            r0.<init>(r10)
        L19:
            java.lang.Object r10 = r0.result
            java.lang.Object r1 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r2 = r0.label
            r3 = 0
            r4 = 1
            if (r2 == 0) goto L3f
            if (r2 != r4) goto L37
            long r7 = r0.J$0
            java.lang.Object r7 = r0.L$1
            kotlin.jvm.internal.Ref$ObjectRef r7 = (kotlin.jvm.internal.Ref.ObjectRef) r7
            java.lang.Object r8 = r0.L$0
            kotlin.jvm.functions.Function2 r8 = (kotlin.jvm.functions.Function2) r8
            kotlin.ResultKt.throwOnFailure(r10)     // Catch: kotlinx.coroutines.TimeoutCancellationException -> L35
            goto L73
        L35:
            r8 = move-exception
            goto L76
        L37:
            java.lang.IllegalStateException r7 = new java.lang.IllegalStateException
            java.lang.String r8 = "call to 'resume' before 'invoke' with coroutine"
            r7.<init>(r8)
            throw r7
        L3f:
            kotlin.ResultKt.throwOnFailure(r10)
            r5 = 0
            int r10 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r10 > 0) goto L49
            return r3
        L49:
            kotlin.jvm.internal.Ref$ObjectRef r10 = new kotlin.jvm.internal.Ref$ObjectRef
            r10.<init>()
            r0.L$0 = r9     // Catch: kotlinx.coroutines.TimeoutCancellationException -> L74
            r0.L$1 = r10     // Catch: kotlinx.coroutines.TimeoutCancellationException -> L74
            r0.J$0 = r7     // Catch: kotlinx.coroutines.TimeoutCancellationException -> L74
            r0.label = r4     // Catch: kotlinx.coroutines.TimeoutCancellationException -> L74
            r2 = r0
            kotlin.coroutines.Continuation r2 = (kotlin.coroutines.Continuation) r2     // Catch: kotlinx.coroutines.TimeoutCancellationException -> L74
            kotlinx.coroutines.TimeoutCoroutine r4 = new kotlinx.coroutines.TimeoutCoroutine     // Catch: kotlinx.coroutines.TimeoutCancellationException -> L74
            r4.<init>(r7, r2)     // Catch: kotlinx.coroutines.TimeoutCancellationException -> L74
            r10.element = r4     // Catch: kotlinx.coroutines.TimeoutCancellationException -> L74
            java.lang.Object r7 = setupTimeout(r4, r9)     // Catch: kotlinx.coroutines.TimeoutCancellationException -> L74
            java.lang.Object r8 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()     // Catch: kotlinx.coroutines.TimeoutCancellationException -> L74
            if (r7 != r8) goto L6f
            kotlin.coroutines.Continuation r0 = (kotlin.coroutines.Continuation) r0     // Catch: kotlinx.coroutines.TimeoutCancellationException -> L74
            kotlin.coroutines.jvm.internal.DebugProbesKt.probeCoroutineSuspended(r0)     // Catch: kotlinx.coroutines.TimeoutCancellationException -> L74
        L6f:
            if (r7 != r1) goto L72
            return r1
        L72:
            r10 = r7
        L73:
            return r10
        L74:
            r8 = move-exception
            r7 = r10
        L76:
            kotlinx.coroutines.Job r9 = r8.coroutine
            T r7 = r7.element
            if (r9 != r7) goto L7d
            return r3
        L7d:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.TimeoutKt.withTimeoutOrNull(long, kotlin.jvm.functions.Function2, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* renamed from: withTimeoutOrNull-KLykuaI  reason: not valid java name */
    public static final <T> Object m1586withTimeoutOrNullKLykuaI(long j, Function2<? super CoroutineScope, ? super Continuation<? super T>, ? extends Object> function2, Continuation<? super T> continuation) {
        return withTimeoutOrNull(DelayKt.m1576toDelayMillisLRDsOJo(j), function2, continuation);
    }

    private static final <U, T extends U> Object setupTimeout(TimeoutCoroutine<U, ? super T> timeoutCoroutine, Function2<? super CoroutineScope, ? super Continuation<? super T>, ? extends Object> function2) {
        JobKt.disposeOnCompletion(timeoutCoroutine, DelayKt.getDelay(timeoutCoroutine.uCont.getContext()).invokeOnTimeout(timeoutCoroutine.time, timeoutCoroutine, timeoutCoroutine.getContext()));
        return UndispatchedKt.startUndispatchedOrReturnIgnoreTimeout(timeoutCoroutine, timeoutCoroutine, function2);
    }

    public static final TimeoutCancellationException TimeoutCancellationException(long j, Job job) {
        return new TimeoutCancellationException("Timed out waiting for " + j + " ms", job);
    }
}
