package androidx.lifecycle;

import androidx.lifecycle.Lifecycle;
import androidx.savedstate.SavedStateRegistry;
/* loaded from: classes.dex */
final class SavedStateHandleController implements LifecycleEventObserver {
    private final SavedStateHandle mHandle;
    private boolean mIsAttached = false;
    private final String mKey;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SavedStateHandleController(String str, SavedStateHandle savedStateHandle) {
        this.mKey = str;
        this.mHandle = savedStateHandle;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAttached() {
        return this.mIsAttached;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void attachToLifecycle(SavedStateRegistry savedStateRegistry, Lifecycle lifecycle) {
        if (this.mIsAttached) {
            throw new IllegalStateException("Already attached to lifecycleOwner");
        }
        this.mIsAttached = true;
        lifecycle.addObserver(this);
        savedStateRegistry.registerSavedStateProvider(this.mKey, this.mHandle.savedStateProvider());
    }

    @Override // androidx.lifecycle.LifecycleEventObserver
    public void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            this.mIsAttached = false;
            lifecycleOwner.getLifecycle().removeObserver(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SavedStateHandle getHandle() {
        return this.mHandle;
    }
}
