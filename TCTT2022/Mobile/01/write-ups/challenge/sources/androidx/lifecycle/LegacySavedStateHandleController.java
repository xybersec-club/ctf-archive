package androidx.lifecycle;

import android.os.Bundle;
import androidx.lifecycle.Lifecycle;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryOwner;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class LegacySavedStateHandleController {
    static final String TAG_SAVED_STATE_HANDLE_CONTROLLER = "androidx.lifecycle.savedstate.vm.tag";

    private LegacySavedStateHandleController() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SavedStateHandleController create(SavedStateRegistry savedStateRegistry, Lifecycle lifecycle, String str, Bundle bundle) {
        SavedStateHandleController savedStateHandleController = new SavedStateHandleController(str, SavedStateHandle.createHandle(savedStateRegistry.consumeRestoredStateForKey(str), bundle));
        savedStateHandleController.attachToLifecycle(savedStateRegistry, lifecycle);
        tryToAddRecreator(savedStateRegistry, lifecycle);
        return savedStateHandleController;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class OnRecreation implements SavedStateRegistry.AutoRecreated {
        OnRecreation() {
        }

        @Override // androidx.savedstate.SavedStateRegistry.AutoRecreated
        public void onRecreated(SavedStateRegistryOwner savedStateRegistryOwner) {
            if (!(savedStateRegistryOwner instanceof ViewModelStoreOwner)) {
                throw new IllegalStateException("Internal error: OnRecreation should be registered only on components that implement ViewModelStoreOwner");
            }
            ViewModelStore viewModelStore = ((ViewModelStoreOwner) savedStateRegistryOwner).getViewModelStore();
            SavedStateRegistry savedStateRegistry = savedStateRegistryOwner.getSavedStateRegistry();
            for (String str : viewModelStore.keys()) {
                LegacySavedStateHandleController.attachHandleIfNeeded(viewModelStore.get(str), savedStateRegistry, savedStateRegistryOwner.getLifecycle());
            }
            if (viewModelStore.keys().isEmpty()) {
                return;
            }
            savedStateRegistry.runOnNextRecreation(OnRecreation.class);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void attachHandleIfNeeded(ViewModel viewModel, SavedStateRegistry savedStateRegistry, Lifecycle lifecycle) {
        SavedStateHandleController savedStateHandleController = (SavedStateHandleController) viewModel.getTag(TAG_SAVED_STATE_HANDLE_CONTROLLER);
        if (savedStateHandleController == null || savedStateHandleController.isAttached()) {
            return;
        }
        savedStateHandleController.attachToLifecycle(savedStateRegistry, lifecycle);
        tryToAddRecreator(savedStateRegistry, lifecycle);
    }

    private static void tryToAddRecreator(final SavedStateRegistry savedStateRegistry, final Lifecycle lifecycle) {
        Lifecycle.State currentState = lifecycle.getCurrentState();
        if (currentState == Lifecycle.State.INITIALIZED || currentState.isAtLeast(Lifecycle.State.STARTED)) {
            savedStateRegistry.runOnNextRecreation(OnRecreation.class);
        } else {
            lifecycle.addObserver(new LifecycleEventObserver() { // from class: androidx.lifecycle.LegacySavedStateHandleController.1
                @Override // androidx.lifecycle.LifecycleEventObserver
                public void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_START) {
                        Lifecycle.this.removeObserver(this);
                        savedStateRegistry.runOnNextRecreation(OnRecreation.class);
                    }
                }
            });
        }
    }
}
