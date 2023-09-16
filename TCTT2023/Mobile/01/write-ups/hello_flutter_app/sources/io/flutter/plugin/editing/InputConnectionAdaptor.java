package io.flutter.plugin.editing;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.DynamicLayout;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.InputMethodManager;
import io.flutter.Log;
import io.flutter.embedding.engine.FlutterJNI;
import io.flutter.embedding.engine.systemchannels.TextInputChannel;
import io.flutter.plugin.editing.ListenableEditingState;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import kotlinx.coroutines.DebugKt;
/* loaded from: classes.dex */
public class InputConnectionAdaptor extends BaseInputConnection implements ListenableEditingState.EditingStateWatcher {
    private static final String TAG = "InputConnectionAdaptor";
    private int batchEditNestDepth;
    private FlutterTextUtils flutterTextUtils;
    private final KeyboardDelegate keyboardDelegate;
    private final int mClient;
    private CursorAnchorInfo.Builder mCursorAnchorInfoBuilder;
    private final ListenableEditingState mEditable;
    private final EditorInfo mEditorInfo;
    private ExtractedTextRequest mExtractRequest;
    private ExtractedText mExtractedText;
    private final View mFlutterView;
    private InputMethodManager mImm;
    private final Layout mLayout;
    private boolean mMonitorCursorUpdate;
    private final TextInputChannel textInputChannel;

    /* loaded from: classes.dex */
    public interface KeyboardDelegate {
        boolean handleEvent(KeyEvent keyEvent);
    }

    public InputConnectionAdaptor(View view, int client, TextInputChannel textInputChannel, KeyboardDelegate keyboardDelegate, ListenableEditingState editable, EditorInfo editorInfo, FlutterJNI flutterJNI) {
        super(view, true);
        this.mMonitorCursorUpdate = false;
        this.mExtractedText = new ExtractedText();
        this.batchEditNestDepth = 0;
        this.mFlutterView = view;
        this.mClient = client;
        this.textInputChannel = textInputChannel;
        this.mEditable = editable;
        editable.addEditingStateListener(this);
        this.mEditorInfo = editorInfo;
        this.keyboardDelegate = keyboardDelegate;
        this.flutterTextUtils = new FlutterTextUtils(flutterJNI);
        this.mLayout = new DynamicLayout(editable, new TextPaint(), Integer.MAX_VALUE, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.mImm = (InputMethodManager) view.getContext().getSystemService("input_method");
    }

    public InputConnectionAdaptor(View view, int client, TextInputChannel textInputChannel, KeyboardDelegate keyboardDelegate, ListenableEditingState editable, EditorInfo editorInfo) {
        this(view, client, textInputChannel, keyboardDelegate, editable, editorInfo, new FlutterJNI());
    }

    private ExtractedText getExtractedText(ExtractedTextRequest request) {
        CharSequence listenableEditingState;
        this.mExtractedText.startOffset = 0;
        this.mExtractedText.partialStartOffset = -1;
        this.mExtractedText.partialEndOffset = -1;
        this.mExtractedText.selectionStart = this.mEditable.getSelectionStart();
        this.mExtractedText.selectionEnd = this.mEditable.getSelectionEnd();
        ExtractedText extractedText = this.mExtractedText;
        if (request == null || (request.flags & 1) == 0) {
            listenableEditingState = this.mEditable.toString();
        } else {
            listenableEditingState = this.mEditable;
        }
        extractedText.text = listenableEditingState;
        return this.mExtractedText;
    }

    private CursorAnchorInfo getCursorAnchorInfo() {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        CursorAnchorInfo.Builder builder = this.mCursorAnchorInfoBuilder;
        if (builder == null) {
            this.mCursorAnchorInfoBuilder = new CursorAnchorInfo.Builder();
        } else {
            builder.reset();
        }
        this.mCursorAnchorInfoBuilder.setSelectionRange(this.mEditable.getSelectionStart(), this.mEditable.getSelectionEnd());
        int composingStart = this.mEditable.getComposingStart();
        int composingEnd = this.mEditable.getComposingEnd();
        if (composingStart >= 0 && composingEnd > composingStart) {
            this.mCursorAnchorInfoBuilder.setComposingText(composingStart, this.mEditable.toString().subSequence(composingStart, composingEnd));
        } else {
            this.mCursorAnchorInfoBuilder.setComposingText(-1, "");
        }
        return this.mCursorAnchorInfoBuilder.build();
    }

    @Override // android.view.inputmethod.BaseInputConnection
    public Editable getEditable() {
        return this.mEditable;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean beginBatchEdit() {
        this.mEditable.beginBatchEdit();
        this.batchEditNestDepth++;
        return super.beginBatchEdit();
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean endBatchEdit() {
        boolean result = super.endBatchEdit();
        this.batchEditNestDepth--;
        this.mEditable.endBatchEdit();
        return result;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean commitText(CharSequence text, int newCursorPosition) {
        boolean result = super.commitText(text, newCursorPosition);
        return result;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        if (this.mEditable.getSelectionStart() == -1) {
            return true;
        }
        boolean result = super.deleteSurroundingText(beforeLength, afterLength);
        return result;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean deleteSurroundingTextInCodePoints(int beforeLength, int afterLength) {
        boolean result = super.deleteSurroundingTextInCodePoints(beforeLength, afterLength);
        return result;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean setComposingRegion(int start, int end) {
        boolean result = super.setComposingRegion(start, end);
        return result;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        boolean result;
        beginBatchEdit();
        if (text.length() == 0) {
            result = super.commitText(text, newCursorPosition);
        } else {
            result = super.setComposingText(text, newCursorPosition);
        }
        endBatchEdit();
        return result;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean finishComposingText() {
        boolean result = super.finishComposingText();
        return result;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
        boolean textMonitor = (flags & 1) != 0;
        if (textMonitor == (this.mExtractRequest == null)) {
            Log.d(TAG, "The input method toggled text monitoring " + (textMonitor ? DebugKt.DEBUG_PROPERTY_VALUE_ON : DebugKt.DEBUG_PROPERTY_VALUE_OFF));
        }
        this.mExtractRequest = textMonitor ? request : null;
        return getExtractedText(request);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean requestCursorUpdates(int cursorUpdateMode) {
        if (Build.VERSION.SDK_INT < 21) {
            return false;
        }
        if ((cursorUpdateMode & 1) != 0) {
            this.mImm.updateCursorAnchorInfo(this.mFlutterView, getCursorAnchorInfo());
        }
        boolean updated = (cursorUpdateMode & 2) != 0;
        if (updated != this.mMonitorCursorUpdate) {
            Log.d(TAG, "The input method toggled cursor monitoring " + (updated ? DebugKt.DEBUG_PROPERTY_VALUE_ON : DebugKt.DEBUG_PROPERTY_VALUE_OFF));
        }
        this.mMonitorCursorUpdate = updated;
        return true;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean clearMetaKeyStates(int states) {
        boolean result = super.clearMetaKeyStates(states);
        return result;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public void closeConnection() {
        super.closeConnection();
        this.mEditable.removeEditingStateListener(this);
        while (this.batchEditNestDepth > 0) {
            endBatchEdit();
            this.batchEditNestDepth--;
        }
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean setSelection(int start, int end) {
        beginBatchEdit();
        boolean result = super.setSelection(start, end);
        endBatchEdit();
        return result;
    }

    private static int clampIndexToEditable(int index, Editable editable) {
        int clamped = Math.max(0, Math.min(editable.length(), index));
        if (clamped != index) {
            Log.d("flutter", "Text selection index was clamped (" + index + "->" + clamped + ") to remain in bounds. This may not be your fault, as some keyboards may select outside of bounds.");
        }
        return clamped;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean sendKeyEvent(KeyEvent event) {
        return this.keyboardDelegate.handleEvent(event);
    }

    public boolean handleKeyEvent(KeyEvent event) {
        if (event.getAction() == 0) {
            if (event.getKeyCode() == 21) {
                return handleHorizontalMovement(true, event.isShiftPressed());
            }
            if (event.getKeyCode() == 22) {
                return handleHorizontalMovement(false, event.isShiftPressed());
            }
            if (event.getKeyCode() == 19) {
                return handleVerticalMovement(true, event.isShiftPressed());
            }
            if (event.getKeyCode() == 20) {
                return handleVerticalMovement(false, event.isShiftPressed());
            }
            if ((event.getKeyCode() == 66 || event.getKeyCode() == 160) && (131072 & this.mEditorInfo.inputType) == 0) {
                performEditorAction(this.mEditorInfo.imeOptions & 255);
                return true;
            }
            int selStart = Selection.getSelectionStart(this.mEditable);
            int selEnd = Selection.getSelectionEnd(this.mEditable);
            int character = event.getUnicodeChar();
            if (selStart < 0 || selEnd < 0 || character == 0) {
                return false;
            }
            int selMin = Math.min(selStart, selEnd);
            int selMax = Math.max(selStart, selEnd);
            beginBatchEdit();
            if (selMin != selMax) {
                this.mEditable.delete(selMin, selMax);
            }
            this.mEditable.insert(selMin, (CharSequence) String.valueOf((char) character));
            setSelection(selMin + 1, selMin + 1);
            endBatchEdit();
            return true;
        } else if (event.getAction() == 1 && (event.getKeyCode() == 59 || event.getKeyCode() == 60)) {
            int selEnd2 = Selection.getSelectionEnd(this.mEditable);
            setSelection(selEnd2, selEnd2);
            return true;
        } else {
            return false;
        }
    }

    private boolean handleHorizontalMovement(boolean isLeft, boolean isShiftPressed) {
        int newSelectionEnd;
        int selStart = Selection.getSelectionStart(this.mEditable);
        int selEnd = Selection.getSelectionEnd(this.mEditable);
        boolean shouldCollapse = false;
        if (selStart < 0 || selEnd < 0) {
            return false;
        }
        if (isLeft) {
            newSelectionEnd = Math.max(this.flutterTextUtils.getOffsetBefore(this.mEditable, selEnd), 0);
        } else {
            newSelectionEnd = Math.min(this.flutterTextUtils.getOffsetAfter(this.mEditable, selEnd), this.mEditable.length());
        }
        if (selStart == selEnd && !isShiftPressed) {
            shouldCollapse = true;
        }
        if (shouldCollapse) {
            setSelection(newSelectionEnd, newSelectionEnd);
        } else {
            setSelection(selStart, newSelectionEnd);
        }
        return true;
    }

    private boolean handleVerticalMovement(boolean isUp, boolean isShiftPressed) {
        int selStart = Selection.getSelectionStart(this.mEditable);
        int selEnd = Selection.getSelectionEnd(this.mEditable);
        boolean shouldCollapse = false;
        if (selStart < 0 || selEnd < 0) {
            return false;
        }
        if (selStart == selEnd && !isShiftPressed) {
            shouldCollapse = true;
        }
        beginBatchEdit();
        if (shouldCollapse) {
            if (isUp) {
                Selection.moveUp(this.mEditable, this.mLayout);
            } else {
                Selection.moveDown(this.mEditable, this.mLayout);
            }
            int newSelection = Selection.getSelectionStart(this.mEditable);
            setSelection(newSelection, newSelection);
        } else {
            if (isUp) {
                Selection.extendUp(this.mEditable, this.mLayout);
            } else {
                Selection.extendDown(this.mEditable, this.mLayout);
            }
            setSelection(Selection.getSelectionStart(this.mEditable), Selection.getSelectionEnd(this.mEditable));
        }
        endBatchEdit();
        return true;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean performContextMenuAction(int id) {
        beginBatchEdit();
        boolean result = doPerformContextMenuAction(id);
        endBatchEdit();
        return result;
    }

    private boolean doPerformContextMenuAction(int id) {
        if (id == 16908319) {
            setSelection(0, this.mEditable.length());
            return true;
        } else if (id == 16908320) {
            int selStart = Selection.getSelectionStart(this.mEditable);
            int selEnd = Selection.getSelectionEnd(this.mEditable);
            if (selStart != selEnd) {
                int selMin = Math.min(selStart, selEnd);
                int selMax = Math.max(selStart, selEnd);
                CharSequence textToCut = this.mEditable.subSequence(selMin, selMax);
                ClipboardManager clipboard = (ClipboardManager) this.mFlutterView.getContext().getSystemService("clipboard");
                clipboard.setPrimaryClip(ClipData.newPlainText("text label?", textToCut));
                this.mEditable.delete(selMin, selMax);
                setSelection(selMin, selMin);
            }
            return true;
        } else if (id == 16908321) {
            int selStart2 = Selection.getSelectionStart(this.mEditable);
            int selEnd2 = Selection.getSelectionEnd(this.mEditable);
            if (selStart2 != selEnd2) {
                CharSequence textToCopy = this.mEditable.subSequence(Math.min(selStart2, selEnd2), Math.max(selStart2, selEnd2));
                ClipboardManager clipboard2 = (ClipboardManager) this.mFlutterView.getContext().getSystemService("clipboard");
                clipboard2.setPrimaryClip(ClipData.newPlainText("text label?", textToCopy));
            }
            return true;
        } else if (id == 16908322) {
            ClipboardManager clipboard3 = (ClipboardManager) this.mFlutterView.getContext().getSystemService("clipboard");
            ClipData clip = clipboard3.getPrimaryClip();
            if (clip != null) {
                CharSequence textToPaste = clip.getItemAt(0).coerceToText(this.mFlutterView.getContext());
                int selStart3 = Math.max(0, Selection.getSelectionStart(this.mEditable));
                int selEnd3 = Math.max(0, Selection.getSelectionEnd(this.mEditable));
                int selMin2 = Math.min(selStart3, selEnd3);
                int selMax2 = Math.max(selStart3, selEnd3);
                if (selMin2 != selMax2) {
                    this.mEditable.delete(selMin2, selMax2);
                }
                this.mEditable.insert(selMin2, textToPaste);
                int newSelStart = textToPaste.length() + selMin2;
                setSelection(newSelStart, newSelStart);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean performPrivateCommand(String action, Bundle data) {
        this.textInputChannel.performPrivateCommand(this.mClient, action, data);
        return true;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean performEditorAction(int actionCode) {
        switch (actionCode) {
            case 0:
                this.textInputChannel.unspecifiedAction(this.mClient);
                return true;
            case 1:
                this.textInputChannel.newline(this.mClient);
                return true;
            case 2:
                this.textInputChannel.go(this.mClient);
                return true;
            case 3:
                this.textInputChannel.search(this.mClient);
                return true;
            case 4:
                this.textInputChannel.send(this.mClient);
                return true;
            case 5:
                this.textInputChannel.next(this.mClient);
                return true;
            case 6:
            default:
                this.textInputChannel.done(this.mClient);
                return true;
            case 7:
                this.textInputChannel.previous(this.mClient);
                return true;
        }
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean commitContent(InputContentInfo inputContentInfo, int flags, Bundle opts) {
        if (Build.VERSION.SDK_INT < 25 || (flags & 1) == 0) {
            return false;
        }
        try {
            inputContentInfo.requestPermission();
            if (inputContentInfo.getDescription().getMimeTypeCount() > 0) {
                inputContentInfo.requestPermission();
                Uri uri = inputContentInfo.getContentUri();
                Object mimeType = inputContentInfo.getDescription().getMimeType(0);
                Context context = this.mFlutterView.getContext();
                if (uri != null) {
                    try {
                        InputStream is = context.getContentResolver().openInputStream(uri);
                        if (is != null) {
                            Object data = readStreamFully(is, 65536);
                            Map<String, Object> obj = new HashMap<>();
                            obj.put("mimeType", mimeType);
                            obj.put("data", data);
                            obj.put("uri", uri.toString());
                            this.textInputChannel.commitContent(this.mClient, obj);
                            inputContentInfo.releasePermission();
                            return true;
                        }
                    } catch (FileNotFoundException e) {
                        inputContentInfo.releasePermission();
                        return false;
                    }
                }
                inputContentInfo.releasePermission();
            }
            return false;
        } catch (Exception e2) {
            return false;
        }
    }

    private byte[] readStreamFully(InputStream is, int blocksize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[blocksize];
        while (true) {
            int len = -1;
            try {
                len = is.read(buffer);
            } catch (IOException e) {
            }
            if (len != -1) {
                baos.write(buffer, 0, len);
            } else {
                return baos.toByteArray();
            }
        }
    }

    @Override // io.flutter.plugin.editing.ListenableEditingState.EditingStateWatcher
    public void didChangeEditingState(boolean textChanged, boolean selectionChanged, boolean composingRegionChanged) {
        this.mImm.updateSelection(this.mFlutterView, this.mEditable.getSelectionStart(), this.mEditable.getSelectionEnd(), this.mEditable.getComposingStart(), this.mEditable.getComposingEnd());
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        ExtractedTextRequest extractedTextRequest = this.mExtractRequest;
        if (extractedTextRequest != null) {
            this.mImm.updateExtractedText(this.mFlutterView, extractedTextRequest.token, getExtractedText(this.mExtractRequest));
        }
        if (this.mMonitorCursorUpdate) {
            CursorAnchorInfo info = getCursorAnchorInfo();
            this.mImm.updateCursorAnchorInfo(this.mFlutterView, info);
        }
    }
}
