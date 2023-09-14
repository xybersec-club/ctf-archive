package androidx.core.util;

import android.os.Build;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes.dex */
public class ObjectsCompat {
    private ObjectsCompat() {
    }

    public static boolean equals(Object obj, Object obj2) {
        if (Build.VERSION.SDK_INT >= 19) {
            return Api19Impl.equals(obj, obj2);
        }
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static int hashCode(Object obj) {
        if (obj != null) {
            return obj.hashCode();
        }
        return 0;
    }

    public static int hash(Object... objArr) {
        if (Build.VERSION.SDK_INT >= 19) {
            return Api19Impl.hash(objArr);
        }
        return Arrays.hashCode(objArr);
    }

    public static String toString(Object obj, String str) {
        return obj != null ? obj.toString() : str;
    }

    public static <T> T requireNonNull(T t) {
        Objects.requireNonNull(t);
        return t;
    }

    public static <T> T requireNonNull(T t, String str) {
        Objects.requireNonNull(t, str);
        return t;
    }

    /* loaded from: classes.dex */
    static class Api19Impl {
        private Api19Impl() {
        }

        static boolean equals(Object obj, Object obj2) {
            return Objects.equals(obj, obj2);
        }

        static int hash(Object... objArr) {
            return Objects.hash(objArr);
        }
    }
}
