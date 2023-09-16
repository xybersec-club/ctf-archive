package io.flutter.plugin.editing;

import io.flutter.Log;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public final class TextEditingDelta {
    private static final String TAG = "TextEditingDelta";
    private int deltaEnd;
    private int deltaStart;
    private CharSequence deltaText;
    private int newComposingEnd;
    private int newComposingStart;
    private int newSelectionEnd;
    private int newSelectionStart;
    private CharSequence oldText;

    public TextEditingDelta(CharSequence oldEditable, int replacementDestinationStart, int replacementDestinationEnd, CharSequence replacementSource, int selectionStart, int selectionEnd, int composingStart, int composingEnd) {
        this.newSelectionStart = selectionStart;
        this.newSelectionEnd = selectionEnd;
        this.newComposingStart = composingStart;
        this.newComposingEnd = composingEnd;
        setDeltas(oldEditable, replacementSource.toString(), replacementDestinationStart, replacementDestinationEnd);
    }

    public TextEditingDelta(CharSequence oldText, int selectionStart, int selectionEnd, int composingStart, int composingEnd) {
        this.newSelectionStart = selectionStart;
        this.newSelectionEnd = selectionEnd;
        this.newComposingStart = composingStart;
        this.newComposingEnd = composingEnd;
        setDeltas(oldText, "", -1, -1);
    }

    public CharSequence getOldText() {
        return this.oldText;
    }

    public CharSequence getDeltaText() {
        return this.deltaText;
    }

    public int getDeltaStart() {
        return this.deltaStart;
    }

    public int getDeltaEnd() {
        return this.deltaEnd;
    }

    public int getNewSelectionStart() {
        return this.newSelectionStart;
    }

    public int getNewSelectionEnd() {
        return this.newSelectionEnd;
    }

    public int getNewComposingStart() {
        return this.newComposingStart;
    }

    public int getNewComposingEnd() {
        return this.newComposingEnd;
    }

    private void setDeltas(CharSequence oldText, CharSequence newText, int newStart, int newExtent) {
        this.oldText = oldText;
        this.deltaText = newText;
        this.deltaStart = newStart;
        this.deltaEnd = newExtent;
    }

    public JSONObject toJSON() {
        JSONObject delta = new JSONObject();
        try {
            delta.put("oldText", this.oldText.toString());
            delta.put("deltaText", this.deltaText.toString());
            delta.put("deltaStart", this.deltaStart);
            delta.put("deltaEnd", this.deltaEnd);
            delta.put("selectionBase", this.newSelectionStart);
            delta.put("selectionExtent", this.newSelectionEnd);
            delta.put("composingBase", this.newComposingStart);
            delta.put("composingExtent", this.newComposingEnd);
        } catch (JSONException e) {
            Log.e(TAG, "unable to create JSONObject: " + e);
        }
        return delta;
    }
}
