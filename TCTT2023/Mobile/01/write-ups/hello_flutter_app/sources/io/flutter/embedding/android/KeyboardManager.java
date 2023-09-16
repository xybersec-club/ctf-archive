package io.flutter.embedding.android;

import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import io.flutter.Log;
import io.flutter.embedding.engine.systemchannels.KeyEventChannel;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.editing.InputConnectionAdaptor;
import java.util.HashSet;
/* loaded from: classes.dex */
public class KeyboardManager implements InputConnectionAdaptor.KeyboardDelegate {
    private static final String TAG = "KeyboardManager";
    private final HashSet<KeyEvent> redispatchedEvents = new HashSet<>();
    protected final Responder[] responders;
    private final ViewDelegate viewDelegate;

    /* loaded from: classes.dex */
    public interface Responder {

        /* loaded from: classes.dex */
        public interface OnKeyEventHandledCallback {
            void onKeyEventHandled(boolean z);
        }

        void handleEvent(KeyEvent keyEvent, OnKeyEventHandledCallback onKeyEventHandledCallback);
    }

    /* loaded from: classes.dex */
    public interface ViewDelegate {
        BinaryMessenger getBinaryMessenger();

        boolean onTextInputKeyEvent(KeyEvent keyEvent);

        void redispatch(KeyEvent keyEvent);
    }

    /* loaded from: classes.dex */
    public static class CharacterCombiner {
        private int combiningCharacter = 0;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Character applyCombiningCharacterToBaseCharacter(int newCharacterCodePoint) {
            char complexCharacter = (char) newCharacterCodePoint;
            boolean isNewCodePointACombiningCharacter = (Integer.MIN_VALUE & newCharacterCodePoint) != 0;
            if (isNewCodePointACombiningCharacter) {
                int plainCodePoint = Integer.MAX_VALUE & newCharacterCodePoint;
                int i = this.combiningCharacter;
                if (i != 0) {
                    this.combiningCharacter = KeyCharacterMap.getDeadChar(i, plainCodePoint);
                } else {
                    this.combiningCharacter = plainCodePoint;
                }
            } else {
                int i2 = this.combiningCharacter;
                if (i2 != 0) {
                    int combinedChar = KeyCharacterMap.getDeadChar(i2, newCharacterCodePoint);
                    if (combinedChar > 0) {
                        complexCharacter = (char) combinedChar;
                    }
                    this.combiningCharacter = 0;
                }
            }
            return Character.valueOf(complexCharacter);
        }
    }

    public KeyboardManager(ViewDelegate viewDelegate) {
        this.viewDelegate = viewDelegate;
        this.responders = new Responder[]{new KeyEmbedderResponder(viewDelegate.getBinaryMessenger()), new KeyChannelResponder(new KeyEventChannel(viewDelegate.getBinaryMessenger()))};
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PerEventCallbackBuilder {
        boolean isEventHandled = false;
        final KeyEvent keyEvent;
        int unrepliedCount;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class Callback implements Responder.OnKeyEventHandledCallback {
            boolean isCalled;

            private Callback() {
                this.isCalled = false;
            }

            @Override // io.flutter.embedding.android.KeyboardManager.Responder.OnKeyEventHandledCallback
            public void onKeyEventHandled(boolean canHandleEvent) {
                if (this.isCalled) {
                    throw new IllegalStateException("The onKeyEventHandledCallback should be called exactly once.");
                }
                this.isCalled = true;
                PerEventCallbackBuilder.this.unrepliedCount--;
                PerEventCallbackBuilder.this.isEventHandled |= canHandleEvent;
                if (PerEventCallbackBuilder.this.unrepliedCount == 0 && !PerEventCallbackBuilder.this.isEventHandled) {
                    KeyboardManager.this.onUnhandled(PerEventCallbackBuilder.this.keyEvent);
                }
            }
        }

        PerEventCallbackBuilder(KeyEvent keyEvent) {
            this.unrepliedCount = KeyboardManager.this.responders.length;
            this.keyEvent = keyEvent;
        }

        public Responder.OnKeyEventHandledCallback buildCallback() {
            return new Callback();
        }
    }

    @Override // io.flutter.plugin.editing.InputConnectionAdaptor.KeyboardDelegate
    public boolean handleEvent(KeyEvent keyEvent) {
        Responder[] responderArr;
        boolean isRedispatchedEvent = this.redispatchedEvents.remove(keyEvent);
        if (isRedispatchedEvent) {
            return false;
        }
        if (this.responders.length > 0) {
            PerEventCallbackBuilder callbackBuilder = new PerEventCallbackBuilder(keyEvent);
            for (Responder primaryResponder : this.responders) {
                primaryResponder.handleEvent(keyEvent, callbackBuilder.buildCallback());
            }
            return true;
        }
        onUnhandled(keyEvent);
        return true;
    }

    public void destroy() {
        int remainingRedispatchCount = this.redispatchedEvents.size();
        if (remainingRedispatchCount > 0) {
            Log.w(TAG, "A KeyboardManager was destroyed with " + String.valueOf(remainingRedispatchCount) + " unhandled redispatch event(s).");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUnhandled(KeyEvent keyEvent) {
        ViewDelegate viewDelegate = this.viewDelegate;
        if (viewDelegate == null || viewDelegate.onTextInputKeyEvent(keyEvent)) {
            return;
        }
        this.redispatchedEvents.add(keyEvent);
        this.viewDelegate.redispatch(keyEvent);
        if (this.redispatchedEvents.remove(keyEvent)) {
            Log.w(TAG, "A redispatched key event was consumed before reaching KeyboardManager");
        }
    }
}
