package androidx.lifecycle;

import com.android.tools.r8.annotations.SynthesizedClassV2;
/* loaded from: classes.dex */
public interface DefaultLifecycleObserver extends FullLifecycleObserver {
    @Override // androidx.lifecycle.FullLifecycleObserver
    void onCreate(LifecycleOwner lifecycleOwner);

    @Override // androidx.lifecycle.FullLifecycleObserver
    void onDestroy(LifecycleOwner lifecycleOwner);

    @Override // androidx.lifecycle.FullLifecycleObserver
    void onPause(LifecycleOwner lifecycleOwner);

    @Override // androidx.lifecycle.FullLifecycleObserver
    void onResume(LifecycleOwner lifecycleOwner);

    @Override // androidx.lifecycle.FullLifecycleObserver
    void onStart(LifecycleOwner lifecycleOwner);

    @Override // androidx.lifecycle.FullLifecycleObserver
    void onStop(LifecycleOwner lifecycleOwner);

    @SynthesizedClassV2(kind = 7, versionHash = "15f1483824cf4085ddca5a8529d873fc59a8ced2cbce67fb2b3dd9033ea03442")
    /* renamed from: androidx.lifecycle.DefaultLifecycleObserver$-CC  reason: invalid class name */
    /* loaded from: classes.dex */
    public final /* synthetic */ class CC {
        public static void $default$onCreate(DefaultLifecycleObserver _this, LifecycleOwner owner) {
        }

        public static void $default$onStart(DefaultLifecycleObserver _this, LifecycleOwner owner) {
        }

        public static void $default$onResume(DefaultLifecycleObserver _this, LifecycleOwner owner) {
        }

        public static void $default$onPause(DefaultLifecycleObserver _this, LifecycleOwner owner) {
        }

        public static void $default$onStop(DefaultLifecycleObserver _this, LifecycleOwner owner) {
        }

        public static void $default$onDestroy(DefaultLifecycleObserver _this, LifecycleOwner owner) {
        }
    }
}
