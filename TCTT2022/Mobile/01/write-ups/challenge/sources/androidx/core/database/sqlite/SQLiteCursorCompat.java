package androidx.core.database.sqlite;

import android.database.sqlite.SQLiteCursor;
import android.os.Build;
/* loaded from: classes.dex */
public final class SQLiteCursorCompat {
    private SQLiteCursorCompat() {
    }

    public static void setFillWindowForwardOnly(SQLiteCursor sQLiteCursor, boolean z) {
        if (Build.VERSION.SDK_INT >= 28) {
            Api28Impl.setFillWindowForwardOnly(sQLiteCursor, z);
        }
    }

    /* loaded from: classes.dex */
    static class Api28Impl {
        private Api28Impl() {
        }

        static void setFillWindowForwardOnly(SQLiteCursor sQLiteCursor, boolean z) {
            sQLiteCursor.setFillWindowForwardOnly(z);
        }
    }
}
