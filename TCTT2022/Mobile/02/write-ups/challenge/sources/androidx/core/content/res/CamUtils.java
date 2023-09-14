package androidx.core.content.res;

import android.graphics.Color;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
/* loaded from: classes.dex */
final class CamUtils {
    static final float[][] XYZ_TO_CAM16RGB = {new float[]{0.401288f, 0.650173f, -0.051461f}, new float[]{-0.250268f, 1.204414f, 0.045854f}, new float[]{-0.002079f, 0.048952f, 0.953127f}};
    static final float[][] CAM16RGB_TO_XYZ = {new float[]{1.8620678f, -1.0112547f, 0.14918678f}, new float[]{0.38752654f, 0.62144744f, -0.00897398f}, new float[]{-0.0158415f, -0.03412294f, 1.0499644f}};
    static final float[] WHITE_POINT_D65 = {95.047f, 100.0f, 108.883f};
    static final float[][] SRGB_TO_XYZ = {new float[]{0.41233894f, 0.35762063f, 0.18051042f}, new float[]{0.2126f, 0.7152f, 0.0722f}, new float[]{0.01932141f, 0.11916382f, 0.9503448f}};

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float lerp(float f, float f2, float f3) {
        return f + ((f2 - f) * f3);
    }

    private CamUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int intFromLStar(float f) {
        if (f < 1.0f) {
            return ViewCompat.MEASURED_STATE_MASK;
        }
        if (f > 99.0f) {
            return -1;
        }
        float f2 = (f + 16.0f) / 116.0f;
        float f3 = (f > 8.0f ? 1 : (f == 8.0f ? 0 : -1)) > 0 ? f2 * f2 * f2 : f / 903.2963f;
        float f4 = f2 * f2 * f2;
        boolean z = f4 > 0.008856452f;
        float f5 = z ? f4 : ((f2 * 116.0f) - 16.0f) / 903.2963f;
        if (!z) {
            f4 = ((f2 * 116.0f) - 16.0f) / 903.2963f;
        }
        float[] fArr = WHITE_POINT_D65;
        return ColorUtils.XYZToColor(f5 * fArr[0], f3 * fArr[1], f4 * fArr[2]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float lStarFromInt(int i) {
        return lStarFromY(yFromInt(i));
    }

    static float lStarFromY(float f) {
        float f2 = f / 100.0f;
        return f2 <= 0.008856452f ? f2 * 903.2963f : (((float) Math.cbrt(f2)) * 116.0f) - 16.0f;
    }

    static float yFromInt(int i) {
        float linearized = linearized(Color.red(i));
        float linearized2 = linearized(Color.green(i));
        float linearized3 = linearized(Color.blue(i));
        float[][] fArr = SRGB_TO_XYZ;
        return (linearized * fArr[1][0]) + (linearized2 * fArr[1][1]) + (linearized3 * fArr[1][2]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float[] xyzFromInt(int i) {
        float linearized = linearized(Color.red(i));
        float linearized2 = linearized(Color.green(i));
        float linearized3 = linearized(Color.blue(i));
        float[][] fArr = SRGB_TO_XYZ;
        return new float[]{(fArr[0][0] * linearized) + (fArr[0][1] * linearized2) + (fArr[0][2] * linearized3), (fArr[1][0] * linearized) + (fArr[1][1] * linearized2) + (fArr[1][2] * linearized3), (linearized * fArr[2][0]) + (linearized2 * fArr[2][1]) + (linearized3 * fArr[2][2])};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float yFromLStar(float f) {
        return (f > 8.0f ? (float) Math.pow((f + 16.0d) / 116.0d, 3.0d) : f / 903.2963f) * 100.0f;
    }

    static float linearized(int i) {
        float f = i / 255.0f;
        return (f <= 0.04045f ? f / 12.92f : (float) Math.pow((f + 0.055f) / 1.055f, 2.4000000953674316d)) * 100.0f;
    }
}
