package android.support.v4.content;

import android.os.AsyncTask;
import java.util.concurrent.Executor;
@Deprecated
/* loaded from: classes.dex */
public final class ParallelExecutorCompat {
    @Deprecated
    public static Executor getParallelExecutor() {
        return AsyncTask.THREAD_POOL_EXECUTOR;
    }

    private ParallelExecutorCompat() {
    }
}
