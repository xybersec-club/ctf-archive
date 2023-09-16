package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import java.util.ArrayList;
/* loaded from: classes.dex */
class AnimatorUtilsApi14 implements AnimatorUtilsImpl {

    /* loaded from: classes.dex */
    interface AnimatorPauseListenerCompat {
        void onAnimationPause(Animator animator);

        void onAnimationResume(Animator animator);
    }

    @Override // android.support.transition.AnimatorUtilsImpl
    public void addPauseListener(Animator animator, AnimatorListenerAdapter animatorListenerAdapter) {
    }

    @Override // android.support.transition.AnimatorUtilsImpl
    public void pause(Animator animator) {
        ArrayList<Animator.AnimatorListener> listeners = animator.getListeners();
        if (listeners != null) {
            int size = listeners.size();
            for (int i = 0; i < size; i++) {
                Animator.AnimatorListener animatorListener = listeners.get(i);
                if (animatorListener instanceof AnimatorPauseListenerCompat) {
                    ((AnimatorPauseListenerCompat) animatorListener).onAnimationPause(animator);
                }
            }
        }
    }

    @Override // android.support.transition.AnimatorUtilsImpl
    public void resume(Animator animator) {
        ArrayList<Animator.AnimatorListener> listeners = animator.getListeners();
        if (listeners != null) {
            int size = listeners.size();
            for (int i = 0; i < size; i++) {
                Animator.AnimatorListener animatorListener = listeners.get(i);
                if (animatorListener instanceof AnimatorPauseListenerCompat) {
                    ((AnimatorPauseListenerCompat) animatorListener).onAnimationResume(animator);
                }
            }
        }
    }
}
