package android.support.v4.text;

import android.os.Build;
import java.util.Locale;
/* loaded from: classes.dex */
public final class ICUCompat {
    private static final ICUCompatBaseImpl IMPL;

    /* loaded from: classes.dex */
    static class ICUCompatBaseImpl {
        ICUCompatBaseImpl() {
        }

        public String maximizeAndGetScript(Locale locale) {
            return ICUCompatIcs.maximizeAndGetScript(locale);
        }
    }

    /* loaded from: classes.dex */
    static class ICUCompatApi21Impl extends ICUCompatBaseImpl {
        ICUCompatApi21Impl() {
        }

        @Override // android.support.v4.text.ICUCompat.ICUCompatBaseImpl
        public String maximizeAndGetScript(Locale locale) {
            return ICUCompatApi21.maximizeAndGetScript(locale);
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new ICUCompatApi21Impl();
        } else {
            IMPL = new ICUCompatBaseImpl();
        }
    }

    public static String maximizeAndGetScript(Locale locale) {
        return IMPL.maximizeAndGetScript(locale);
    }

    private ICUCompat() {
    }
}
