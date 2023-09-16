package androidx.core.content.res;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.util.TypedValue;
import android.util.Xml;
import androidx.core.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class ColorStateListInflaterCompat {
    private static final ThreadLocal<TypedValue> sTempTypedValue = new ThreadLocal<>();

    private ColorStateListInflaterCompat() {
    }

    public static ColorStateList inflate(Resources resources, int resId, Resources.Theme theme) {
        try {
            XmlPullParser parser = resources.getXml(resId);
            return createFromXml(resources, parser, theme);
        } catch (Exception e) {
            Log.e("CSLCompat", "Failed to inflate ColorStateList.", e);
            return null;
        }
    }

    public static ColorStateList createFromXml(Resources r, XmlPullParser parser, Resources.Theme theme) throws XmlPullParserException, IOException {
        int type;
        AttributeSet attrs = Xml.asAttributeSet(parser);
        do {
            type = parser.next();
            if (type == 2) {
                break;
            }
        } while (type != 1);
        if (type != 2) {
            throw new XmlPullParserException("No start tag found");
        }
        return createFromXmlInner(r, parser, attrs, theme);
    }

    public static ColorStateList createFromXmlInner(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        String name = parser.getName();
        if (!name.equals("selector")) {
            throw new XmlPullParserException(parser.getPositionDescription() + ": invalid color state list tag " + name);
        }
        return inflate(r, parser, attrs, theme);
    }

    private static ColorStateList inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        int depth;
        int baseColor;
        Resources resources = r;
        Resources.Theme theme2 = theme;
        int i = 1;
        int innerDepth = parser.getDepth() + 1;
        int[][] stateSpecList = new int[20];
        int[] colorList = new int[stateSpecList.length];
        int listSize = 0;
        int[] colorList2 = colorList;
        int[][] stateSpecList2 = stateSpecList;
        while (true) {
            int type = parser.next();
            if (type == i || ((depth = parser.getDepth()) < innerDepth && type == 3)) {
                break;
            } else if (type != 2 || depth > innerDepth || !parser.getName().equals("item")) {
                i = 1;
                resources = r;
                theme2 = theme;
            } else {
                TypedArray a = obtainAttributes(resources, theme2, attrs, R.styleable.ColorStateListItem);
                int resourceId = a.getResourceId(R.styleable.ColorStateListItem_android_color, -1);
                if (resourceId != -1 && !isColorInt(resources, resourceId)) {
                    try {
                        baseColor = createFromXml(resources, resources.getXml(resourceId), theme2).getDefaultColor();
                    } catch (Exception e) {
                        baseColor = a.getColor(R.styleable.ColorStateListItem_android_color, -65281);
                    }
                } else {
                    int baseColor2 = R.styleable.ColorStateListItem_android_color;
                    baseColor = a.getColor(baseColor2, -65281);
                }
                float alphaMod = 1.0f;
                if (a.hasValue(R.styleable.ColorStateListItem_android_alpha)) {
                    alphaMod = a.getFloat(R.styleable.ColorStateListItem_android_alpha, 1.0f);
                } else if (a.hasValue(R.styleable.ColorStateListItem_alpha)) {
                    alphaMod = a.getFloat(R.styleable.ColorStateListItem_alpha, 1.0f);
                }
                a.recycle();
                int j = 0;
                int numAttrs = attrs.getAttributeCount();
                int[] stateSpec = new int[numAttrs];
                int i2 = 0;
                while (i2 < numAttrs) {
                    int stateResId = attrs.getAttributeNameResource(i2);
                    int numAttrs2 = numAttrs;
                    if (stateResId != 16843173 && stateResId != 16843551 && stateResId != R.attr.alpha) {
                        int j2 = j + 1;
                        stateSpec[j] = attrs.getAttributeBooleanValue(i2, false) ? stateResId : -stateResId;
                        j = j2;
                    }
                    i2++;
                    numAttrs = numAttrs2;
                }
                int[] stateSpec2 = StateSet.trimStateSet(stateSpec, j);
                int color = modulateColorAlpha(baseColor, alphaMod);
                colorList2 = GrowingArrayUtils.append(colorList2, listSize, color);
                stateSpecList2 = (int[][]) GrowingArrayUtils.append(stateSpecList2, listSize, stateSpec2);
                listSize++;
                i = 1;
                resources = r;
                theme2 = theme;
            }
        }
        int[] colors = new int[listSize];
        int[][] stateSpecs = new int[listSize];
        System.arraycopy(colorList2, 0, colors, 0, listSize);
        System.arraycopy(stateSpecList2, 0, stateSpecs, 0, listSize);
        return new ColorStateList(stateSpecs, colors);
    }

    private static boolean isColorInt(Resources r, int resId) {
        TypedValue value = getTypedValue();
        r.getValue(resId, value, true);
        return value.type >= 28 && value.type <= 31;
    }

    private static TypedValue getTypedValue() {
        ThreadLocal<TypedValue> threadLocal = sTempTypedValue;
        TypedValue tv = threadLocal.get();
        if (tv == null) {
            TypedValue tv2 = new TypedValue();
            threadLocal.set(tv2);
            return tv2;
        }
        return tv;
    }

    private static TypedArray obtainAttributes(Resources res, Resources.Theme theme, AttributeSet set, int[] attrs) {
        return theme == null ? res.obtainAttributes(set, attrs) : theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

    private static int modulateColorAlpha(int color, float alphaMod) {
        int alpha = Math.round(Color.alpha(color) * alphaMod);
        return (16777215 & color) | (alpha << 24);
    }
}
