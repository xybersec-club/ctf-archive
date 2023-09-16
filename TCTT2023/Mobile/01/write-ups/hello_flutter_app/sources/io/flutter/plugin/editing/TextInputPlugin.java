package io.flutter.plugin.editing;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStructure;
import android.view.WindowInsets;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import androidx.core.view.inputmethod.EditorInfoCompat;
import io.flutter.Log;
import io.flutter.embedding.android.KeyboardManager;
import io.flutter.embedding.engine.systemchannels.TextInputChannel;
import io.flutter.plugin.editing.ListenableEditingState;
import io.flutter.plugin.platform.PlatformViewsController;
import java.util.ArrayList;
import java.util.HashMap;
/* loaded from: classes.dex */
public class TextInputPlugin implements ListenableEditingState.EditingStateWatcher {
    private static final String TAG = "TextInputPlugin";
    private final AutofillManager afm;
    private SparseArray<TextInputChannel.Configuration> autofillConfiguration;
    private TextInputChannel.Configuration configuration;
    private ImeSyncDeferringInsetsCallback imeSyncCallback;
    private InputTarget inputTarget = new InputTarget(InputTarget.Type.NO_TARGET, 0);
    private boolean isInputConnectionLocked;
    private Rect lastClientRect;
    private InputConnection lastInputConnection;
    private ListenableEditingState mEditable;
    private final InputMethodManager mImm;
    private TextInputChannel.TextEditState mLastKnownFrameworkTextEditingState;
    private boolean mRestartInputPending;
    private final View mView;
    private PlatformViewsController platformViewsController;
    private final TextInputChannel textInputChannel;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface MinMax {
        void inspect(double d, double d2);
    }

    public TextInputPlugin(View view, TextInputChannel textInputChannel, PlatformViewsController platformViewsController) {
        this.mView = view;
        this.mEditable = new ListenableEditingState(null, view);
        this.mImm = (InputMethodManager) view.getContext().getSystemService("input_method");
        if (Build.VERSION.SDK_INT >= 26) {
            this.afm = (AutofillManager) view.getContext().getSystemService(AutofillManager.class);
        } else {
            this.afm = null;
        }
        if (Build.VERSION.SDK_INT >= 30) {
            int mask = (view.getWindowSystemUiVisibility() & 2) == 0 ? 0 | WindowInsets.Type.navigationBars() : 0;
            ImeSyncDeferringInsetsCallback imeSyncDeferringInsetsCallback = new ImeSyncDeferringInsetsCallback(view, (view.getWindowSystemUiVisibility() & 4) == 0 ? mask | WindowInsets.Type.statusBars() : mask, WindowInsets.Type.ime());
            this.imeSyncCallback = imeSyncDeferringInsetsCallback;
            imeSyncDeferringInsetsCallback.install();
        }
        this.textInputChannel = textInputChannel;
        textInputChannel.setTextInputMethodHandler(new TextInputChannel.TextInputMethodHandler() { // from class: io.flutter.plugin.editing.TextInputPlugin.1
            @Override // io.flutter.embedding.engine.systemchannels.TextInputChannel.TextInputMethodHandler
            public void show() {
                TextInputPlugin textInputPlugin = TextInputPlugin.this;
                textInputPlugin.showTextInput(textInputPlugin.mView);
            }

            @Override // io.flutter.embedding.engine.systemchannels.TextInputChannel.TextInputMethodHandler
            public void hide() {
                if (TextInputPlugin.this.inputTarget.type == InputTarget.Type.PHYSICAL_DISPLAY_PLATFORM_VIEW) {
                    TextInputPlugin.this.notifyViewExited();
                    return;
                }
                TextInputPlugin textInputPlugin = TextInputPlugin.this;
                textInputPlugin.hideTextInput(textInputPlugin.mView);
            }

            @Override // io.flutter.embedding.engine.systemchannels.TextInputChannel.TextInputMethodHandler
            public void requestAutofill() {
                TextInputPlugin.this.notifyViewEntered();
            }

            @Override // io.flutter.embedding.engine.systemchannels.TextInputChannel.TextInputMethodHandler
            public void finishAutofillContext(boolean shouldSave) {
                if (Build.VERSION.SDK_INT < 26 || TextInputPlugin.this.afm == null) {
                    return;
                }
                if (shouldSave) {
                    TextInputPlugin.this.afm.commit();
                } else {
                    TextInputPlugin.this.afm.cancel();
                }
            }

            @Override // io.flutter.embedding.engine.systemchannels.TextInputChannel.TextInputMethodHandler
            public void setClient(int textInputClientId, TextInputChannel.Configuration configuration) {
                TextInputPlugin.this.setTextInputClient(textInputClientId, configuration);
            }

            @Override // io.flutter.embedding.engine.systemchannels.TextInputChannel.TextInputMethodHandler
            public void setPlatformViewClient(int platformViewId, boolean usesVirtualDisplay) {
                TextInputPlugin.this.setPlatformViewTextInputClient(platformViewId, usesVirtualDisplay);
            }

            @Override // io.flutter.embedding.engine.systemchannels.TextInputChannel.TextInputMethodHandler
            public void setEditingState(TextInputChannel.TextEditState editingState) {
                TextInputPlugin textInputPlugin = TextInputPlugin.this;
                textInputPlugin.setTextInputEditingState(textInputPlugin.mView, editingState);
            }

            @Override // io.flutter.embedding.engine.systemchannels.TextInputChannel.TextInputMethodHandler
            public void setEditableSizeAndTransform(double width, double height, double[] transform) {
                TextInputPlugin.this.saveEditableSizeAndTransform(width, height, transform);
            }

            @Override // io.flutter.embedding.engine.systemchannels.TextInputChannel.TextInputMethodHandler
            public void clearClient() {
                TextInputPlugin.this.clearTextInputClient();
            }

            @Override // io.flutter.embedding.engine.systemchannels.TextInputChannel.TextInputMethodHandler
            public void sendAppPrivateCommand(String action, Bundle data) {
                TextInputPlugin.this.sendTextInputAppPrivateCommand(action, data);
            }
        });
        textInputChannel.requestExistingInputState();
        this.platformViewsController = platformViewsController;
        platformViewsController.attachTextInputPlugin(this);
    }

    public InputMethodManager getInputMethodManager() {
        return this.mImm;
    }

    Editable getEditable() {
        return this.mEditable;
    }

    ImeSyncDeferringInsetsCallback getImeSyncCallback() {
        return this.imeSyncCallback;
    }

    public void lockPlatformViewInputConnection() {
        if (this.inputTarget.type == InputTarget.Type.VIRTUAL_DISPLAY_PLATFORM_VIEW) {
            this.isInputConnectionLocked = true;
        }
    }

    public void unlockPlatformViewInputConnection() {
        if (this.inputTarget.type == InputTarget.Type.VIRTUAL_DISPLAY_PLATFORM_VIEW) {
            this.isInputConnectionLocked = false;
        }
    }

    public void destroy() {
        this.platformViewsController.detachTextInputPlugin();
        this.textInputChannel.setTextInputMethodHandler(null);
        notifyViewExited();
        this.mEditable.removeEditingStateListener(this);
        ImeSyncDeferringInsetsCallback imeSyncDeferringInsetsCallback = this.imeSyncCallback;
        if (imeSyncDeferringInsetsCallback != null) {
            imeSyncDeferringInsetsCallback.remove();
        }
    }

    private static int inputTypeFromTextInputType(TextInputChannel.InputType type, boolean obscureText, boolean autocorrect, boolean enableSuggestions, boolean enableIMEPersonalizedLearning, TextInputChannel.TextCapitalization textCapitalization) {
        if (type.type == TextInputChannel.TextInputType.DATETIME) {
            return 4;
        }
        if (type.type == TextInputChannel.TextInputType.NUMBER) {
            int textType = 2;
            if (type.isSigned) {
                textType = 2 | 4096;
            }
            if (type.isDecimal) {
                return textType | 8192;
            }
            return textType;
        } else if (type.type == TextInputChannel.TextInputType.PHONE) {
            return 3;
        } else {
            if (type.type == TextInputChannel.TextInputType.NONE) {
                return 0;
            }
            int textType2 = 1;
            if (type.type == TextInputChannel.TextInputType.MULTILINE) {
                textType2 = 1 | 131072;
            } else if (type.type == TextInputChannel.TextInputType.EMAIL_ADDRESS) {
                textType2 = 1 | 32;
            } else if (type.type == TextInputChannel.TextInputType.URL) {
                textType2 = 1 | 16;
            } else if (type.type == TextInputChannel.TextInputType.VISIBLE_PASSWORD) {
                textType2 = 1 | 144;
            } else if (type.type == TextInputChannel.TextInputType.NAME) {
                textType2 = 1 | 96;
            } else if (type.type == TextInputChannel.TextInputType.POSTAL_ADDRESS) {
                textType2 = 1 | 112;
            }
            if (obscureText) {
                textType2 = textType2 | 524288 | 128;
            } else {
                if (autocorrect) {
                    textType2 |= 32768;
                }
                if (!enableSuggestions) {
                    textType2 |= 524288;
                }
            }
            if (textCapitalization == TextInputChannel.TextCapitalization.CHARACTERS) {
                return textType2 | 4096;
            }
            if (textCapitalization == TextInputChannel.TextCapitalization.WORDS) {
                return textType2 | 8192;
            }
            if (textCapitalization == TextInputChannel.TextCapitalization.SENTENCES) {
                return textType2 | 16384;
            }
            return textType2;
        }
    }

    public InputConnection createInputConnection(View view, KeyboardManager keyboardManager, EditorInfo outAttrs) {
        int enterAction;
        if (this.inputTarget.type == InputTarget.Type.NO_TARGET) {
            this.lastInputConnection = null;
            return null;
        } else if (this.inputTarget.type == InputTarget.Type.PHYSICAL_DISPLAY_PLATFORM_VIEW) {
            return null;
        } else {
            if (this.inputTarget.type == InputTarget.Type.VIRTUAL_DISPLAY_PLATFORM_VIEW) {
                if (this.isInputConnectionLocked) {
                    return this.lastInputConnection;
                }
                InputConnection onCreateInputConnection = this.platformViewsController.getPlatformViewById(this.inputTarget.id).onCreateInputConnection(outAttrs);
                this.lastInputConnection = onCreateInputConnection;
                return onCreateInputConnection;
            }
            outAttrs.inputType = inputTypeFromTextInputType(this.configuration.inputType, this.configuration.obscureText, this.configuration.autocorrect, this.configuration.enableSuggestions, this.configuration.enableIMEPersonalizedLearning, this.configuration.textCapitalization);
            outAttrs.imeOptions = 33554432;
            if (Build.VERSION.SDK_INT >= 26 && !this.configuration.enableIMEPersonalizedLearning) {
                outAttrs.imeOptions |= 16777216;
            }
            if (this.configuration.inputAction == null) {
                if ((131072 & outAttrs.inputType) != 0) {
                    enterAction = 1;
                } else {
                    enterAction = 6;
                }
            } else {
                enterAction = this.configuration.inputAction.intValue();
            }
            if (this.configuration.actionLabel != null) {
                outAttrs.actionLabel = this.configuration.actionLabel;
                outAttrs.actionId = enterAction;
            }
            outAttrs.imeOptions |= enterAction;
            if (this.configuration.contentCommitMimeTypes != null) {
                String[] imgTypeString = this.configuration.contentCommitMimeTypes;
                EditorInfoCompat.setContentMimeTypes(outAttrs, imgTypeString);
            }
            InputConnectionAdaptor connection = new InputConnectionAdaptor(view, this.inputTarget.id, this.textInputChannel, keyboardManager, this.mEditable, outAttrs);
            outAttrs.initialSelStart = this.mEditable.getSelectionStart();
            outAttrs.initialSelEnd = this.mEditable.getSelectionEnd();
            this.lastInputConnection = connection;
            return connection;
        }
    }

    public InputConnection getLastInputConnection() {
        return this.lastInputConnection;
    }

    public void clearPlatformViewClient(int platformViewId) {
        if ((this.inputTarget.type == InputTarget.Type.VIRTUAL_DISPLAY_PLATFORM_VIEW || this.inputTarget.type == InputTarget.Type.PHYSICAL_DISPLAY_PLATFORM_VIEW) && this.inputTarget.id == platformViewId) {
            this.inputTarget = new InputTarget(InputTarget.Type.NO_TARGET, 0);
            notifyViewExited();
            this.mImm.hideSoftInputFromWindow(this.mView.getApplicationWindowToken(), 0);
            this.mImm.restartInput(this.mView);
            this.mRestartInputPending = false;
        }
    }

    public void sendTextInputAppPrivateCommand(String action, Bundle data) {
        this.mImm.sendAppPrivateCommand(this.mView, action, data);
    }

    private boolean canShowTextInput() {
        TextInputChannel.Configuration configuration = this.configuration;
        return configuration == null || configuration.inputType == null || this.configuration.inputType.type != TextInputChannel.TextInputType.NONE;
    }

    void showTextInput(View view) {
        if (canShowTextInput()) {
            view.requestFocus();
            this.mImm.showSoftInput(view, 0);
            return;
        }
        hideTextInput(view);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideTextInput(View view) {
        notifyViewExited();
        this.mImm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    void setTextInputClient(int client, TextInputChannel.Configuration configuration) {
        notifyViewExited();
        this.configuration = configuration;
        if (canShowTextInput()) {
            this.inputTarget = new InputTarget(InputTarget.Type.FRAMEWORK_CLIENT, client);
        } else {
            this.inputTarget = new InputTarget(InputTarget.Type.NO_TARGET, client);
        }
        this.mEditable.removeEditingStateListener(this);
        this.mEditable = new ListenableEditingState(configuration.autofill != null ? configuration.autofill.editState : null, this.mView);
        updateAutofillConfigurationIfNeeded(configuration);
        this.mRestartInputPending = true;
        unlockPlatformViewInputConnection();
        this.lastClientRect = null;
        this.mEditable.addEditingStateListener(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPlatformViewTextInputClient(int platformViewId, boolean usesVirtualDisplay) {
        if (usesVirtualDisplay) {
            this.mView.requestFocus();
            this.inputTarget = new InputTarget(InputTarget.Type.VIRTUAL_DISPLAY_PLATFORM_VIEW, platformViewId);
            this.mImm.restartInput(this.mView);
            this.mRestartInputPending = false;
            return;
        }
        this.inputTarget = new InputTarget(InputTarget.Type.PHYSICAL_DISPLAY_PLATFORM_VIEW, platformViewId);
        this.lastInputConnection = null;
    }

    private static boolean composingChanged(TextInputChannel.TextEditState before, TextInputChannel.TextEditState after) {
        int composingRegionLength = before.composingEnd - before.composingStart;
        if (composingRegionLength != after.composingEnd - after.composingStart) {
            return true;
        }
        for (int index = 0; index < composingRegionLength; index++) {
            if (before.text.charAt(before.composingStart + index) != after.text.charAt(after.composingStart + index)) {
                return true;
            }
        }
        return false;
    }

    void setTextInputEditingState(View view, TextInputChannel.TextEditState state) {
        TextInputChannel.TextEditState textEditState;
        if (!this.mRestartInputPending && (textEditState = this.mLastKnownFrameworkTextEditingState) != null && textEditState.hasComposing()) {
            boolean composingChanged = composingChanged(this.mLastKnownFrameworkTextEditingState, state);
            this.mRestartInputPending = composingChanged;
            if (composingChanged) {
                Log.i(TAG, "Composing region changed by the framework. Restarting the input method.");
            }
        }
        this.mLastKnownFrameworkTextEditingState = state;
        this.mEditable.setEditingState(state);
        if (this.mRestartInputPending) {
            this.mImm.restartInput(view);
            this.mRestartInputPending = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveEditableSizeAndTransform(double width, double height, final double[] matrix) {
        final double[] minMax = new double[4];
        final boolean isAffine = matrix[3] == 0.0d && matrix[7] == 0.0d && matrix[15] == 1.0d;
        double d = matrix[12] / matrix[15];
        minMax[1] = d;
        minMax[0] = d;
        double d2 = matrix[13] / matrix[15];
        minMax[3] = d2;
        minMax[2] = d2;
        MinMax finder = new MinMax() { // from class: io.flutter.plugin.editing.TextInputPlugin.2
            @Override // io.flutter.plugin.editing.TextInputPlugin.MinMax
            public void inspect(double x, double y) {
                double d3 = 1.0d;
                if (!isAffine) {
                    double[] dArr = matrix;
                    d3 = 1.0d / (((dArr[3] * x) + (dArr[7] * y)) + dArr[15]);
                }
                double w = d3;
                double[] dArr2 = matrix;
                double tx = ((dArr2[0] * x) + (dArr2[4] * y) + dArr2[12]) * w;
                double ty = ((dArr2[1] * x) + (dArr2[5] * y) + dArr2[13]) * w;
                double[] dArr3 = minMax;
                if (tx < dArr3[0]) {
                    dArr3[0] = tx;
                } else if (tx > dArr3[1]) {
                    dArr3[1] = tx;
                }
                if (ty < dArr3[2]) {
                    dArr3[2] = ty;
                } else if (ty > dArr3[3]) {
                    dArr3[3] = ty;
                }
            }
        };
        finder.inspect(width, 0.0d);
        finder.inspect(width, height);
        finder.inspect(0.0d, height);
        Float density = Float.valueOf(this.mView.getContext().getResources().getDisplayMetrics().density);
        double d3 = minMax[0];
        double floatValue = density.floatValue();
        Double.isNaN(floatValue);
        double d4 = minMax[2];
        double floatValue2 = density.floatValue();
        Double.isNaN(floatValue2);
        double d5 = minMax[1];
        double floatValue3 = density.floatValue();
        Double.isNaN(floatValue3);
        double d6 = minMax[3];
        double floatValue4 = density.floatValue();
        Double.isNaN(floatValue4);
        this.lastClientRect = new Rect((int) (floatValue * d3), (int) (d4 * floatValue2), (int) Math.ceil(d5 * floatValue3), (int) Math.ceil(d6 * floatValue4));
    }

    void clearTextInputClient() {
        if (this.inputTarget.type == InputTarget.Type.VIRTUAL_DISPLAY_PLATFORM_VIEW) {
            return;
        }
        this.mEditable.removeEditingStateListener(this);
        notifyViewExited();
        this.configuration = null;
        updateAutofillConfigurationIfNeeded(null);
        this.inputTarget = new InputTarget(InputTarget.Type.NO_TARGET, 0);
        unlockPlatformViewInputConnection();
        this.lastClientRect = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InputTarget {
        int id;
        Type type;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public enum Type {
            NO_TARGET,
            FRAMEWORK_CLIENT,
            VIRTUAL_DISPLAY_PLATFORM_VIEW,
            PHYSICAL_DISPLAY_PLATFORM_VIEW
        }

        public InputTarget(Type type, int id) {
            this.type = type;
            this.id = id;
        }
    }

    public boolean handleKeyEvent(KeyEvent keyEvent) {
        InputConnection inputConnection;
        if (!getInputMethodManager().isAcceptingText() || (inputConnection = this.lastInputConnection) == null) {
            return false;
        }
        if (inputConnection instanceof InputConnectionAdaptor) {
            return ((InputConnectionAdaptor) inputConnection).handleKeyEvent(keyEvent);
        }
        return inputConnection.sendKeyEvent(keyEvent);
    }

    @Override // io.flutter.plugin.editing.ListenableEditingState.EditingStateWatcher
    public void didChangeEditingState(boolean textChanged, boolean selectionChanged, boolean composingRegionChanged) {
        if (textChanged) {
            notifyValueChanged(this.mEditable.toString());
        }
        int selectionStart = this.mEditable.getSelectionStart();
        int selectionEnd = this.mEditable.getSelectionEnd();
        int composingStart = this.mEditable.getComposingStart();
        int composingEnd = this.mEditable.getComposingEnd();
        ArrayList<TextEditingDelta> batchTextEditingDeltas = this.mEditable.extractBatchTextEditingDeltas();
        boolean skipFrameworkUpdate = this.mLastKnownFrameworkTextEditingState == null || (this.mEditable.toString().equals(this.mLastKnownFrameworkTextEditingState.text) && selectionStart == this.mLastKnownFrameworkTextEditingState.selectionStart && selectionEnd == this.mLastKnownFrameworkTextEditingState.selectionEnd && composingStart == this.mLastKnownFrameworkTextEditingState.composingStart && composingEnd == this.mLastKnownFrameworkTextEditingState.composingEnd);
        if (!skipFrameworkUpdate) {
            Log.v(TAG, "send EditingState to flutter: " + this.mEditable.toString());
            if (this.configuration.enableDeltaModel) {
                this.textInputChannel.updateEditingStateWithDeltas(this.inputTarget.id, batchTextEditingDeltas);
                this.mEditable.clearBatchDeltas();
            } else {
                this.textInputChannel.updateEditingState(this.inputTarget.id, this.mEditable.toString(), selectionStart, selectionEnd, composingStart, composingEnd);
            }
            this.mLastKnownFrameworkTextEditingState = new TextInputChannel.TextEditState(this.mEditable.toString(), selectionStart, selectionEnd, composingStart, composingEnd);
            return;
        }
        this.mEditable.clearBatchDeltas();
    }

    private boolean needsAutofill() {
        return this.autofillConfiguration != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyViewEntered() {
        if (Build.VERSION.SDK_INT < 26 || this.afm == null || !needsAutofill()) {
            return;
        }
        String triggerIdentifier = this.configuration.autofill.uniqueIdentifier;
        int[] offset = new int[2];
        this.mView.getLocationOnScreen(offset);
        Rect rect = new Rect(this.lastClientRect);
        rect.offset(offset[0], offset[1]);
        this.afm.notifyViewEntered(this.mView, triggerIdentifier.hashCode(), rect);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyViewExited() {
        TextInputChannel.Configuration configuration;
        if (Build.VERSION.SDK_INT < 26 || this.afm == null || (configuration = this.configuration) == null || configuration.autofill == null || !needsAutofill()) {
            return;
        }
        String triggerIdentifier = this.configuration.autofill.uniqueIdentifier;
        this.afm.notifyViewExited(this.mView, triggerIdentifier.hashCode());
    }

    private void notifyValueChanged(String newValue) {
        if (Build.VERSION.SDK_INT < 26 || this.afm == null || !needsAutofill()) {
            return;
        }
        String triggerIdentifier = this.configuration.autofill.uniqueIdentifier;
        this.afm.notifyValueChanged(this.mView, triggerIdentifier.hashCode(), AutofillValue.forText(newValue));
    }

    private void updateAutofillConfigurationIfNeeded(TextInputChannel.Configuration configuration) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        if (configuration == null || configuration.autofill == null) {
            this.autofillConfiguration = null;
            return;
        }
        TextInputChannel.Configuration[] configurations = configuration.fields;
        SparseArray<TextInputChannel.Configuration> sparseArray = new SparseArray<>();
        this.autofillConfiguration = sparseArray;
        if (configurations == null) {
            sparseArray.put(configuration.autofill.uniqueIdentifier.hashCode(), configuration);
            return;
        }
        for (TextInputChannel.Configuration config : configurations) {
            TextInputChannel.Configuration.Autofill autofill = config.autofill;
            if (autofill != null) {
                this.autofillConfiguration.put(autofill.uniqueIdentifier.hashCode(), config);
                this.afm.notifyValueChanged(this.mView, autofill.uniqueIdentifier.hashCode(), AutofillValue.forText(autofill.editState.text));
            }
        }
    }

    public void onProvideAutofillVirtualStructure(ViewStructure structure, int flags) {
        Rect rect;
        ViewStructure viewStructure = structure;
        if (Build.VERSION.SDK_INT < 26 || !needsAutofill()) {
            return;
        }
        String triggerIdentifier = this.configuration.autofill.uniqueIdentifier;
        AutofillId parentId = structure.getAutofillId();
        int i = 0;
        while (i < this.autofillConfiguration.size()) {
            int autofillId = this.autofillConfiguration.keyAt(i);
            TextInputChannel.Configuration config = this.autofillConfiguration.valueAt(i);
            TextInputChannel.Configuration.Autofill autofill = config.autofill;
            if (autofill != null) {
                viewStructure.addChildCount(1);
                ViewStructure child = viewStructure.newChild(i);
                child.setAutofillId(parentId, autofillId);
                if (autofill.hints.length > 0) {
                    child.setAutofillHints(autofill.hints);
                }
                child.setAutofillType(1);
                child.setVisibility(0);
                if (autofill.hintText != null) {
                    child.setHint(autofill.hintText);
                }
                if (triggerIdentifier.hashCode() != autofillId || (rect = this.lastClientRect) == null) {
                    child.setDimens(0, 0, 0, 0, 1, 1);
                    child.setAutofillValue(AutofillValue.forText(autofill.editState.text));
                } else {
                    child.setDimens(rect.left, this.lastClientRect.top, 0, 0, this.lastClientRect.width(), this.lastClientRect.height());
                    child.setAutofillValue(AutofillValue.forText(this.mEditable));
                }
            }
            i++;
            viewStructure = structure;
        }
    }

    public void autofill(SparseArray<AutofillValue> values) {
        TextInputChannel.Configuration configuration;
        if (Build.VERSION.SDK_INT < 26 || (configuration = this.configuration) == null || this.autofillConfiguration == null || configuration.autofill == null) {
            return;
        }
        TextInputChannel.Configuration.Autofill currentAutofill = this.configuration.autofill;
        HashMap<String, TextInputChannel.TextEditState> editingValues = new HashMap<>();
        for (int i = 0; i < values.size(); i++) {
            int virtualId = values.keyAt(i);
            TextInputChannel.Configuration config = this.autofillConfiguration.get(virtualId);
            if (config != null && config.autofill != null) {
                TextInputChannel.Configuration.Autofill autofill = config.autofill;
                String value = values.valueAt(i).getTextValue().toString();
                TextInputChannel.TextEditState newState = new TextInputChannel.TextEditState(value, value.length(), value.length(), -1, -1);
                if (autofill.uniqueIdentifier.equals(currentAutofill.uniqueIdentifier)) {
                    this.mEditable.setEditingState(newState);
                } else {
                    editingValues.put(autofill.uniqueIdentifier, newState);
                }
            }
        }
        this.textInputChannel.updateEditingStateWithTag(this.inputTarget.id, editingValues);
    }
}
