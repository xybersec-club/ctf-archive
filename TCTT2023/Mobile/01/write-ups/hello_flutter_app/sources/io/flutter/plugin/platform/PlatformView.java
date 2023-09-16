package io.flutter.plugin.platform;

import android.view.View;
import com.android.tools.r8.annotations.SynthesizedClassV2;
/* loaded from: classes.dex */
public interface PlatformView {
    void dispose();

    View getView();

    void onFlutterViewAttached(View view);

    void onFlutterViewDetached();

    void onInputConnectionLocked();

    void onInputConnectionUnlocked();

    @SynthesizedClassV2(kind = 7, versionHash = "15f1483824cf4085ddca5a8529d873fc59a8ced2cbce67fb2b3dd9033ea03442")
    /* renamed from: io.flutter.plugin.platform.PlatformView$-CC  reason: invalid class name */
    /* loaded from: classes.dex */
    public final /* synthetic */ class CC {
        public static void $default$onFlutterViewAttached(PlatformView _this, View flutterView) {
        }

        public static void $default$onFlutterViewDetached(PlatformView _this) {
        }

        public static void $default$onInputConnectionLocked(PlatformView _this) {
        }

        public static void $default$onInputConnectionUnlocked(PlatformView _this) {
        }
    }
}
