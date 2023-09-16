package io.flutter.embedding.android;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.android.tools.r8.annotations.SynthesizedClassV2;
@Deprecated
/* loaded from: classes.dex */
public interface SplashScreen {
    View createSplashView(Context context, Bundle bundle);

    boolean doesSplashViewRememberItsTransition();

    Bundle saveSplashScreenState();

    void transitionToFlutter(Runnable runnable);

    @SynthesizedClassV2(kind = 7, versionHash = "15f1483824cf4085ddca5a8529d873fc59a8ced2cbce67fb2b3dd9033ea03442")
    /* renamed from: io.flutter.embedding.android.SplashScreen$-CC  reason: invalid class name */
    /* loaded from: classes.dex */
    public final /* synthetic */ class CC {
        public static boolean $default$doesSplashViewRememberItsTransition(SplashScreen _this) {
            return false;
        }

        public static Bundle $default$saveSplashScreenState(SplashScreen _this) {
            return null;
        }
    }
}
