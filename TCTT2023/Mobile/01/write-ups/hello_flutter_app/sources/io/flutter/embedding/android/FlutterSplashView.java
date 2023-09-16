package io.flutter.embedding.android;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import io.flutter.Log;
import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.renderer.FlutterUiDisplayListener;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class FlutterSplashView extends FrameLayout {
    private static String TAG = "FlutterSplashView";
    private final FlutterView.FlutterEngineAttachmentListener flutterEngineAttachmentListener;
    private final FlutterUiDisplayListener flutterUiDisplayListener;
    private FlutterView flutterView;
    private final Runnable onTransitionComplete;
    private String previousCompletedSplashIsolate;
    private SplashScreen splashScreen;
    Bundle splashScreenState;
    private View splashScreenView;
    private String transitioningIsolateId;

    public FlutterSplashView(Context context) {
        this(context, null, 0);
    }

    public FlutterSplashView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlutterSplashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.flutterEngineAttachmentListener = new FlutterView.FlutterEngineAttachmentListener() { // from class: io.flutter.embedding.android.FlutterSplashView.1
            @Override // io.flutter.embedding.android.FlutterView.FlutterEngineAttachmentListener
            public void onFlutterEngineAttachedToFlutterView(FlutterEngine engine) {
                FlutterSplashView.this.flutterView.removeFlutterEngineAttachmentListener(this);
                FlutterSplashView flutterSplashView = FlutterSplashView.this;
                flutterSplashView.displayFlutterViewWithSplash(flutterSplashView.flutterView, FlutterSplashView.this.splashScreen);
            }

            @Override // io.flutter.embedding.android.FlutterView.FlutterEngineAttachmentListener
            public void onFlutterEngineDetachedFromFlutterView() {
            }
        };
        this.flutterUiDisplayListener = new FlutterUiDisplayListener() { // from class: io.flutter.embedding.android.FlutterSplashView.2
            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiDisplayed() {
                if (FlutterSplashView.this.splashScreen != null) {
                    FlutterSplashView.this.transitionToFlutter();
                }
            }

            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiNoLongerDisplayed() {
            }
        };
        this.onTransitionComplete = new Runnable() { // from class: io.flutter.embedding.android.FlutterSplashView.3
            @Override // java.lang.Runnable
            public void run() {
                FlutterSplashView flutterSplashView = FlutterSplashView.this;
                flutterSplashView.removeView(flutterSplashView.splashScreenView);
                FlutterSplashView flutterSplashView2 = FlutterSplashView.this;
                flutterSplashView2.previousCompletedSplashIsolate = flutterSplashView2.transitioningIsolateId;
            }
        };
        setSaveEnabled(true);
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.previousCompletedSplashIsolate = this.previousCompletedSplashIsolate;
        SplashScreen splashScreen = this.splashScreen;
        savedState.splashScreenState = splashScreen != null ? splashScreen.saveSplashScreenState() : null;
        return savedState;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.previousCompletedSplashIsolate = savedState.previousCompletedSplashIsolate;
        this.splashScreenState = savedState.splashScreenState;
    }

    public void displayFlutterViewWithSplash(FlutterView flutterView, SplashScreen splashScreen) {
        FlutterView flutterView2 = this.flutterView;
        if (flutterView2 != null) {
            flutterView2.removeOnFirstFrameRenderedListener(this.flutterUiDisplayListener);
            removeView(this.flutterView);
        }
        View view = this.splashScreenView;
        if (view != null) {
            removeView(view);
        }
        this.flutterView = flutterView;
        addView(flutterView);
        this.splashScreen = splashScreen;
        if (splashScreen != null) {
            if (isSplashScreenNeededNow()) {
                Log.v(TAG, "Showing splash screen UI.");
                View createSplashView = splashScreen.createSplashView(getContext(), this.splashScreenState);
                this.splashScreenView = createSplashView;
                addView(createSplashView);
                flutterView.addOnFirstFrameRenderedListener(this.flutterUiDisplayListener);
            } else if (isSplashScreenTransitionNeededNow()) {
                Log.v(TAG, "Showing an immediate splash transition to Flutter due to previously interrupted transition.");
                View createSplashView2 = splashScreen.createSplashView(getContext(), this.splashScreenState);
                this.splashScreenView = createSplashView2;
                addView(createSplashView2);
                transitionToFlutter();
            } else if (!flutterView.isAttachedToFlutterEngine()) {
                Log.v(TAG, "FlutterView is not yet attached to a FlutterEngine. Showing nothing until a FlutterEngine is attached.");
                flutterView.addFlutterEngineAttachmentListener(this.flutterEngineAttachmentListener);
            }
        }
    }

    private boolean isSplashScreenNeededNow() {
        FlutterView flutterView = this.flutterView;
        return (flutterView == null || !flutterView.isAttachedToFlutterEngine() || this.flutterView.hasRenderedFirstFrame() || hasSplashCompleted()) ? false : true;
    }

    private boolean isSplashScreenTransitionNeededNow() {
        SplashScreen splashScreen;
        FlutterView flutterView = this.flutterView;
        return flutterView != null && flutterView.isAttachedToFlutterEngine() && (splashScreen = this.splashScreen) != null && splashScreen.doesSplashViewRememberItsTransition() && wasPreviousSplashTransitionInterrupted();
    }

    private boolean wasPreviousSplashTransitionInterrupted() {
        FlutterView flutterView = this.flutterView;
        if (flutterView == null) {
            throw new IllegalStateException("Cannot determine if previous splash transition was interrupted when no FlutterView is set.");
        }
        if (flutterView.isAttachedToFlutterEngine()) {
            return this.flutterView.hasRenderedFirstFrame() && !hasSplashCompleted();
        }
        throw new IllegalStateException("Cannot determine if previous splash transition was interrupted when no FlutterEngine is attached to our FlutterView. This question depends on an isolate ID to differentiate Flutter experiences.");
    }

    private boolean hasSplashCompleted() {
        FlutterView flutterView = this.flutterView;
        if (flutterView == null) {
            throw new IllegalStateException("Cannot determine if splash has completed when no FlutterView is set.");
        }
        if (flutterView.isAttachedToFlutterEngine()) {
            return this.flutterView.getAttachedFlutterEngine().getDartExecutor().getIsolateServiceId() != null && this.flutterView.getAttachedFlutterEngine().getDartExecutor().getIsolateServiceId().equals(this.previousCompletedSplashIsolate);
        }
        throw new IllegalStateException("Cannot determine if splash has completed when no FlutterEngine is attached to our FlutterView. This question depends on an isolate ID to differentiate Flutter experiences.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transitionToFlutter() {
        this.transitioningIsolateId = this.flutterView.getAttachedFlutterEngine().getDartExecutor().getIsolateServiceId();
        Log.v(TAG, "Transitioning splash screen to a Flutter UI. Isolate: " + this.transitioningIsolateId);
        this.splashScreen.transitionToFlutter(this.onTransitionComplete);
    }

    /* loaded from: classes.dex */
    public static class SavedState extends View.BaseSavedState {
        public static Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: io.flutter.embedding.android.FlutterSplashView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        private String previousCompletedSplashIsolate;
        private Bundle splashScreenState;

        SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel source) {
            super(source);
            this.previousCompletedSplashIsolate = source.readString();
            this.splashScreenState = source.readBundle(getClass().getClassLoader());
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(this.previousCompletedSplashIsolate);
            out.writeBundle(this.splashScreenState);
        }
    }
}
