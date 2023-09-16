package io.flutter.embedding.android;

import android.view.KeyEvent;
import io.flutter.embedding.android.KeyData;
import io.flutter.embedding.android.KeyboardManager;
import io.flutter.embedding.android.KeyboardMap;
import io.flutter.plugin.common.BinaryMessenger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/* loaded from: classes.dex */
public class KeyEmbedderResponder implements KeyboardManager.Responder {
    private static final String TAG = "KeyEmbedderResponder";
    private final BinaryMessenger messenger;
    private final HashMap<Long, Long> pressingRecords = new HashMap<>();
    private final HashMap<Long, KeyboardMap.TogglingGoal> togglingGoals = new HashMap<>();
    private final KeyboardManager.CharacterCombiner characterCombiner = new KeyboardManager.CharacterCombiner();

    private static KeyData.Type getEventType(KeyEvent event) {
        boolean isRepeatEvent = event.getRepeatCount() > 0;
        switch (event.getAction()) {
            case 0:
                return isRepeatEvent ? KeyData.Type.kRepeat : KeyData.Type.kDown;
            case 1:
                return KeyData.Type.kUp;
            default:
                throw new AssertionError("Unexpected event type");
        }
    }

    public KeyEmbedderResponder(BinaryMessenger messenger) {
        KeyboardMap.TogglingGoal[] togglingGoals;
        this.messenger = messenger;
        for (KeyboardMap.TogglingGoal goal : KeyboardMap.getTogglingGoals()) {
            this.togglingGoals.put(Long.valueOf(goal.logicalKey), goal);
        }
    }

    private static long keyOfPlane(long key, long plane) {
        return (KeyboardMap.kValueMask & key) | plane;
    }

    private Long getPhysicalKey(KeyEvent event) {
        long scancode = event.getScanCode();
        if (scancode == 0) {
            return Long.valueOf(keyOfPlane(event.getKeyCode(), KeyboardMap.kAndroidPlane));
        }
        Long byMapping = KeyboardMap.scanCodeToPhysical.get(Long.valueOf(scancode));
        if (byMapping == null) {
            return Long.valueOf(keyOfPlane(event.getScanCode(), KeyboardMap.kAndroidPlane));
        }
        return byMapping;
    }

    private Long getLogicalKey(KeyEvent event) {
        Long byMapping = KeyboardMap.keyCodeToLogical.get(Long.valueOf(event.getKeyCode()));
        if (byMapping != null) {
            return byMapping;
        }
        return Long.valueOf(keyOfPlane(event.getKeyCode(), KeyboardMap.kAndroidPlane));
    }

    void updatePressingState(Long physicalKey, Long logicalKey) {
        if (logicalKey != null) {
            Long previousValue = this.pressingRecords.put(physicalKey, logicalKey);
            if (previousValue != null) {
                throw new AssertionError("The key was not empty");
            }
            return;
        }
        Long previousValue2 = this.pressingRecords.remove(physicalKey);
        if (previousValue2 == null) {
            throw new AssertionError("The key was empty");
        }
    }

    void synchronizePressingKey(KeyboardMap.PressingGoal goal, boolean truePressed, long eventLogicalKey, final long eventPhysicalKey, final KeyEvent event, ArrayList<Runnable> postSynchronize) {
        boolean[] nowStates = new boolean[goal.keys.length];
        Boolean[] preEventStates = new Boolean[goal.keys.length];
        boolean postEventAnyPressed = false;
        int keyIdx = 0;
        while (true) {
            boolean z = false;
            if (keyIdx < goal.keys.length) {
                final KeyboardMap.KeyPair key = goal.keys[keyIdx];
                nowStates[keyIdx] = this.pressingRecords.containsKey(Long.valueOf(key.physicalKey));
                if (key.logicalKey == eventLogicalKey) {
                    switch (AnonymousClass1.$SwitchMap$io$flutter$embedding$android$KeyData$Type[getEventType(event).ordinal()]) {
                        case 1:
                            preEventStates[keyIdx] = false;
                            if (!truePressed) {
                                postSynchronize.add(new Runnable() { // from class: io.flutter.embedding.android.KeyEmbedderResponder$$ExternalSyntheticLambda1
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        KeyEmbedderResponder.this.m18x63b96e0c(key, eventPhysicalKey, event);
                                    }
                                });
                            }
                            postEventAnyPressed = true;
                            continue;
                        case 2:
                            preEventStates[keyIdx] = Boolean.valueOf(nowStates[keyIdx]);
                            continue;
                        case 3:
                            if (!truePressed) {
                                postSynchronize.add(new Runnable() { // from class: io.flutter.embedding.android.KeyEmbedderResponder$$ExternalSyntheticLambda2
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        KeyEmbedderResponder.this.m19xa7448bcd(key, event);
                                    }
                                });
                            }
                            preEventStates[keyIdx] = Boolean.valueOf(nowStates[keyIdx]);
                            postEventAnyPressed = true;
                            continue;
                        default:
                            continue;
                    }
                } else {
                    postEventAnyPressed = (postEventAnyPressed || nowStates[keyIdx]) ? true : true;
                }
                keyIdx++;
            } else {
                if (truePressed) {
                    for (int keyIdx2 = 0; keyIdx2 < goal.keys.length; keyIdx2++) {
                        if (preEventStates[keyIdx2] == null) {
                            if (postEventAnyPressed) {
                                preEventStates[keyIdx2] = Boolean.valueOf(nowStates[keyIdx2]);
                            } else {
                                preEventStates[keyIdx2] = true;
                                postEventAnyPressed = true;
                            }
                        }
                    }
                    if (!postEventAnyPressed) {
                        preEventStates[0] = true;
                    }
                } else {
                    for (int keyIdx3 = 0; keyIdx3 < goal.keys.length; keyIdx3++) {
                        if (preEventStates[keyIdx3] == null) {
                            preEventStates[keyIdx3] = false;
                        }
                    }
                }
                for (int keyIdx4 = 0; keyIdx4 < goal.keys.length; keyIdx4++) {
                    if (nowStates[keyIdx4] != preEventStates[keyIdx4].booleanValue()) {
                        KeyboardMap.KeyPair key2 = goal.keys[keyIdx4];
                        synthesizeEvent(preEventStates[keyIdx4].booleanValue(), Long.valueOf(key2.logicalKey), Long.valueOf(key2.physicalKey), event.getEventTime());
                    }
                }
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: io.flutter.embedding.android.KeyEmbedderResponder$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$io$flutter$embedding$android$KeyData$Type;

        static {
            int[] iArr = new int[KeyData.Type.values().length];
            $SwitchMap$io$flutter$embedding$android$KeyData$Type = iArr;
            try {
                iArr[KeyData.Type.kDown.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$io$flutter$embedding$android$KeyData$Type[KeyData.Type.kUp.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$io$flutter$embedding$android$KeyData$Type[KeyData.Type.kRepeat.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$synchronizePressingKey$0$io-flutter-embedding-android-KeyEmbedderResponder  reason: not valid java name */
    public /* synthetic */ void m18x63b96e0c(KeyboardMap.KeyPair key, long eventPhysicalKey, KeyEvent event) {
        synthesizeEvent(false, Long.valueOf(key.logicalKey), Long.valueOf(eventPhysicalKey), event.getEventTime());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$synchronizePressingKey$1$io-flutter-embedding-android-KeyEmbedderResponder  reason: not valid java name */
    public /* synthetic */ void m19xa7448bcd(KeyboardMap.KeyPair key, KeyEvent event) {
        synthesizeEvent(false, Long.valueOf(key.logicalKey), Long.valueOf(key.physicalKey), event.getEventTime());
    }

    void synchronizeTogglingKey(KeyboardMap.TogglingGoal goal, boolean trueEnabled, long eventLogicalKey, KeyEvent event) {
        if (goal.logicalKey != eventLogicalKey && goal.enabled != trueEnabled) {
            boolean firstIsDown = !this.pressingRecords.containsKey(Long.valueOf(goal.physicalKey));
            if (firstIsDown) {
                goal.enabled = !goal.enabled;
            }
            synthesizeEvent(firstIsDown, Long.valueOf(goal.logicalKey), Long.valueOf(goal.physicalKey), event.getEventTime());
            if (!firstIsDown) {
                goal.enabled = !goal.enabled;
            }
            synthesizeEvent(!firstIsDown, Long.valueOf(goal.logicalKey), Long.valueOf(goal.physicalKey), event.getEventTime());
        }
    }

    private boolean handleEventImpl(KeyEvent event, KeyboardManager.Responder.OnKeyEventHandledCallback onKeyEventHandledCallback) {
        KeyboardMap.PressingGoal[] pressingGoalArr;
        boolean isDownEvent;
        KeyData.Type type;
        KeyboardMap.TogglingGoal maybeTogglingGoal;
        if (event.getScanCode() == 0 && event.getKeyCode() == 0) {
            return false;
        }
        Long physicalKey = getPhysicalKey(event);
        Long logicalKey = getLogicalKey(event);
        ArrayList<Runnable> postSynchronizeEvents = new ArrayList<>();
        for (KeyboardMap.PressingGoal goal : KeyboardMap.pressingGoals) {
            synchronizePressingKey(goal, (event.getMetaState() & goal.mask) != 0, logicalKey.longValue(), physicalKey.longValue(), event, postSynchronizeEvents);
        }
        for (KeyboardMap.TogglingGoal goal2 : this.togglingGoals.values()) {
            synchronizeTogglingKey(goal2, (event.getMetaState() & goal2.mask) != 0, logicalKey.longValue(), event);
        }
        switch (event.getAction()) {
            case 0:
                isDownEvent = true;
                break;
            case 1:
                isDownEvent = false;
                break;
            default:
                return false;
        }
        String character = null;
        Long lastLogicalRecord = this.pressingRecords.get(physicalKey);
        if (isDownEvent) {
            if (lastLogicalRecord == null) {
                type = KeyData.Type.kDown;
            } else if (event.getRepeatCount() > 0) {
                type = KeyData.Type.kRepeat;
            } else {
                synthesizeEvent(false, lastLogicalRecord, physicalKey, event.getEventTime());
                type = KeyData.Type.kDown;
            }
            char complexChar = this.characterCombiner.applyCombiningCharacterToBaseCharacter(event.getUnicodeChar()).charValue();
            if (complexChar != 0) {
                character = "" + complexChar;
            }
        } else if (lastLogicalRecord == null) {
            return false;
        } else {
            type = KeyData.Type.kUp;
        }
        if (type != KeyData.Type.kRepeat) {
            updatePressingState(physicalKey, isDownEvent ? logicalKey : null);
        }
        if (type == KeyData.Type.kDown && (maybeTogglingGoal = this.togglingGoals.get(logicalKey)) != null) {
            maybeTogglingGoal.enabled = !maybeTogglingGoal.enabled;
        }
        KeyData output = new KeyData();
        output.timestamp = event.getEventTime();
        output.type = type;
        output.logicalKey = logicalKey.longValue();
        output.physicalKey = physicalKey.longValue();
        output.character = character;
        output.synthesized = false;
        sendKeyEvent(output, onKeyEventHandledCallback);
        Iterator<Runnable> it = postSynchronizeEvents.iterator();
        while (it.hasNext()) {
            Runnable postSyncEvent = it.next();
            postSyncEvent.run();
        }
        return true;
    }

    private void synthesizeEvent(boolean isDown, Long logicalKey, Long physicalKey, long timestamp) {
        KeyData output = new KeyData();
        output.timestamp = timestamp;
        output.type = isDown ? KeyData.Type.kDown : KeyData.Type.kUp;
        output.logicalKey = logicalKey.longValue();
        output.physicalKey = physicalKey.longValue();
        output.character = null;
        output.synthesized = true;
        if (physicalKey.longValue() != 0 && logicalKey.longValue() != 0) {
            updatePressingState(physicalKey, isDown ? logicalKey : null);
        }
        sendKeyEvent(output, null);
    }

    private void sendKeyEvent(KeyData data, final KeyboardManager.Responder.OnKeyEventHandledCallback onKeyEventHandledCallback) {
        BinaryMessenger.BinaryReply handleMessageReply;
        if (onKeyEventHandledCallback == null) {
            handleMessageReply = null;
        } else {
            handleMessageReply = new BinaryMessenger.BinaryReply() { // from class: io.flutter.embedding.android.KeyEmbedderResponder$$ExternalSyntheticLambda0
                @Override // io.flutter.plugin.common.BinaryMessenger.BinaryReply
                public final void reply(ByteBuffer byteBuffer) {
                    KeyEmbedderResponder.lambda$sendKeyEvent$2(KeyboardManager.Responder.OnKeyEventHandledCallback.this, byteBuffer);
                }
            };
        }
        this.messenger.send(KeyData.CHANNEL, data.toBytes(), handleMessageReply);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$sendKeyEvent$2(KeyboardManager.Responder.OnKeyEventHandledCallback onKeyEventHandledCallback, ByteBuffer message) {
        Boolean handled = false;
        message.rewind();
        if (message.capacity() != 0) {
            handled = Boolean.valueOf(message.get() != 0);
        }
        onKeyEventHandledCallback.onKeyEventHandled(handled.booleanValue());
    }

    @Override // io.flutter.embedding.android.KeyboardManager.Responder
    public void handleEvent(KeyEvent event, KeyboardManager.Responder.OnKeyEventHandledCallback onKeyEventHandledCallback) {
        boolean sentAny = handleEventImpl(event, onKeyEventHandledCallback);
        if (!sentAny) {
            synthesizeEvent(true, 0L, 0L, 0L);
            onKeyEventHandledCallback.onKeyEventHandled(true);
        }
    }
}
