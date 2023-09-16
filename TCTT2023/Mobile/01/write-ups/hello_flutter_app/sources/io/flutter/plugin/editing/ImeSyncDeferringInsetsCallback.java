package io.flutter.plugin.editing;

import android.graphics.Insets;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimation;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ImeSyncDeferringInsetsCallback {
    private int deferredInsetTypes;
    private WindowInsets lastWindowInsets;
    private int overlayInsetTypes;
    private View view;
    private boolean animating = false;
    private boolean needsSave = false;
    private AnimationCallback animationCallback = new AnimationCallback();
    private InsetsListener insetsListener = new InsetsListener();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImeSyncDeferringInsetsCallback(View view, int overlayInsetTypes, int deferredInsetTypes) {
        this.overlayInsetTypes = overlayInsetTypes;
        this.deferredInsetTypes = deferredInsetTypes;
        this.view = view;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void install() {
        this.view.setWindowInsetsAnimationCallback(this.animationCallback);
        this.view.setOnApplyWindowInsetsListener(this.insetsListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void remove() {
        this.view.setWindowInsetsAnimationCallback(null);
        this.view.setOnApplyWindowInsetsListener(null);
    }

    View.OnApplyWindowInsetsListener getInsetsListener() {
        return this.insetsListener;
    }

    WindowInsetsAnimation.Callback getAnimationCallback() {
        return this.animationCallback;
    }

    /* loaded from: classes.dex */
    private class AnimationCallback extends WindowInsetsAnimation.Callback {
        AnimationCallback() {
            super(1);
        }

        @Override // android.view.WindowInsetsAnimation.Callback
        public void onPrepare(WindowInsetsAnimation animation) {
            if ((animation.getTypeMask() & ImeSyncDeferringInsetsCallback.this.deferredInsetTypes) != 0) {
                ImeSyncDeferringInsetsCallback.this.animating = true;
                ImeSyncDeferringInsetsCallback.this.needsSave = true;
            }
        }

        @Override // android.view.WindowInsetsAnimation.Callback
        public WindowInsets onProgress(WindowInsets insets, List<WindowInsetsAnimation> runningAnimations) {
            if (!ImeSyncDeferringInsetsCallback.this.animating || ImeSyncDeferringInsetsCallback.this.needsSave) {
                return insets;
            }
            boolean matching = false;
            for (WindowInsetsAnimation animation : runningAnimations) {
                if ((animation.getTypeMask() & ImeSyncDeferringInsetsCallback.this.deferredInsetTypes) != 0) {
                    matching = true;
                }
            }
            if (!matching) {
                return insets;
            }
            WindowInsets.Builder builder = new WindowInsets.Builder(ImeSyncDeferringInsetsCallback.this.lastWindowInsets);
            Insets newImeInsets = Insets.of(0, 0, 0, Math.max(insets.getInsets(ImeSyncDeferringInsetsCallback.this.deferredInsetTypes).bottom - insets.getInsets(ImeSyncDeferringInsetsCallback.this.overlayInsetTypes).bottom, 0));
            builder.setInsets(ImeSyncDeferringInsetsCallback.this.deferredInsetTypes, newImeInsets);
            ImeSyncDeferringInsetsCallback.this.view.onApplyWindowInsets(builder.build());
            return insets;
        }

        @Override // android.view.WindowInsetsAnimation.Callback
        public void onEnd(WindowInsetsAnimation animation) {
            if (ImeSyncDeferringInsetsCallback.this.animating && (animation.getTypeMask() & ImeSyncDeferringInsetsCallback.this.deferredInsetTypes) != 0) {
                ImeSyncDeferringInsetsCallback.this.animating = false;
                if (ImeSyncDeferringInsetsCallback.this.lastWindowInsets != null && ImeSyncDeferringInsetsCallback.this.view != null) {
                    ImeSyncDeferringInsetsCallback.this.view.dispatchApplyWindowInsets(ImeSyncDeferringInsetsCallback.this.lastWindowInsets);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private class InsetsListener implements View.OnApplyWindowInsetsListener {
        private InsetsListener() {
        }

        @Override // android.view.View.OnApplyWindowInsetsListener
        public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
            ImeSyncDeferringInsetsCallback.this.view = view;
            if (ImeSyncDeferringInsetsCallback.this.needsSave) {
                ImeSyncDeferringInsetsCallback.this.lastWindowInsets = windowInsets;
                ImeSyncDeferringInsetsCallback.this.needsSave = false;
            }
            if (ImeSyncDeferringInsetsCallback.this.animating) {
                return WindowInsets.CONSUMED;
            }
            return view.onApplyWindowInsets(windowInsets);
        }
    }
}
