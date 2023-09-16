package androidx.constraintlayout.motion.utils;

import java.util.Arrays;
/* loaded from: classes.dex */
public class Oscillator {
    public static final int BOUNCE = 6;
    public static final int COS_WAVE = 5;
    public static final int REVERSE_SAW_WAVE = 4;
    public static final int SAW_WAVE = 3;
    public static final int SIN_WAVE = 0;
    public static final int SQUARE_WAVE = 1;
    public static String TAG = "Oscillator";
    public static final int TRIANGLE_WAVE = 2;
    double[] mArea;
    int mType;
    float[] mPeriod = new float[0];
    double[] mPosition = new double[0];
    double PI2 = 6.283185307179586d;
    private boolean mNormalized = false;

    public String toString() {
        return "pos =" + Arrays.toString(this.mPosition) + " period=" + Arrays.toString(this.mPeriod);
    }

    public void setType(int i) {
        this.mType = i;
    }

    public void addPoint(double d, float f) {
        int length = this.mPeriod.length + 1;
        int binarySearch = Arrays.binarySearch(this.mPosition, d);
        if (binarySearch < 0) {
            binarySearch = (-binarySearch) - 1;
        }
        this.mPosition = Arrays.copyOf(this.mPosition, length);
        this.mPeriod = Arrays.copyOf(this.mPeriod, length);
        this.mArea = new double[length];
        double[] dArr = this.mPosition;
        System.arraycopy(dArr, binarySearch, dArr, binarySearch + 1, (length - binarySearch) - 1);
        this.mPosition[binarySearch] = d;
        this.mPeriod[binarySearch] = f;
        this.mNormalized = false;
    }

    public void normalize() {
        float[] fArr;
        float[] fArr2;
        float[] fArr3;
        int i;
        int i2 = 0;
        double d = 0.0d;
        while (true) {
            if (i2 >= this.mPeriod.length) {
                break;
            }
            d += fArr[i2];
            i2++;
        }
        double d2 = 0.0d;
        int i3 = 1;
        while (true) {
            if (i3 >= this.mPeriod.length) {
                break;
            }
            double[] dArr = this.mPosition;
            d2 += (dArr[i3] - dArr[i3 - 1]) * ((fArr2[i] + fArr2[i3]) / 2.0f);
            i3++;
        }
        int i4 = 0;
        while (true) {
            float[] fArr4 = this.mPeriod;
            if (i4 >= fArr4.length) {
                break;
            }
            fArr4[i4] = (float) (fArr4[i4] * (d / d2));
            i4++;
        }
        this.mArea[0] = 0.0d;
        int i5 = 1;
        while (true) {
            if (i5 < this.mPeriod.length) {
                int i6 = i5 - 1;
                double[] dArr2 = this.mPosition;
                double d3 = dArr2[i5] - dArr2[i6];
                double[] dArr3 = this.mArea;
                dArr3[i5] = dArr3[i6] + (d3 * ((fArr3[i6] + fArr3[i5]) / 2.0f));
                i5++;
            } else {
                this.mNormalized = true;
                return;
            }
        }
    }

    double getP(double d) {
        if (d < 0.0d) {
            d = 0.0d;
        } else if (d > 1.0d) {
            d = 1.0d;
        }
        int binarySearch = Arrays.binarySearch(this.mPosition, d);
        if (binarySearch > 0) {
            return 1.0d;
        }
        if (binarySearch != 0) {
            int i = (-binarySearch) - 1;
            float[] fArr = this.mPeriod;
            float f = fArr[i];
            int i2 = i - 1;
            float f2 = fArr[i2];
            double d2 = f - f2;
            double[] dArr = this.mPosition;
            double d3 = dArr[i];
            double d4 = dArr[i2];
            double d5 = d2 / (d3 - d4);
            return this.mArea[i2] + ((f2 - (d5 * d4)) * (d - d4)) + ((d5 * ((d * d) - (d4 * d4))) / 2.0d);
        }
        return 0.0d;
    }

    public double getValue(double d) {
        double abs;
        switch (this.mType) {
            case 1:
                return Math.signum(0.5d - (getP(d) % 1.0d));
            case 2:
                abs = Math.abs((((getP(d) * 4.0d) + 1.0d) % 4.0d) - 2.0d);
                break;
            case 3:
                return (((getP(d) * 2.0d) + 1.0d) % 2.0d) - 1.0d;
            case 4:
                abs = ((getP(d) * 2.0d) + 1.0d) % 2.0d;
                break;
            case 5:
                return Math.cos(this.PI2 * getP(d));
            case 6:
                double abs2 = 1.0d - Math.abs(((getP(d) * 4.0d) % 4.0d) - 2.0d);
                abs = abs2 * abs2;
                break;
            default:
                return Math.sin(this.PI2 * getP(d));
        }
        return 1.0d - abs;
    }

    double getDP(double d) {
        if (d <= 0.0d) {
            d = 1.0E-5d;
        } else if (d >= 1.0d) {
            d = 0.999999d;
        }
        int binarySearch = Arrays.binarySearch(this.mPosition, d);
        if (binarySearch <= 0 && binarySearch != 0) {
            int i = (-binarySearch) - 1;
            float[] fArr = this.mPeriod;
            float f = fArr[i];
            int i2 = i - 1;
            float f2 = fArr[i2];
            double d2 = f - f2;
            double[] dArr = this.mPosition;
            double d3 = dArr[i];
            double d4 = dArr[i2];
            double d5 = d2 / (d3 - d4);
            return (f2 - (d5 * d4)) + (d * d5);
        }
        return 0.0d;
    }

    public double getSlope(double d) {
        double dp;
        double signum;
        double dp2;
        double dp3;
        double sin;
        switch (this.mType) {
            case 1:
                return 0.0d;
            case 2:
                dp = getDP(d) * 4.0d;
                signum = Math.signum((((getP(d) * 4.0d) + 3.0d) % 4.0d) - 2.0d);
                return dp * signum;
            case 3:
                dp2 = getDP(d);
                return dp2 * 2.0d;
            case 4:
                dp2 = -getDP(d);
                return dp2 * 2.0d;
            case 5:
                dp3 = (-this.PI2) * getDP(d);
                sin = Math.sin(this.PI2 * getP(d));
                return dp3 * sin;
            case 6:
                dp = getDP(d) * 4.0d;
                signum = (((getP(d) * 4.0d) + 2.0d) % 4.0d) - 2.0d;
                return dp * signum;
            default:
                dp3 = this.PI2 * getDP(d);
                sin = Math.cos(this.PI2 * getP(d));
                return dp3 * sin;
        }
    }
}