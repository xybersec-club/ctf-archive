package io.flutter.plugin.editing;

import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import io.flutter.Log;
import io.flutter.embedding.engine.systemchannels.TextInputChannel;
import java.util.ArrayList;
import java.util.Iterator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ListenableEditingState extends SpannableStringBuilder {
    private static final String TAG = "ListenableEditingState";
    private int mComposingEndWhenBeginBatchEdit;
    private int mComposingStartWhenBeginBatchEdit;
    private BaseInputConnection mDummyConnection;
    private int mSelectionEndWhenBeginBatchEdit;
    private int mSelectionStartWhenBeginBatchEdit;
    private String mTextWhenBeginBatchEdit;
    private String mToStringCache;
    private int mBatchEditNestDepth = 0;
    private int mChangeNotificationDepth = 0;
    private ArrayList<EditingStateWatcher> mListeners = new ArrayList<>();
    private ArrayList<EditingStateWatcher> mPendingListeners = new ArrayList<>();
    private ArrayList<TextEditingDelta> mBatchTextEditingDeltas = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface EditingStateWatcher {
        void didChangeEditingState(boolean z, boolean z2, boolean z3);
    }

    public ListenableEditingState(TextInputChannel.TextEditState initialState, View view) {
        this.mDummyConnection = new BaseInputConnection(view, true) { // from class: io.flutter.plugin.editing.ListenableEditingState.1
            @Override // android.view.inputmethod.BaseInputConnection
            public Editable getEditable() {
                return this;
            }
        };
        if (initialState != null) {
            setEditingState(initialState);
        }
    }

    public ArrayList<TextEditingDelta> extractBatchTextEditingDeltas() {
        ArrayList<TextEditingDelta> currentBatchDeltas = new ArrayList<>(this.mBatchTextEditingDeltas);
        this.mBatchTextEditingDeltas.clear();
        return currentBatchDeltas;
    }

    public void clearBatchDeltas() {
        this.mBatchTextEditingDeltas.clear();
    }

    public void beginBatchEdit() {
        this.mBatchEditNestDepth++;
        if (this.mChangeNotificationDepth > 0) {
            Log.e(TAG, "editing state should not be changed in a listener callback");
        }
        if (this.mBatchEditNestDepth == 1 && !this.mListeners.isEmpty()) {
            this.mTextWhenBeginBatchEdit = toString();
            this.mSelectionStartWhenBeginBatchEdit = getSelectionStart();
            this.mSelectionEndWhenBeginBatchEdit = getSelectionEnd();
            this.mComposingStartWhenBeginBatchEdit = getComposingStart();
            this.mComposingEndWhenBeginBatchEdit = getComposingEnd();
        }
    }

    public void endBatchEdit() {
        int i = this.mBatchEditNestDepth;
        if (i == 0) {
            Log.e(TAG, "endBatchEdit called without a matching beginBatchEdit");
            return;
        }
        if (i == 1) {
            Iterator<EditingStateWatcher> it = this.mPendingListeners.iterator();
            while (it.hasNext()) {
                EditingStateWatcher listener = it.next();
                notifyListener(listener, true, true, true);
            }
            if (!this.mListeners.isEmpty()) {
                Log.v(TAG, "didFinishBatchEdit with " + String.valueOf(this.mListeners.size()) + " listener(s)");
                boolean textChanged = !toString().equals(this.mTextWhenBeginBatchEdit);
                boolean z = false;
                boolean selectionChanged = (this.mSelectionStartWhenBeginBatchEdit == getSelectionStart() && this.mSelectionEndWhenBeginBatchEdit == getSelectionEnd()) ? false : true;
                boolean composingRegionChanged = (this.mComposingStartWhenBeginBatchEdit == getComposingStart() && this.mComposingEndWhenBeginBatchEdit == getComposingEnd()) ? true : true;
                notifyListenersIfNeeded(textChanged, selectionChanged, composingRegionChanged);
            }
        }
        this.mListeners.addAll(this.mPendingListeners);
        this.mPendingListeners.clear();
        this.mBatchEditNestDepth--;
    }

    public void setComposingRange(int composingStart, int composingEnd) {
        if (composingStart < 0 || composingStart >= composingEnd) {
            BaseInputConnection.removeComposingSpans(this);
        } else {
            this.mDummyConnection.setComposingRegion(composingStart, composingEnd);
        }
    }

    public void setEditingState(TextInputChannel.TextEditState newState) {
        beginBatchEdit();
        replace(0, length(), (CharSequence) newState.text);
        if (newState.hasSelection()) {
            Selection.setSelection(this, newState.selectionStart, newState.selectionEnd);
        } else {
            Selection.removeSelection(this);
        }
        setComposingRange(newState.composingStart, newState.composingEnd);
        clearBatchDeltas();
        endBatchEdit();
    }

    public void addEditingStateListener(EditingStateWatcher listener) {
        if (this.mChangeNotificationDepth > 0) {
            Log.e(TAG, "adding a listener " + listener.toString() + " in a listener callback");
        }
        if (this.mBatchEditNestDepth > 0) {
            Log.w(TAG, "a listener was added to EditingState while a batch edit was in progress");
            this.mPendingListeners.add(listener);
            return;
        }
        this.mListeners.add(listener);
    }

    public void removeEditingStateListener(EditingStateWatcher listener) {
        if (this.mChangeNotificationDepth > 0) {
            Log.e(TAG, "removing a listener " + listener.toString() + " in a listener callback");
        }
        this.mListeners.remove(listener);
        if (this.mBatchEditNestDepth > 0) {
            this.mPendingListeners.remove(listener);
        }
    }

    @Override // android.text.SpannableStringBuilder, android.text.Editable
    public SpannableStringBuilder replace(int start, int end, CharSequence tb, int tbstart, int tbend) {
        if (this.mChangeNotificationDepth > 0) {
            Log.e(TAG, "editing state should not be changed in a listener callback");
        }
        CharSequence oldText = toString();
        boolean textChanged = end - start != tbend - tbstart;
        boolean textChanged2 = textChanged;
        for (int i = 0; i < end - start && !textChanged2; i++) {
            textChanged2 |= charAt(start + i) != tb.charAt(tbstart + i);
        }
        if (textChanged2) {
            this.mToStringCache = null;
        }
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        int composingStart = getComposingStart();
        int composingEnd = getComposingEnd();
        SpannableStringBuilder editable = super.replace(start, end, tb, tbstart, tbend);
        boolean textChanged3 = textChanged2;
        this.mBatchTextEditingDeltas.add(new TextEditingDelta(oldText, start, end, tb, getSelectionStart(), getSelectionEnd(), getComposingStart(), getComposingEnd()));
        if (this.mBatchEditNestDepth > 0) {
            return editable;
        }
        boolean selectionChanged = (getSelectionStart() == selectionStart && getSelectionEnd() == selectionEnd) ? false : true;
        boolean composingRegionChanged = (getComposingStart() == composingStart && getComposingEnd() == composingEnd) ? false : true;
        notifyListenersIfNeeded(textChanged3, selectionChanged, composingRegionChanged);
        return editable;
    }

    private void notifyListener(EditingStateWatcher listener, boolean textChanged, boolean selectionChanged, boolean composingChanged) {
        this.mChangeNotificationDepth++;
        listener.didChangeEditingState(textChanged, selectionChanged, composingChanged);
        this.mChangeNotificationDepth--;
    }

    private void notifyListenersIfNeeded(boolean textChanged, boolean selectionChanged, boolean composingChanged) {
        if (textChanged || selectionChanged || composingChanged) {
            Iterator<EditingStateWatcher> it = this.mListeners.iterator();
            while (it.hasNext()) {
                EditingStateWatcher listener = it.next();
                notifyListener(listener, textChanged, selectionChanged, composingChanged);
            }
        }
    }

    public final int getSelectionStart() {
        return Selection.getSelectionStart(this);
    }

    public final int getSelectionEnd() {
        return Selection.getSelectionEnd(this);
    }

    public final int getComposingStart() {
        return BaseInputConnection.getComposingSpanStart(this);
    }

    public final int getComposingEnd() {
        return BaseInputConnection.getComposingSpanEnd(this);
    }

    @Override // android.text.SpannableStringBuilder, android.text.Spannable
    public void setSpan(Object what, int start, int end, int flags) {
        super.setSpan(what, start, end, flags);
        this.mBatchTextEditingDeltas.add(new TextEditingDelta(toString(), getSelectionStart(), getSelectionEnd(), getComposingStart(), getComposingEnd()));
    }

    @Override // android.text.SpannableStringBuilder, java.lang.CharSequence
    public String toString() {
        String str = this.mToStringCache;
        if (str != null) {
            return str;
        }
        String spannableStringBuilder = super.toString();
        this.mToStringCache = spannableStringBuilder;
        return spannableStringBuilder;
    }
}
