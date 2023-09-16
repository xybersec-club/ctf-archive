package androidx.core.view;
/* loaded from: classes.dex */
public interface WindowInsetsAnimationControlListenerCompat {
    void onCancelled(WindowInsetsAnimationControllerCompat controller);

    void onFinished(WindowInsetsAnimationControllerCompat controller);

    void onReady(WindowInsetsAnimationControllerCompat controller, int types);
}
