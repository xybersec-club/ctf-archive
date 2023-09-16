package io.flutter.embedding.android;

import android.view.KeyEvent;
import io.flutter.embedding.android.KeyboardManager;
import io.flutter.embedding.engine.systemchannels.KeyEventChannel;
/* loaded from: classes.dex */
public class KeyChannelResponder implements KeyboardManager.Responder {
    private static final String TAG = "KeyChannelResponder";
    private final KeyboardManager.CharacterCombiner characterCombiner = new KeyboardManager.CharacterCombiner();
    private final KeyEventChannel keyEventChannel;

    public KeyChannelResponder(KeyEventChannel keyEventChannel) {
        this.keyEventChannel = keyEventChannel;
    }

    @Override // io.flutter.embedding.android.KeyboardManager.Responder
    public void handleEvent(KeyEvent keyEvent, final KeyboardManager.Responder.OnKeyEventHandledCallback onKeyEventHandledCallback) {
        int action = keyEvent.getAction();
        if (action != 0 && action != 1) {
            onKeyEventHandledCallback.onKeyEventHandled(false);
            return;
        }
        Character complexCharacter = this.characterCombiner.applyCombiningCharacterToBaseCharacter(keyEvent.getUnicodeChar());
        KeyEventChannel.FlutterKeyEvent flutterEvent = new KeyEventChannel.FlutterKeyEvent(keyEvent, complexCharacter);
        boolean isKeyUp = action != 0;
        this.keyEventChannel.sendFlutterKeyEvent(flutterEvent, isKeyUp, new KeyEventChannel.EventResponseHandler() { // from class: io.flutter.embedding.android.KeyChannelResponder$$ExternalSyntheticLambda0
            @Override // io.flutter.embedding.engine.systemchannels.KeyEventChannel.EventResponseHandler
            public final void onFrameworkResponse(boolean z) {
                KeyboardManager.Responder.OnKeyEventHandledCallback.this.onKeyEventHandled(z);
            }
        });
    }
}
