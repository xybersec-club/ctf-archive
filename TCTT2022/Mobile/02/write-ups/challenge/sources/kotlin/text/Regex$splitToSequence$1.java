package kotlin.text;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.sequences.SequenceScope;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: Regex.kt */
@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlin/sequences/SequenceScope;", ""}, k = 3, mv = {1, 7, 1}, xi = 48)
@DebugMetadata(c = "kotlin.text.Regex$splitToSequence$1", f = "Regex.kt", i = {1, 1, 1}, l = {276, 284, 288}, m = "invokeSuspend", n = {"$this$sequence", "matcher", "splitCount"}, s = {"L$0", "L$1", "I$0"})
/* loaded from: classes.dex */
public final class Regex$splitToSequence$1 extends RestrictedSuspendLambda implements Function2<SequenceScope<? super String>, Continuation<? super Unit>, Object> {
    final /* synthetic */ CharSequence $input;
    final /* synthetic */ int $limit;
    int I$0;
    private /* synthetic */ Object L$0;
    Object L$1;
    int label;
    final /* synthetic */ Regex this$0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Regex$splitToSequence$1(Regex regex, CharSequence charSequence, int i, Continuation<? super Regex$splitToSequence$1> continuation) {
        super(2, continuation);
        this.this$0 = regex;
        this.$input = charSequence;
        this.$limit = i;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        Regex$splitToSequence$1 regex$splitToSequence$1 = new Regex$splitToSequence$1(this.this$0, this.$input, this.$limit, continuation);
        regex$splitToSequence$1.L$0 = obj;
        return regex$splitToSequence$1;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(SequenceScope<? super String> sequenceScope, Continuation<? super Unit> continuation) {
        return ((Regex$splitToSequence$1) create(sequenceScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0073 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x007e  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x00a2 A[RETURN] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:19:0x0071 -> B:21:0x0074). Please submit an issue!!! */
    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object invokeSuspend(java.lang.Object r10) {
        /*
            r9 = this;
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r1 = r9.label
            r2 = 0
            r3 = 3
            r4 = 2
            r5 = 1
            if (r1 == 0) goto L34
            if (r1 == r5) goto L2f
            if (r1 == r4) goto L1f
            if (r1 != r3) goto L17
            kotlin.ResultKt.throwOnFailure(r10)
            goto La3
        L17:
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException
            java.lang.String r10 = "call to 'resume' before 'invoke' with coroutine"
            r9.<init>(r10)
            throw r9
        L1f:
            int r1 = r9.I$0
            java.lang.Object r2 = r9.L$1
            java.util.regex.Matcher r2 = (java.util.regex.Matcher) r2
            java.lang.Object r6 = r9.L$0
            kotlin.sequences.SequenceScope r6 = (kotlin.sequences.SequenceScope) r6
            kotlin.ResultKt.throwOnFailure(r10)
            r10 = r1
            r1 = r2
            goto L74
        L2f:
            kotlin.ResultKt.throwOnFailure(r10)
            goto Lb8
        L34:
            kotlin.ResultKt.throwOnFailure(r10)
            java.lang.Object r10 = r9.L$0
            kotlin.sequences.SequenceScope r10 = (kotlin.sequences.SequenceScope) r10
            kotlin.text.Regex r1 = r9.this$0
            java.util.regex.Pattern r1 = kotlin.text.Regex.access$getNativePattern$p(r1)
            java.lang.CharSequence r6 = r9.$input
            java.util.regex.Matcher r1 = r1.matcher(r6)
            int r6 = r9.$limit
            if (r6 == r5) goto La6
            boolean r6 = r1.find()
            if (r6 != 0) goto L52
            goto La6
        L52:
            r6 = r10
            r10 = r2
        L54:
            java.lang.CharSequence r7 = r9.$input
            int r8 = r1.start()
            java.lang.CharSequence r2 = r7.subSequence(r2, r8)
            java.lang.String r2 = r2.toString()
            r7 = r9
            kotlin.coroutines.Continuation r7 = (kotlin.coroutines.Continuation) r7
            r9.L$0 = r6
            r9.L$1 = r1
            r9.I$0 = r10
            r9.label = r4
            java.lang.Object r2 = r6.yield(r2, r7)
            if (r2 != r0) goto L74
            return r0
        L74:
            int r2 = r1.end()
            int r10 = r10 + r5
            int r7 = r9.$limit
            int r7 = r7 - r5
            if (r10 == r7) goto L84
            boolean r7 = r1.find()
            if (r7 != 0) goto L54
        L84:
            java.lang.CharSequence r10 = r9.$input
            int r1 = r10.length()
            java.lang.CharSequence r10 = r10.subSequence(r2, r1)
            java.lang.String r10 = r10.toString()
            r1 = r9
            kotlin.coroutines.Continuation r1 = (kotlin.coroutines.Continuation) r1
            r2 = 0
            r9.L$0 = r2
            r9.L$1 = r2
            r9.label = r3
            java.lang.Object r9 = r6.yield(r10, r1)
            if (r9 != r0) goto La3
            return r0
        La3:
            kotlin.Unit r9 = kotlin.Unit.INSTANCE
            return r9
        La6:
            java.lang.CharSequence r1 = r9.$input
            java.lang.String r1 = r1.toString()
            r2 = r9
            kotlin.coroutines.Continuation r2 = (kotlin.coroutines.Continuation) r2
            r9.label = r5
            java.lang.Object r9 = r10.yield(r1, r2)
            if (r9 != r0) goto Lb8
            return r0
        Lb8:
            kotlin.Unit r9 = kotlin.Unit.INSTANCE
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.text.Regex$splitToSequence$1.invokeSuspend(java.lang.Object):java.lang.Object");
    }
}
