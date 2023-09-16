package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
/* loaded from: classes.dex */
interface AnimatorUtilsImpl {
    void addPauseListener(Animator animator, AnimatorListenerAdapter animatorListenerAdapter);

    void pause(Animator animator);

    void resume(Animator animator);
}
