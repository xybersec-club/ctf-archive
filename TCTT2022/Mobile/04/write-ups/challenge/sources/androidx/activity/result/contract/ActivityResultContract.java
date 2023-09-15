package androidx.activity.result.contract;

import android.content.Context;
import android.content.Intent;
/* loaded from: classes.dex */
public abstract class ActivityResultContract<I, O> {
    public abstract Intent createIntent(Context context, I i);

    public SynchronousResult<O> getSynchronousResult(Context context, I i) {
        return null;
    }

    public abstract O parseResult(int i, Intent intent);

    /* loaded from: classes.dex */
    public static final class SynchronousResult<T> {
        private final T mValue;

        public SynchronousResult(T t) {
            this.mValue = t;
        }

        public T getValue() {
            return this.mValue;
        }
    }
}
