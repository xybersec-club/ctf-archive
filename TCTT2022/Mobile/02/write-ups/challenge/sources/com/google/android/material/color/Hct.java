package com.google.android.material.color;
/* loaded from: classes.dex */
final class Hct {
    private static final float CHROMA_SEARCH_ENDPOINT = 0.4f;
    private static final float DE_MAX = 1.0f;
    private static final float DE_MAX_ERROR = 1.0E-9f;
    private static final float DL_MAX = 0.2f;
    private static final float LIGHTNESS_SEARCH_ENDPOINT = 0.01f;
    private float chroma;
    private float hue;
    private float tone;

    public static Hct from(float f, float f2, float f3) {
        return new Hct(f, f2, f3);
    }

    public static Hct fromInt(int i) {
        Cam16 fromInt = Cam16.fromInt(i);
        return new Hct(fromInt.getHue(), fromInt.getChroma(), ColorUtils.lstarFromInt(i));
    }

    private Hct(float f, float f2, float f3) {
        setInternalState(gamutMap(f, f2, f3));
    }

    public float getHue() {
        return this.hue;
    }

    public float getChroma() {
        return this.chroma;
    }

    public float getTone() {
        return this.tone;
    }

    public int toInt() {
        return gamutMap(this.hue, this.chroma, this.tone);
    }

    public void setHue(float f) {
        setInternalState(gamutMap(MathUtils.sanitizeDegrees(f), this.chroma, this.tone));
    }

    public void setChroma(float f) {
        setInternalState(gamutMap(this.hue, f, this.tone));
    }

    public void setTone(float f) {
        setInternalState(gamutMap(this.hue, this.chroma, f));
    }

    private void setInternalState(int i) {
        Cam16 fromInt = Cam16.fromInt(i);
        float lstarFromInt = ColorUtils.lstarFromInt(i);
        this.hue = fromInt.getHue();
        this.chroma = fromInt.getChroma();
        this.tone = lstarFromInt;
    }

    private static int gamutMap(float f, float f2, float f3) {
        return gamutMapInViewingConditions(f, f2, f3, ViewingConditions.DEFAULT);
    }

    static int gamutMapInViewingConditions(float f, float f2, float f3, ViewingConditions viewingConditions) {
        if (f2 < 1.0d || Math.round(f3) <= 0.0d || Math.round(f3) >= 100.0d) {
            return ColorUtils.intFromLstar(f3);
        }
        float sanitizeDegrees = MathUtils.sanitizeDegrees(f);
        Cam16 cam16 = null;
        boolean z = true;
        float f4 = 0.0f;
        float f5 = f2;
        while (Math.abs(f4 - f2) >= CHROMA_SEARCH_ENDPOINT) {
            Cam16 findCamByJ = findCamByJ(sanitizeDegrees, f5, f3);
            if (z) {
                if (findCamByJ != null) {
                    return findCamByJ.viewed(viewingConditions);
                }
                z = false;
            } else if (findCamByJ == null) {
                f2 = f5;
            } else {
                f4 = f5;
                cam16 = findCamByJ;
            }
            f5 = ((f2 - f4) / 2.0f) + f4;
        }
        if (cam16 == null) {
            return ColorUtils.intFromLstar(f3);
        }
        return cam16.viewed(viewingConditions);
    }

    private static Cam16 findCamByJ(float f, float f2, float f3) {
        float f4 = 1000.0f;
        float f5 = 0.0f;
        Cam16 cam16 = null;
        float f6 = 100.0f;
        float f7 = 1000.0f;
        while (Math.abs(f5 - f6) > LIGHTNESS_SEARCH_ENDPOINT) {
            float f8 = ((f6 - f5) / 2.0f) + f5;
            int i = Cam16.fromJch(f8, f2, f).getInt();
            float lstarFromInt = ColorUtils.lstarFromInt(i);
            float abs = Math.abs(f3 - lstarFromInt);
            if (abs < 0.2f) {
                Cam16 fromInt = Cam16.fromInt(i);
                float distance = fromInt.distance(Cam16.fromJch(fromInt.getJ(), fromInt.getChroma(), f));
                if (distance <= 1.0f && distance <= f4) {
                    cam16 = fromInt;
                    f7 = abs;
                    f4 = distance;
                }
            }
            if (f7 == 0.0f && f4 < DE_MAX_ERROR) {
                break;
            } else if (lstarFromInt < f3) {
                f5 = f8;
            } else {
                f6 = f8;
            }
        }
        return cam16;
    }
}
