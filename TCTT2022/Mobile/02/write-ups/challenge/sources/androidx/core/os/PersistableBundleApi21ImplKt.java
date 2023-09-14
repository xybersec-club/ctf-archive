package androidx.core.os;

import android.os.Build;
import android.os.PersistableBundle;
import java.util.Objects;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Typography;
/* compiled from: PersistableBundle.kt */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\bÃ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J$\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00042\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u0007¨\u0006\r"}, d2 = {"Landroidx/core/os/PersistableBundleApi21ImplKt;", "", "()V", "createPersistableBundle", "Landroid/os/PersistableBundle;", "capacity", "", "putValue", "", "persistableBundle", "key", "", "value", "core-ktx_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* loaded from: classes.dex */
final class PersistableBundleApi21ImplKt {
    public static final PersistableBundleApi21ImplKt INSTANCE = new PersistableBundleApi21ImplKt();

    private PersistableBundleApi21ImplKt() {
    }

    @JvmStatic
    public static final PersistableBundle createPersistableBundle(int i) {
        return new PersistableBundle(i);
    }

    @JvmStatic
    public static final void putValue(PersistableBundle persistableBundle, String str, Object obj) {
        Intrinsics.checkNotNullParameter(persistableBundle, "persistableBundle");
        if (obj == null) {
            persistableBundle.putString(str, null);
        } else if (obj instanceof Boolean) {
            if (Build.VERSION.SDK_INT >= 22) {
                PersistableBundleApi22ImplKt.putBoolean(persistableBundle, str, ((Boolean) obj).booleanValue());
                return;
            }
            throw new IllegalArgumentException("Illegal value type boolean for key \"" + str + Typography.quote);
        } else if (obj instanceof Double) {
            persistableBundle.putDouble(str, ((Number) obj).doubleValue());
        } else if (obj instanceof Integer) {
            persistableBundle.putInt(str, ((Number) obj).intValue());
        } else if (obj instanceof Long) {
            persistableBundle.putLong(str, ((Number) obj).longValue());
        } else if (obj instanceof String) {
            persistableBundle.putString(str, (String) obj);
        } else if (obj instanceof boolean[]) {
            if (Build.VERSION.SDK_INT >= 22) {
                PersistableBundleApi22ImplKt.putBooleanArray(persistableBundle, str, (boolean[]) obj);
                return;
            }
            throw new IllegalArgumentException("Illegal value type boolean[] for key \"" + str + Typography.quote);
        } else if (obj instanceof double[]) {
            persistableBundle.putDoubleArray(str, (double[]) obj);
        } else if (obj instanceof int[]) {
            persistableBundle.putIntArray(str, (int[]) obj);
        } else if (obj instanceof long[]) {
            persistableBundle.putLongArray(str, (long[]) obj);
        } else if (obj instanceof Object[]) {
            Class<?> componentType = obj.getClass().getComponentType();
            Intrinsics.checkNotNull(componentType);
            if (String.class.isAssignableFrom(componentType)) {
                Objects.requireNonNull(obj, "null cannot be cast to non-null type kotlin.Array<kotlin.String>");
                persistableBundle.putStringArray(str, (String[]) obj);
                return;
            }
            throw new IllegalArgumentException("Illegal value array type " + componentType.getCanonicalName() + " for key \"" + str + Typography.quote);
        } else {
            throw new IllegalArgumentException("Illegal value type " + obj.getClass().getCanonicalName() + " for key \"" + str + Typography.quote);
        }
    }
}
