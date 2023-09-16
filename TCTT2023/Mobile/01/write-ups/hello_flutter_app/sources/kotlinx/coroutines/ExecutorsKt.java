package kotlinx.coroutines;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import kotlin.Metadata;
/* compiled from: Executors.kt */
@Metadata(d1 = {"\u0000\u0018\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0011\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0007¢\u0006\u0002\b\u0003\u001a\u0011\u0010\u0000\u001a\u00020\u0004*\u00020\u0005H\u0007¢\u0006\u0002\b\u0003\u001a\n\u0010\u0006\u001a\u00020\u0002*\u00020\u0001¨\u0006\u0007"}, d2 = {"asCoroutineDispatcher", "Lkotlinx/coroutines/CoroutineDispatcher;", "Ljava/util/concurrent/Executor;", "from", "Lkotlinx/coroutines/ExecutorCoroutineDispatcher;", "Ljava/util/concurrent/ExecutorService;", "asExecutor", "kotlinx-coroutines-core"}, k = 2, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes.dex */
public final class ExecutorsKt {
    public static final ExecutorCoroutineDispatcher from(ExecutorService $this$asCoroutineDispatcher) {
        return new ExecutorCoroutineDispatcherImpl($this$asCoroutineDispatcher);
    }

    public static final CoroutineDispatcher from(Executor $this$asCoroutineDispatcher) {
        DispatcherExecutor dispatcherExecutor = $this$asCoroutineDispatcher instanceof DispatcherExecutor ? (DispatcherExecutor) $this$asCoroutineDispatcher : null;
        return dispatcherExecutor == null ? new ExecutorCoroutineDispatcherImpl($this$asCoroutineDispatcher) : dispatcherExecutor.dispatcher;
    }

    public static final Executor asExecutor(CoroutineDispatcher $this$asExecutor) {
        ExecutorCoroutineDispatcher executorCoroutineDispatcher = $this$asExecutor instanceof ExecutorCoroutineDispatcher ? (ExecutorCoroutineDispatcher) $this$asExecutor : null;
        return executorCoroutineDispatcher == null ? new DispatcherExecutor($this$asExecutor) : executorCoroutineDispatcher.getExecutor();
    }
}
