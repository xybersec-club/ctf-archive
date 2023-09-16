package android.support.design.widget;

import android.graphics.Outline;
/* loaded from: classes.dex */
class CircularBorderDrawableLollipop extends CircularBorderDrawable {
    @Override // android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        copyBounds(this.mRect);
        outline.setOval(this.mRect);
    }
}
