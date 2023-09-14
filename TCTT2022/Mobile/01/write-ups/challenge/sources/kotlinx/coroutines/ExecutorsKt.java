package kotlinx.coroutines;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import kotlin.Metadata;
/* compiled from: Executors.kt */
@Metadata(d1 = {"\u0000\u001e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0011\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0007¢\u0006\u0002\b\u0003\u001a\u0011\u0010\u0000\u001a\u00020\u0004*\u00020\u0005H\u0007¢\u0006\u0002\b\u0003\u001a\n\u0010\u0006\u001a\u00020\u0002*\u00020\u0001*\u0010\b\u0007\u0010\u0007\"\u00020\u00042\u00020\u0004B\u0002\b\b¨\u0006\t"}, d2 = {"asCoroutineDispatcher", "Lkotlinx/coroutines/CoroutineDispatcher;", "Ljava/util/concurrent/Executor;", TypedValues.TransitionType.S_FROM, "Lkotlinx/coroutines/ExecutorCoroutineDispatcher;", "Ljava/util/concurrent/ExecutorService;", "asExecutor", "CloseableCoroutineDispatcher", "Lkotlinx/coroutines/ExperimentalCoroutinesApi;", "kotlinx-coroutines-core"}, k = 2, mv = {1, 6, 0}, xi = 48)
/* loaded from: classes.dex */
public final class ExecutorsKt {
    public static /* synthetic */ void CloseableCoroutineDispatcher$annotations() {
    }

    public static final ExecutorCoroutineDispatcher from(ExecutorService executorService) {
        return new ExecutorCoroutineDispatcherImpl(executorService);
    }

    public static final CoroutineDispatcher from(Executor executor) {
        DispatcherExecutor dispatcherExecutor = executor instanceof DispatcherExecutor ? (DispatcherExecutor) executor : null;
        CoroutineDispatcher coroutineDispatcher = dispatcherExecutor != null ? dispatcherExecutor.dispatcher : null;
        return coroutineDispatcher == null ? new ExecutorCoroutineDispatcherImpl(executor) : coroutineDispatcher;
    }

    public static final Executor asExecutor(CoroutineDispatcher coroutineDispatcher) {
        ExecutorCoroutineDispatcher executorCoroutineDispatcher = coroutineDispatcher instanceof ExecutorCoroutineDispatcher ? (ExecutorCoroutineDispatcher) coroutineDispatcher : null;
        Executor executor = executorCoroutineDispatcher != null ? executorCoroutineDispatcher.getExecutor() : null;
        return executor == null ? new DispatcherExecutor(coroutineDispatcher) : executor;
    }
}
