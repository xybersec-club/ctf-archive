package androidx.core.graphics.drawable;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
/* loaded from: classes.dex */
public interface TintAwareDrawable {
    void setTint(int tint);

    void setTintList(ColorStateList tint);

    void setTintMode(PorterDuff.Mode tintMode);
}
