package androidx.core.view.animation;

import android.graphics.Path;
import android.os.Build;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
/* loaded from: classes.dex */
public final class PathInterpolatorCompat {
    private PathInterpolatorCompat() {
    }

    public static Interpolator create(Path path) {
        if (Build.VERSION.SDK_INT >= 21) {
            return Api21Impl.createPathInterpolator(path);
        }
        return new PathInterpolatorApi14(path);
    }

    public static Interpolator create(float f, float f2) {
        if (Build.VERSION.SDK_INT >= 21) {
            return Api21Impl.createPathInterpolator(f, f2);
        }
        return new PathInterpolatorApi14(f, f2);
    }

    public static Interpolator create(float f, float f2, float f3, float f4) {
        if (Build.VERSION.SDK_INT >= 21) {
            return Api21Impl.createPathInterpolator(f, f2, f3, f4);
        }
        return new PathInterpolatorApi14(f, f2, f3, f4);
    }

    /* loaded from: classes.dex */
    static class Api21Impl {
        private Api21Impl() {
        }

        static PathInterpolator createPathInterpolator(Path path) {
            return new PathInterpolator(path);
        }

        static PathInterpolator createPathInterpolator(float f, float f2) {
            return new PathInterpolator(f, f2);
        }

        static PathInterpolator createPathInterpolator(float f, float f2, float f3, float f4) {
            return new PathInterpolator(f, f2, f3, f4);
        }
    }
}
