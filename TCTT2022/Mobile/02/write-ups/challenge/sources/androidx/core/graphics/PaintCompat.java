package androidx.core.graphics;

import android.graphics.BlendMode;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import androidx.core.graphics.BlendModeUtils;
import androidx.core.util.Pair;
/* loaded from: classes.dex */
public final class PaintCompat {
    private static final String EM_STRING = "m";
    private static final String TOFU_STRING = "\udfffd";
    private static final ThreadLocal<Pair<Rect, Rect>> sRectThreadLocal = new ThreadLocal<>();

    public static boolean hasGlyph(Paint paint, String str) {
        if (Build.VERSION.SDK_INT >= 23) {
            return Api23Impl.hasGlyph(paint, str);
        }
        int length = str.length();
        if (length == 1 && Character.isWhitespace(str.charAt(0))) {
            return true;
        }
        float measureText = paint.measureText(TOFU_STRING);
        float measureText2 = paint.measureText(EM_STRING);
        float measureText3 = paint.measureText(str);
        float f = 0.0f;
        if (measureText3 == 0.0f) {
            return false;
        }
        if (str.codePointCount(0, str.length()) > 1) {
            if (measureText3 > measureText2 * 2.0f) {
                return false;
            }
            int i = 0;
            while (i < length) {
                int charCount = Character.charCount(str.codePointAt(i)) + i;
                f += paint.measureText(str, i, charCount);
                i = charCount;
            }
            if (measureText3 >= f) {
                return false;
            }
        }
        if (measureText3 != measureText) {
            return true;
        }
        Pair<Rect, Rect> obtainEmptyRects = obtainEmptyRects();
        paint.getTextBounds(TOFU_STRING, 0, 2, obtainEmptyRects.first);
        paint.getTextBounds(str, 0, length, obtainEmptyRects.second);
        return !obtainEmptyRects.first.equals(obtainEmptyRects.second);
    }

    public static boolean setBlendMode(Paint paint, BlendModeCompat blendModeCompat) {
        if (Build.VERSION.SDK_INT >= 29) {
            Api29Impl.setBlendMode(paint, blendModeCompat != null ? BlendModeUtils.Api29Impl.obtainBlendModeFromCompat(blendModeCompat) : null);
            return true;
        } else if (blendModeCompat != null) {
            PorterDuff.Mode obtainPorterDuffFromCompat = BlendModeUtils.obtainPorterDuffFromCompat(blendModeCompat);
            paint.setXfermode(obtainPorterDuffFromCompat != null ? new PorterDuffXfermode(obtainPorterDuffFromCompat) : null);
            return obtainPorterDuffFromCompat != null;
        } else {
            paint.setXfermode(null);
            return true;
        }
    }

    private static Pair<Rect, Rect> obtainEmptyRects() {
        ThreadLocal<Pair<Rect, Rect>> threadLocal = sRectThreadLocal;
        Pair<Rect, Rect> pair = threadLocal.get();
        if (pair == null) {
            Pair<Rect, Rect> pair2 = new Pair<>(new Rect(), new Rect());
            threadLocal.set(pair2);
            return pair2;
        }
        pair.first.setEmpty();
        pair.second.setEmpty();
        return pair;
    }

    private PaintCompat() {
    }

    /* loaded from: classes.dex */
    static class Api29Impl {
        private Api29Impl() {
        }

        static void setBlendMode(Paint paint, Object obj) {
            paint.setBlendMode((BlendMode) obj);
        }
    }

    /* loaded from: classes.dex */
    static class Api23Impl {
        private Api23Impl() {
        }

        static boolean hasGlyph(Paint paint, String str) {
            return paint.hasGlyph(str);
        }
    }
}
