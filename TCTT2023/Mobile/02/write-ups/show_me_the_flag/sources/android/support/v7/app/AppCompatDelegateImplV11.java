package android.support.v7.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AppCompatDelegateImplV11 extends AppCompatDelegateImplV9 {
    @Override // android.support.v7.app.AppCompatDelegateImplV9
    View callActivityOnCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatDelegateImplV11(Context context, Window window, AppCompatCallback appCompatCallback) {
        super(context, window, appCompatCallback);
    }

    @Override // android.support.v7.app.AppCompatDelegateImplV9, android.support.v7.app.AppCompatDelegate
    public boolean hasWindowFeature(int i) {
        return super.hasWindowFeature(i) || this.mWindow.hasFeature(i);
    }
}
