package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
/* loaded from: classes.dex */
class AnimatorUtils {
    private static final AnimatorUtilsImpl IMPL;

    AnimatorUtils() {
    }

    static {
        if (Build.VERSION.SDK_INT >= 19) {
            IMPL = new AnimatorUtilsApi19();
        } else {
            IMPL = new AnimatorUtilsApi14();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void addPauseListener(Animator animator, AnimatorListenerAdapter animatorListenerAdapter) {
        IMPL.addPauseListener(animator, animatorListenerAdapter);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void pause(Animator animator) {
        IMPL.pause(animator);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void resume(Animator animator) {
        IMPL.resume(animator);
    }
}
