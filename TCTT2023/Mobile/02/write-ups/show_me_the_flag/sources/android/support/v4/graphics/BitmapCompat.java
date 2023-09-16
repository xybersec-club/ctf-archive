package android.support.v4.graphics;

import android.graphics.Bitmap;
import android.os.Build;
/* loaded from: classes.dex */
public final class BitmapCompat {
    static final BitmapCompatBaseImpl IMPL;

    /* loaded from: classes.dex */
    static class BitmapCompatBaseImpl {
        public boolean hasMipMap(Bitmap bitmap) {
            return false;
        }

        public void setHasMipMap(Bitmap bitmap, boolean z) {
        }

        BitmapCompatBaseImpl() {
        }

        public int getAllocationByteCount(Bitmap bitmap) {
            return bitmap.getByteCount();
        }
    }

    /* loaded from: classes.dex */
    static class BitmapCompatApi18Impl extends BitmapCompatBaseImpl {
        BitmapCompatApi18Impl() {
        }

        @Override // android.support.v4.graphics.BitmapCompat.BitmapCompatBaseImpl
        public boolean hasMipMap(Bitmap bitmap) {
            return bitmap.hasMipMap();
        }

        @Override // android.support.v4.graphics.BitmapCompat.BitmapCompatBaseImpl
        public void setHasMipMap(Bitmap bitmap, boolean z) {
            bitmap.setHasMipMap(z);
        }
    }

    /* loaded from: classes.dex */
    static class BitmapCompatApi19Impl extends BitmapCompatApi18Impl {
        BitmapCompatApi19Impl() {
        }

        @Override // android.support.v4.graphics.BitmapCompat.BitmapCompatBaseImpl
        public int getAllocationByteCount(Bitmap bitmap) {
            return bitmap.getAllocationByteCount();
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 19) {
            IMPL = new BitmapCompatApi19Impl();
        } else if (Build.VERSION.SDK_INT >= 18) {
            IMPL = new BitmapCompatApi18Impl();
        } else {
            IMPL = new BitmapCompatBaseImpl();
        }
    }

    public static boolean hasMipMap(Bitmap bitmap) {
        return IMPL.hasMipMap(bitmap);
    }

    public static void setHasMipMap(Bitmap bitmap, boolean z) {
        IMPL.setHasMipMap(bitmap, z);
    }

    public static int getAllocationByteCount(Bitmap bitmap) {
        return IMPL.getAllocationByteCount(bitmap);
    }

    private BitmapCompat() {
    }
}
