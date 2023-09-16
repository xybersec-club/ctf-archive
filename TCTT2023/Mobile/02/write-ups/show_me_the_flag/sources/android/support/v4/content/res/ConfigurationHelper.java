package android.support.v4.content.res;

import android.content.res.Resources;
import android.os.Build;
/* loaded from: classes.dex */
public final class ConfigurationHelper {
    private ConfigurationHelper() {
    }

    @Deprecated
    public static int getScreenHeightDp(Resources resources) {
        return resources.getConfiguration().screenHeightDp;
    }

    @Deprecated
    public static int getScreenWidthDp(Resources resources) {
        return resources.getConfiguration().screenWidthDp;
    }

    @Deprecated
    public static int getSmallestScreenWidthDp(Resources resources) {
        return resources.getConfiguration().smallestScreenWidthDp;
    }

    public static int getDensityDpi(Resources resources) {
        if (Build.VERSION.SDK_INT >= 17) {
            return resources.getConfiguration().densityDpi;
        }
        return resources.getDisplayMetrics().densityDpi;
    }
}
