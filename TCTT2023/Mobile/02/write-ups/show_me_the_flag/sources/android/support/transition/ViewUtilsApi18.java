package android.support.transition;

import android.view.View;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ViewUtilsApi18 extends ViewUtilsApi14 {
    @Override // android.support.transition.ViewUtilsApi14, android.support.transition.ViewUtilsImpl
    public ViewOverlayImpl getOverlay(View view) {
        return new ViewOverlayApi18(view);
    }

    @Override // android.support.transition.ViewUtilsApi14, android.support.transition.ViewUtilsImpl
    public WindowIdImpl getWindowId(View view) {
        return new WindowIdApi18(view);
    }
}
