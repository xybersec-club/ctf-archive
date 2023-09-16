package kotlinx.coroutines;

import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.jvm.internal.CoroutineStackFrame;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.internal.DispatchedContinuation;
import kotlinx.coroutines.internal.StackTraceRecoveryKt;
import kotlinx.coroutines.internal.ThreadContextKt;
import kotlinx.coroutines.scheduling.Task;
import kotlinx.coroutines.scheduling.TaskContext;
/* compiled from: DispatchedTask.kt */
@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u000e\b \u0018\u0000*\u0006\b\u0000\u0010\u0001 \u00002\u00060\u0002j\u0002`\u0003B\r\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u001f\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0010¢\u0006\u0002\b\u0011J\u0019\u0010\u0012\u001a\u0004\u0018\u00010\u00102\b\u0010\u0013\u001a\u0004\u0018\u00010\u000eH\u0010¢\u0006\u0002\b\u0014J\u001f\u0010\u0015\u001a\u0002H\u0001\"\u0004\b\u0001\u0010\u00012\b\u0010\u0013\u001a\u0004\u0018\u00010\u000eH\u0010¢\u0006\u0004\b\u0016\u0010\u0017J\u001a\u0010\u0018\u001a\u00020\f2\b\u0010\u0019\u001a\u0004\u0018\u00010\u00102\b\u0010\u001a\u001a\u0004\u0018\u00010\u0010J\u0006\u0010\u001b\u001a\u00020\fJ\u000f\u0010\u001c\u001a\u0004\u0018\u00010\u000eH ¢\u0006\u0002\b\u001dR\u0018\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\bX \u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\nR\u0012\u0010\u0004\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006\u001e"}, d2 = {"Lkotlinx/coroutines/DispatchedTask;", "T", "Lkotlinx/coroutines/scheduling/Task;", "Lkotlinx/coroutines/SchedulerTask;", "resumeMode", "", "(I)V", "delegate", "Lkotlin/coroutines/Continuation;", "getDelegate$kotlinx_coroutines_core", "()Lkotlin/coroutines/Continuation;", "cancelCompletedResult", "", "takenState", "", "cause", "", "cancelCompletedResult$kotlinx_coroutines_core", "getExceptionalResult", "state", "getExceptionalResult$kotlinx_coroutines_core", "getSuccessfulResult", "getSuccessfulResult$kotlinx_coroutines_core", "(Ljava/lang/Object;)Ljava/lang/Object;", "handleFatalException", "exception", "finallyException", "run", "takeState", "takeState$kotlinx_coroutines_core", "kotlinx-coroutines-core"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes.dex */
public abstract class DispatchedTask<T> extends Task {
    public int resumeMode;

    public abstract Continuation<T> getDelegate$kotlinx_coroutines_core();

    public abstract Object takeState$kotlinx_coroutines_core();

    public DispatchedTask(int resumeMode) {
        this.resumeMode = resumeMode;
    }

    public void cancelCompletedResult$kotlinx_coroutines_core(Object takenState, Throwable cause) {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T> T getSuccessfulResult$kotlinx_coroutines_core(Object state) {
        return state;
    }

    public Throwable getExceptionalResult$kotlinx_coroutines_core(Object state) {
        CompletedExceptionally completedExceptionally = state instanceof CompletedExceptionally ? (CompletedExceptionally) state : null;
        if (completedExceptionally == null) {
            return null;
        }
        return completedExceptionally.cause;
    }

    @Override // java.lang.Runnable
    public final void run() {
        Object result;
        UndispatchedCoroutine undispatchedCoroutine;
        CancellationException cancellationException;
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(this.resumeMode != -1)) {
                throw new AssertionError();
            }
        }
        TaskContext taskContext = this.taskContext;
        Throwable fatalException = null;
        try {
            DispatchedContinuation delegate = (DispatchedContinuation) getDelegate$kotlinx_coroutines_core();
            Continuation continuation = delegate.continuation;
            Object countOrElement$iv = delegate.countOrElement;
            CoroutineContext context$iv = continuation.getContext();
            Object oldValue$iv = ThreadContextKt.updateThreadContext(context$iv, countOrElement$iv);
            Job job = null;
            if (oldValue$iv != ThreadContextKt.NO_THREAD_ELEMENTS) {
                undispatchedCoroutine = CoroutineContextKt.updateUndispatchedCompletion(continuation, context$iv, oldValue$iv);
            } else {
                UndispatchedCoroutine undispatchedCoroutine2 = null;
                undispatchedCoroutine = null;
            }
            UndispatchedCoroutine undispatchedCompletion$iv = undispatchedCoroutine;
            try {
                CoroutineContext context = continuation.getContext();
                Object state = takeState$kotlinx_coroutines_core();
                Throwable exception = getExceptionalResult$kotlinx_coroutines_core(state);
                if (exception == null) {
                    try {
                        if (DispatchedTaskKt.isCancellableMode(this.resumeMode)) {
                            job = (Job) context.get(Job.Key);
                        }
                    } catch (Throwable th) {
                        th = th;
                        if (undispatchedCompletion$iv != null) {
                        }
                        ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
                        throw th;
                    }
                }
                try {
                    if (job != null && !job.isActive()) {
                        CancellationException cause = job.getCancellationException();
                        cancelCompletedResult$kotlinx_coroutines_core(state, cause);
                        Result.Companion companion = Result.Companion;
                        if (DebugKt.getRECOVER_STACK_TRACES()) {
                            try {
                                if (continuation instanceof CoroutineStackFrame) {
                                    cancellationException = StackTraceRecoveryKt.recoverFromStackFrame(cause, (CoroutineStackFrame) continuation);
                                    continuation.resumeWith(Result.m38constructorimpl(ResultKt.createFailure(cancellationException)));
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                if (undispatchedCompletion$iv != null || undispatchedCompletion$iv.clearThreadContext()) {
                                    ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
                                }
                                throw th;
                            }
                        }
                        cancellationException = cause;
                        continuation.resumeWith(Result.m38constructorimpl(ResultKt.createFailure(cancellationException)));
                    } else if (exception != null) {
                        Result.Companion companion2 = Result.Companion;
                        continuation.resumeWith(Result.m38constructorimpl(ResultKt.createFailure(exception)));
                    } else {
                        T successfulResult$kotlinx_coroutines_core = getSuccessfulResult$kotlinx_coroutines_core(state);
                        Result.Companion companion3 = Result.Companion;
                        continuation.resumeWith(Result.m38constructorimpl(successfulResult$kotlinx_coroutines_core));
                    }
                    Unit unit = Unit.INSTANCE;
                    if (undispatchedCompletion$iv == null || undispatchedCompletion$iv.clearThreadContext()) {
                        ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
                    }
                    try {
                        Result.Companion companion4 = Result.Companion;
                        DispatchedTask<T> dispatchedTask = this;
                        taskContext.afterTask();
                        result = Result.m38constructorimpl(Unit.INSTANCE);
                    } catch (Throwable th3) {
                        th = th3;
                        Result.Companion companion5 = Result.Companion;
                        result = Result.m38constructorimpl(ResultKt.createFailure(th));
                        handleFatalException(fatalException, Result.m41exceptionOrNullimpl(result));
                    }
                } catch (Throwable th4) {
                    th = th4;
                }
            } catch (Throwable th5) {
                th = th5;
            }
        } catch (Throwable e) {
            fatalException = e;
            try {
                Result.Companion companion6 = Result.Companion;
                DispatchedTask<T> dispatchedTask2 = this;
                taskContext.afterTask();
                result = Result.m38constructorimpl(Unit.INSTANCE);
            } catch (Throwable th6) {
                th = th6;
                Result.Companion companion52 = Result.Companion;
                result = Result.m38constructorimpl(ResultKt.createFailure(th));
                handleFatalException(fatalException, Result.m41exceptionOrNullimpl(result));
            }
        }
        handleFatalException(fatalException, Result.m41exceptionOrNullimpl(result));
    }

    public final void handleFatalException(Throwable exception, Throwable finallyException) {
        if (exception == null && finallyException == null) {
            return;
        }
        if (exception != null && finallyException != null) {
            kotlin.ExceptionsKt.addSuppressed(exception, finallyException);
        }
        Throwable cause = exception == null ? finallyException : exception;
        Intrinsics.checkNotNull(cause);
        CoroutinesInternalError reason = new CoroutinesInternalError("Fatal exception in coroutines machinery for " + this + ". Please read KDoc to 'handleFatalException' method and report this incident to maintainers", cause);
        CoroutineExceptionHandlerKt.handleCoroutineException(getDelegate$kotlinx_coroutines_core().getContext(), reason);
    }
}
