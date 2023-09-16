package android.support.v7.app;

import android.app.UiModeManager;
import android.content.Context;
import android.support.v7.app.AppCompatDelegateImplV14;
import android.view.ActionMode;
import android.view.Window;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AppCompatDelegateImplV23 extends AppCompatDelegateImplV14 {
    private final UiModeManager mUiModeManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatDelegateImplV23(Context context, Window window, AppCompatCallback appCompatCallback) {
        super(context, window, appCompatCallback);
        this.mUiModeManager = (UiModeManager) context.getSystemService("uimode");
    }

    @Override // android.support.v7.app.AppCompatDelegateImplV14, android.support.v7.app.AppCompatDelegateImplBase
    Window.Callback wrapWindowCallback(Window.Callback callback) {
        return new AppCompatWindowCallbackV23(callback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v7.app.AppCompatDelegateImplV14
    public int mapNightMode(int i) {
        if (i == 0 && this.mUiModeManager.getNightMode() == 0) {
            return -1;
        }
        return super.mapNightMode(i);
    }

    /* loaded from: classes.dex */
    class AppCompatWindowCallbackV23 extends AppCompatDelegateImplV14.AppCompatWindowCallbackV14 {
        @Override // android.support.v7.app.AppCompatDelegateImplV14.AppCompatWindowCallbackV14, android.support.v7.view.WindowCallbackWrapper, android.view.Window.Callback
        public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public AppCompatWindowCallbackV23(Window.Callback callback) {
            super(callback);
        }

        @Override // android.support.v7.view.WindowCallbackWrapper, android.view.Window.Callback
        public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int i) {
            if (AppCompatDelegateImplV23.this.isHandleNativeActionModesEnabled() && i == 0) {
                return startAsSupportActionMode(callback);
            }
            return super.onWindowStartingActionMode(callback, i);
        }
    }
}
