package io.flutter.view;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.LocaleSpan;
import android.text.style.TtsSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import io.flutter.Log;
import io.flutter.embedding.engine.systemchannels.AccessibilityChannel;
import io.flutter.plugin.platform.PlatformViewsAccessibilityDelegate;
import io.flutter.util.Predicate;
import io.flutter.util.ViewUtils;
import io.flutter.view.AccessibilityBridge;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public class AccessibilityBridge extends AccessibilityNodeProvider {
    private static final int ACTION_SHOW_ON_SCREEN = 16908342;
    private static final int BOLD_TEXT_WEIGHT_ADJUSTMENT = 300;
    private static final int MIN_ENGINE_GENERATED_NODE_ID = 65536;
    private static final int ROOT_NODE_ID = 0;
    private static final float SCROLL_EXTENT_FOR_INFINITY = 100000.0f;
    private static final float SCROLL_POSITION_CAP_FOR_INFINITY = 70000.0f;
    private static final String TAG = "AccessibilityBridge";
    private final AccessibilityChannel accessibilityChannel;
    private int accessibilityFeatureFlags;
    private SemanticsNode accessibilityFocusedSemanticsNode;
    private final AccessibilityManager accessibilityManager;
    private final AccessibilityChannel.AccessibilityMessageHandler accessibilityMessageHandler;
    private final AccessibilityManager.AccessibilityStateChangeListener accessibilityStateChangeListener;
    private final AccessibilityViewEmbedder accessibilityViewEmbedder;
    private boolean accessibleNavigation;
    private final ContentObserver animationScaleObserver;
    private final ContentResolver contentResolver;
    private final Map<Integer, CustomAccessibilityAction> customAccessibilityActions;
    private Integer embeddedAccessibilityFocusedNodeId;
    private Integer embeddedInputFocusedNodeId;
    private final List<Integer> flutterNavigationStack;
    private final Map<Integer, SemanticsNode> flutterSemanticsTree;
    private SemanticsNode hoveredObject;
    private SemanticsNode inputFocusedSemanticsNode;
    private boolean isReleased;
    private SemanticsNode lastInputFocusedSemanticsNode;
    private Integer lastLeftFrameInset;
    private OnAccessibilityChangeListener onAccessibilityChangeListener;
    private final PlatformViewsAccessibilityDelegate platformViewsAccessibilityDelegate;
    private int previousRouteId;
    private final View rootAccessibilityView;
    private final AccessibilityManager.TouchExplorationStateChangeListener touchExplorationStateChangeListener;
    private static final int SCROLLABLE_ACTIONS = ((Action.SCROLL_RIGHT.value | Action.SCROLL_LEFT.value) | Action.SCROLL_UP.value) | Action.SCROLL_DOWN.value;
    private static final int FOCUSABLE_FLAGS = ((((((((((Flag.HAS_CHECKED_STATE.value | Flag.IS_CHECKED.value) | Flag.IS_SELECTED.value) | Flag.IS_TEXT_FIELD.value) | Flag.IS_FOCUSED.value) | Flag.HAS_ENABLED_STATE.value) | Flag.IS_ENABLED.value) | Flag.IS_IN_MUTUALLY_EXCLUSIVE_GROUP.value) | Flag.HAS_TOGGLED_STATE.value) | Flag.IS_TOGGLED.value) | Flag.IS_FOCUSABLE.value) | Flag.IS_SLIDER.value;
    private static int FIRST_RESOURCE_ID = 267386881;

    /* loaded from: classes.dex */
    public interface OnAccessibilityChangeListener {
        void onAccessibilityChanged(boolean z, boolean z2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum StringAttributeType {
        SPELLOUT,
        LOCALE
    }

    static /* synthetic */ int access$1272(AccessibilityBridge x0, int x1) {
        int i = x0.accessibilityFeatureFlags & x1;
        x0.accessibilityFeatureFlags = i;
        return i;
    }

    static /* synthetic */ int access$1276(AccessibilityBridge x0, int x1) {
        int i = x0.accessibilityFeatureFlags | x1;
        x0.accessibilityFeatureFlags = i;
        return i;
    }

    public int getHoveredObjectId() {
        return this.hoveredObject.id;
    }

    public boolean getAccessibleNavigation() {
        return this.accessibleNavigation;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAccessibleNavigation(boolean value) {
        if (this.accessibleNavigation == value) {
            return;
        }
        this.accessibleNavigation = value;
        if (value) {
            this.accessibilityFeatureFlags |= AccessibilityFeature.ACCESSIBLE_NAVIGATION.value;
        } else {
            this.accessibilityFeatureFlags &= AccessibilityFeature.ACCESSIBLE_NAVIGATION.value ^ (-1);
        }
        sendLatestAccessibilityFlagsToFlutter();
    }

    public AccessibilityBridge(View rootAccessibilityView, AccessibilityChannel accessibilityChannel, AccessibilityManager accessibilityManager, ContentResolver contentResolver, PlatformViewsAccessibilityDelegate platformViewsAccessibilityDelegate) {
        this(rootAccessibilityView, accessibilityChannel, accessibilityManager, contentResolver, new AccessibilityViewEmbedder(rootAccessibilityView, 65536), platformViewsAccessibilityDelegate);
    }

    public AccessibilityBridge(View rootAccessibilityView, AccessibilityChannel accessibilityChannel, final AccessibilityManager accessibilityManager, ContentResolver contentResolver, AccessibilityViewEmbedder accessibilityViewEmbedder, PlatformViewsAccessibilityDelegate platformViewsAccessibilityDelegate) {
        this.flutterSemanticsTree = new HashMap();
        this.customAccessibilityActions = new HashMap();
        this.accessibilityFeatureFlags = 0;
        this.flutterNavigationStack = new ArrayList();
        this.previousRouteId = 0;
        this.lastLeftFrameInset = 0;
        this.accessibleNavigation = false;
        this.isReleased = false;
        this.accessibilityMessageHandler = new AccessibilityChannel.AccessibilityMessageHandler() { // from class: io.flutter.view.AccessibilityBridge.1
            @Override // io.flutter.embedding.engine.systemchannels.AccessibilityChannel.AccessibilityMessageHandler
            public void announce(String message) {
                AccessibilityBridge.this.rootAccessibilityView.announceForAccessibility(message);
            }

            @Override // io.flutter.embedding.engine.systemchannels.AccessibilityChannel.AccessibilityMessageHandler
            public void onTap(int nodeId) {
                AccessibilityBridge.this.sendAccessibilityEvent(nodeId, 1);
            }

            @Override // io.flutter.embedding.engine.systemchannels.AccessibilityChannel.AccessibilityMessageHandler
            public void onLongPress(int nodeId) {
                AccessibilityBridge.this.sendAccessibilityEvent(nodeId, 2);
            }

            @Override // io.flutter.embedding.engine.systemchannels.AccessibilityChannel.AccessibilityMessageHandler
            public void onTooltip(String message) {
                if (Build.VERSION.SDK_INT < 28) {
                    AccessibilityEvent e = AccessibilityBridge.this.obtainAccessibilityEvent(0, 32);
                    e.getText().add(message);
                    AccessibilityBridge.this.sendAccessibilityEvent(e);
                }
            }

            @Override // io.flutter.embedding.engine.FlutterJNI.AccessibilityDelegate
            public void updateCustomAccessibilityActions(ByteBuffer buffer, String[] strings) {
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                AccessibilityBridge.this.updateCustomAccessibilityActions(buffer, strings);
            }

            @Override // io.flutter.embedding.engine.FlutterJNI.AccessibilityDelegate
            public void updateSemantics(ByteBuffer buffer, String[] strings, ByteBuffer[] stringAttributeArgs) {
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                for (ByteBuffer args : stringAttributeArgs) {
                    args.order(ByteOrder.LITTLE_ENDIAN);
                }
                AccessibilityBridge.this.updateSemantics(buffer, strings, stringAttributeArgs);
            }
        };
        AccessibilityManager.AccessibilityStateChangeListener accessibilityStateChangeListener = new AccessibilityManager.AccessibilityStateChangeListener() { // from class: io.flutter.view.AccessibilityBridge.2
            @Override // android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener
            public void onAccessibilityStateChanged(boolean accessibilityEnabled) {
                if (AccessibilityBridge.this.isReleased) {
                    return;
                }
                if (accessibilityEnabled) {
                    AccessibilityBridge.this.accessibilityChannel.setAccessibilityMessageHandler(AccessibilityBridge.this.accessibilityMessageHandler);
                    AccessibilityBridge.this.accessibilityChannel.onAndroidAccessibilityEnabled();
                } else {
                    AccessibilityBridge.this.setAccessibleNavigation(false);
                    AccessibilityBridge.this.accessibilityChannel.setAccessibilityMessageHandler(null);
                    AccessibilityBridge.this.accessibilityChannel.onAndroidAccessibilityDisabled();
                }
                if (AccessibilityBridge.this.onAccessibilityChangeListener != null) {
                    AccessibilityBridge.this.onAccessibilityChangeListener.onAccessibilityChanged(accessibilityEnabled, AccessibilityBridge.this.accessibilityManager.isTouchExplorationEnabled());
                }
            }
        };
        this.accessibilityStateChangeListener = accessibilityStateChangeListener;
        ContentObserver contentObserver = new ContentObserver(new Handler()) { // from class: io.flutter.view.AccessibilityBridge.3
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                onChange(selfChange, null);
            }

            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                String value;
                if (AccessibilityBridge.this.isReleased) {
                    return;
                }
                if (Build.VERSION.SDK_INT < 17) {
                    value = null;
                } else {
                    value = Settings.Global.getString(AccessibilityBridge.this.contentResolver, "transition_animation_scale");
                }
                boolean shouldAnimationsBeDisabled = value != null && value.equals("0");
                if (shouldAnimationsBeDisabled) {
                    AccessibilityBridge.access$1276(AccessibilityBridge.this, AccessibilityFeature.DISABLE_ANIMATIONS.value);
                } else {
                    AccessibilityBridge.access$1272(AccessibilityBridge.this, AccessibilityFeature.DISABLE_ANIMATIONS.value ^ (-1));
                }
                AccessibilityBridge.this.sendLatestAccessibilityFlagsToFlutter();
            }
        };
        this.animationScaleObserver = contentObserver;
        this.rootAccessibilityView = rootAccessibilityView;
        this.accessibilityChannel = accessibilityChannel;
        this.accessibilityManager = accessibilityManager;
        this.contentResolver = contentResolver;
        this.accessibilityViewEmbedder = accessibilityViewEmbedder;
        this.platformViewsAccessibilityDelegate = platformViewsAccessibilityDelegate;
        accessibilityStateChangeListener.onAccessibilityStateChanged(accessibilityManager.isEnabled());
        accessibilityManager.addAccessibilityStateChangeListener(accessibilityStateChangeListener);
        if (Build.VERSION.SDK_INT >= 19) {
            AccessibilityManager.TouchExplorationStateChangeListener touchExplorationStateChangeListener = new AccessibilityManager.TouchExplorationStateChangeListener() { // from class: io.flutter.view.AccessibilityBridge.4
                @Override // android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener
                public void onTouchExplorationStateChanged(boolean isTouchExplorationEnabled) {
                    if (AccessibilityBridge.this.isReleased) {
                        return;
                    }
                    if (!isTouchExplorationEnabled) {
                        AccessibilityBridge.this.setAccessibleNavigation(false);
                        AccessibilityBridge.this.onTouchExplorationExit();
                    }
                    if (AccessibilityBridge.this.onAccessibilityChangeListener != null) {
                        AccessibilityBridge.this.onAccessibilityChangeListener.onAccessibilityChanged(accessibilityManager.isEnabled(), isTouchExplorationEnabled);
                    }
                }
            };
            this.touchExplorationStateChangeListener = touchExplorationStateChangeListener;
            touchExplorationStateChangeListener.onTouchExplorationStateChanged(accessibilityManager.isTouchExplorationEnabled());
            accessibilityManager.addTouchExplorationStateChangeListener(touchExplorationStateChangeListener);
        } else {
            this.touchExplorationStateChangeListener = null;
        }
        if (Build.VERSION.SDK_INT >= 17) {
            contentObserver.onChange(false);
            Uri transitionUri = Settings.Global.getUriFor("transition_animation_scale");
            contentResolver.registerContentObserver(transitionUri, false, contentObserver);
        }
        if (Build.VERSION.SDK_INT >= 31) {
            setBoldTextFlag();
        }
        platformViewsAccessibilityDelegate.attachAccessibilityBridge(this);
    }

    public void release() {
        this.isReleased = true;
        this.platformViewsAccessibilityDelegate.detachAccessibilityBridge();
        setOnAccessibilityChangeListener(null);
        this.accessibilityManager.removeAccessibilityStateChangeListener(this.accessibilityStateChangeListener);
        if (Build.VERSION.SDK_INT >= 19) {
            this.accessibilityManager.removeTouchExplorationStateChangeListener(this.touchExplorationStateChangeListener);
        }
        this.contentResolver.unregisterContentObserver(this.animationScaleObserver);
        this.accessibilityChannel.setAccessibilityMessageHandler(null);
    }

    public boolean isAccessibilityEnabled() {
        return this.accessibilityManager.isEnabled();
    }

    public boolean isTouchExplorationEnabled() {
        return this.accessibilityManager.isTouchExplorationEnabled();
    }

    public void setOnAccessibilityChangeListener(OnAccessibilityChangeListener listener) {
        this.onAccessibilityChangeListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendLatestAccessibilityFlagsToFlutter() {
        this.accessibilityChannel.setAccessibilityFeatures(this.accessibilityFeatureFlags);
    }

    private boolean shouldSetCollectionInfo(final SemanticsNode semanticsNode) {
        return semanticsNode.scrollChildren > 0 && (SemanticsNode.nullableHasAncestor(this.accessibilityFocusedSemanticsNode, new Predicate() { // from class: io.flutter.view.AccessibilityBridge$$ExternalSyntheticLambda0
            @Override // io.flutter.util.Predicate
            public final boolean test(Object obj) {
                return AccessibilityBridge.lambda$shouldSetCollectionInfo$0(AccessibilityBridge.SemanticsNode.this, (AccessibilityBridge.SemanticsNode) obj);
            }
        }) || !SemanticsNode.nullableHasAncestor(this.accessibilityFocusedSemanticsNode, new Predicate() { // from class: io.flutter.view.AccessibilityBridge$$ExternalSyntheticLambda1
            @Override // io.flutter.util.Predicate
            public final boolean test(Object obj) {
                boolean hasFlag;
                hasFlag = ((AccessibilityBridge.SemanticsNode) obj).hasFlag(AccessibilityBridge.Flag.HAS_IMPLICIT_SCROLLING);
                return hasFlag;
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$shouldSetCollectionInfo$0(SemanticsNode semanticsNode, SemanticsNode o) {
        return o == semanticsNode;
    }

    private void setBoldTextFlag() {
        View view = this.rootAccessibilityView;
        if (view == null || view.getResources() == null) {
            return;
        }
        int fontWeightAdjustment = this.rootAccessibilityView.getResources().getConfiguration().fontWeightAdjustment;
        boolean shouldBold = fontWeightAdjustment != Integer.MAX_VALUE && fontWeightAdjustment >= BOLD_TEXT_WEIGHT_ADJUSTMENT;
        if (shouldBold) {
            this.accessibilityFeatureFlags |= AccessibilityFeature.BOLD_TEXT.value;
        } else {
            this.accessibilityFeatureFlags &= AccessibilityFeature.BOLD_TEXT.value;
        }
        sendLatestAccessibilityFlagsToFlutter();
    }

    public AccessibilityNodeInfo obtainAccessibilityNodeInfo(View rootView, int virtualViewId) {
        return AccessibilityNodeInfo.obtain(rootView, virtualViewId);
    }

    @Override // android.view.accessibility.AccessibilityNodeProvider
    public AccessibilityNodeInfo createAccessibilityNodeInfo(int virtualViewId) {
        SemanticsNode semanticsNode;
        boolean z = true;
        setAccessibleNavigation(true);
        if (virtualViewId >= 65536) {
            return this.accessibilityViewEmbedder.createAccessibilityNodeInfo(virtualViewId);
        }
        if (virtualViewId == -1) {
            AccessibilityNodeInfo result = AccessibilityNodeInfo.obtain(this.rootAccessibilityView);
            this.rootAccessibilityView.onInitializeAccessibilityNodeInfo(result);
            if (this.flutterSemanticsTree.containsKey(0)) {
                result.addChild(this.rootAccessibilityView, 0);
            }
            return result;
        }
        SemanticsNode semanticsNode2 = this.flutterSemanticsTree.get(Integer.valueOf(virtualViewId));
        if (semanticsNode2 != null) {
            if (semanticsNode2.platformViewId == -1 || !this.platformViewsAccessibilityDelegate.usesVirtualDisplay(semanticsNode2.platformViewId)) {
                AccessibilityNodeInfo result2 = obtainAccessibilityNodeInfo(this.rootAccessibilityView, virtualViewId);
                if (Build.VERSION.SDK_INT >= 18) {
                    result2.setViewIdResourceName("");
                }
                result2.setPackageName(this.rootAccessibilityView.getContext().getPackageName());
                result2.setClassName("android.view.View");
                result2.setSource(this.rootAccessibilityView, virtualViewId);
                result2.setFocusable(semanticsNode2.isFocusable());
                SemanticsNode semanticsNode3 = this.inputFocusedSemanticsNode;
                if (semanticsNode3 != null) {
                    result2.setFocused(semanticsNode3.id == virtualViewId);
                }
                SemanticsNode semanticsNode4 = this.accessibilityFocusedSemanticsNode;
                if (semanticsNode4 != null) {
                    result2.setAccessibilityFocused(semanticsNode4.id == virtualViewId);
                }
                if (semanticsNode2.hasFlag(Flag.IS_TEXT_FIELD)) {
                    result2.setPassword(semanticsNode2.hasFlag(Flag.IS_OBSCURED));
                    if (!semanticsNode2.hasFlag(Flag.IS_READ_ONLY)) {
                        result2.setClassName("android.widget.EditText");
                    }
                    if (Build.VERSION.SDK_INT >= 18) {
                        result2.setEditable(!semanticsNode2.hasFlag(Flag.IS_READ_ONLY));
                        if (semanticsNode2.textSelectionBase != -1 && semanticsNode2.textSelectionExtent != -1) {
                            result2.setTextSelection(semanticsNode2.textSelectionBase, semanticsNode2.textSelectionExtent);
                        }
                        if (Build.VERSION.SDK_INT > 18 && (semanticsNode = this.accessibilityFocusedSemanticsNode) != null && semanticsNode.id == virtualViewId) {
                            result2.setLiveRegion(1);
                        }
                    }
                    int granularities = 0;
                    if (semanticsNode2.hasAction(Action.MOVE_CURSOR_FORWARD_BY_CHARACTER)) {
                        result2.addAction(256);
                        granularities = 0 | 1;
                    }
                    if (semanticsNode2.hasAction(Action.MOVE_CURSOR_BACKWARD_BY_CHARACTER)) {
                        result2.addAction(512);
                        granularities |= 1;
                    }
                    if (semanticsNode2.hasAction(Action.MOVE_CURSOR_FORWARD_BY_WORD)) {
                        result2.addAction(256);
                        granularities |= 2;
                    }
                    if (semanticsNode2.hasAction(Action.MOVE_CURSOR_BACKWARD_BY_WORD)) {
                        result2.addAction(512);
                        granularities |= 2;
                    }
                    result2.setMovementGranularities(granularities);
                    if (Build.VERSION.SDK_INT >= 21 && semanticsNode2.maxValueLength >= 0) {
                        int length = semanticsNode2.value == null ? 0 : semanticsNode2.value.length();
                        int i = (length - semanticsNode2.currentValueLength) + semanticsNode2.maxValueLength;
                        result2.setMaxTextLength((length - semanticsNode2.currentValueLength) + semanticsNode2.maxValueLength);
                    }
                }
                int granularities2 = Build.VERSION.SDK_INT;
                if (granularities2 > 18) {
                    if (semanticsNode2.hasAction(Action.SET_SELECTION)) {
                        result2.addAction(131072);
                    }
                    if (semanticsNode2.hasAction(Action.COPY)) {
                        result2.addAction(16384);
                    }
                    if (semanticsNode2.hasAction(Action.CUT)) {
                        result2.addAction(65536);
                    }
                    if (semanticsNode2.hasAction(Action.PASTE)) {
                        result2.addAction(32768);
                    }
                }
                if (Build.VERSION.SDK_INT >= 21 && semanticsNode2.hasAction(Action.SET_TEXT)) {
                    result2.addAction(2097152);
                }
                if (semanticsNode2.hasFlag(Flag.IS_BUTTON) || semanticsNode2.hasFlag(Flag.IS_LINK)) {
                    result2.setClassName("android.widget.Button");
                }
                if (semanticsNode2.hasFlag(Flag.IS_IMAGE)) {
                    result2.setClassName("android.widget.ImageView");
                }
                if (Build.VERSION.SDK_INT > 18 && semanticsNode2.hasAction(Action.DISMISS)) {
                    result2.setDismissable(true);
                    result2.addAction(1048576);
                }
                if (semanticsNode2.parent != null) {
                    if (semanticsNode2.id <= 0) {
                        Log.e(TAG, "Semantics node id is not > ROOT_NODE_ID.");
                    }
                    result2.setParent(this.rootAccessibilityView, semanticsNode2.parent.id);
                } else {
                    if (semanticsNode2.id != 0) {
                        Log.e(TAG, "Semantics node id does not equal ROOT_NODE_ID.");
                    }
                    result2.setParent(this.rootAccessibilityView);
                }
                if (semanticsNode2.previousNodeId != -1 && Build.VERSION.SDK_INT >= 22) {
                    result2.setTraversalAfter(this.rootAccessibilityView, semanticsNode2.previousNodeId);
                }
                Rect bounds = semanticsNode2.getGlobalRect();
                if (semanticsNode2.parent == null) {
                    result2.setBoundsInParent(bounds);
                } else {
                    Rect parentBounds = semanticsNode2.parent.getGlobalRect();
                    Rect boundsInParent = new Rect(bounds);
                    boundsInParent.offset(-parentBounds.left, -parentBounds.top);
                    result2.setBoundsInParent(boundsInParent);
                }
                Rect boundsInScreen = getBoundsInScreen(bounds);
                result2.setBoundsInScreen(boundsInScreen);
                result2.setVisibleToUser(true);
                result2.setEnabled(!semanticsNode2.hasFlag(Flag.HAS_ENABLED_STATE) || semanticsNode2.hasFlag(Flag.IS_ENABLED));
                if (semanticsNode2.hasAction(Action.TAP)) {
                    if (Build.VERSION.SDK_INT >= 21 && semanticsNode2.onTapOverride != null) {
                        result2.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, semanticsNode2.onTapOverride.hint));
                        result2.setClickable(true);
                    } else {
                        result2.addAction(16);
                        result2.setClickable(true);
                    }
                }
                if (semanticsNode2.hasAction(Action.LONG_PRESS)) {
                    if (Build.VERSION.SDK_INT >= 21 && semanticsNode2.onLongPressOverride != null) {
                        result2.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, semanticsNode2.onLongPressOverride.hint));
                        result2.setLongClickable(true);
                    } else {
                        result2.addAction(32);
                        result2.setLongClickable(true);
                    }
                }
                if (semanticsNode2.hasAction(Action.SCROLL_LEFT) || semanticsNode2.hasAction(Action.SCROLL_UP) || semanticsNode2.hasAction(Action.SCROLL_RIGHT) || semanticsNode2.hasAction(Action.SCROLL_DOWN)) {
                    result2.setScrollable(true);
                    if (semanticsNode2.hasFlag(Flag.HAS_IMPLICIT_SCROLLING)) {
                        if (semanticsNode2.hasAction(Action.SCROLL_LEFT) || semanticsNode2.hasAction(Action.SCROLL_RIGHT)) {
                            if (Build.VERSION.SDK_INT > 19 && shouldSetCollectionInfo(semanticsNode2)) {
                                result2.setCollectionInfo(AccessibilityNodeInfo.CollectionInfo.obtain(0, semanticsNode2.scrollChildren, false));
                            } else {
                                result2.setClassName("android.widget.HorizontalScrollView");
                            }
                        } else if (Build.VERSION.SDK_INT > 18 && shouldSetCollectionInfo(semanticsNode2)) {
                            result2.setCollectionInfo(AccessibilityNodeInfo.CollectionInfo.obtain(semanticsNode2.scrollChildren, 0, false));
                        } else {
                            result2.setClassName("android.widget.ScrollView");
                        }
                    }
                    if (semanticsNode2.hasAction(Action.SCROLL_LEFT) || semanticsNode2.hasAction(Action.SCROLL_UP)) {
                        result2.addAction(4096);
                    }
                    if (semanticsNode2.hasAction(Action.SCROLL_RIGHT) || semanticsNode2.hasAction(Action.SCROLL_DOWN)) {
                        result2.addAction(8192);
                    }
                }
                if (semanticsNode2.hasAction(Action.INCREASE) || semanticsNode2.hasAction(Action.DECREASE)) {
                    result2.setClassName("android.widget.SeekBar");
                    if (semanticsNode2.hasAction(Action.INCREASE)) {
                        result2.addAction(4096);
                    }
                    if (semanticsNode2.hasAction(Action.DECREASE)) {
                        result2.addAction(8192);
                    }
                }
                if (semanticsNode2.hasFlag(Flag.IS_LIVE_REGION) && Build.VERSION.SDK_INT > 18) {
                    result2.setLiveRegion(1);
                }
                if (semanticsNode2.hasFlag(Flag.IS_TEXT_FIELD)) {
                    result2.setText(semanticsNode2.getValue());
                    if (Build.VERSION.SDK_INT >= 28) {
                        result2.setHintText(semanticsNode2.getTextFieldHint());
                    }
                } else if (!semanticsNode2.hasFlag(Flag.SCOPES_ROUTE)) {
                    CharSequence content = semanticsNode2.getValueLabelHint();
                    if (Build.VERSION.SDK_INT < 28 && semanticsNode2.tooltip != null) {
                        content = ((Object) (content != null ? content : "")) + "\n" + semanticsNode2.tooltip;
                    }
                    if (content != null) {
                        result2.setContentDescription(content);
                    }
                }
                if (Build.VERSION.SDK_INT >= 28 && semanticsNode2.tooltip != null) {
                    result2.setTooltipText(semanticsNode2.tooltip);
                }
                boolean hasCheckedState = semanticsNode2.hasFlag(Flag.HAS_CHECKED_STATE);
                boolean hasToggledState = semanticsNode2.hasFlag(Flag.HAS_TOGGLED_STATE);
                if (hasCheckedState && hasToggledState) {
                    Log.e(TAG, "Expected semanticsNode to have checked state and toggled state.");
                }
                if (!hasCheckedState && !hasToggledState) {
                    z = false;
                }
                result2.setCheckable(z);
                if (!hasCheckedState) {
                    if (hasToggledState) {
                        result2.setChecked(semanticsNode2.hasFlag(Flag.IS_TOGGLED));
                        result2.setClassName("android.widget.Switch");
                    }
                } else {
                    result2.setChecked(semanticsNode2.hasFlag(Flag.IS_CHECKED));
                    if (semanticsNode2.hasFlag(Flag.IS_IN_MUTUALLY_EXCLUSIVE_GROUP)) {
                        result2.setClassName("android.widget.RadioButton");
                    } else {
                        result2.setClassName("android.widget.CheckBox");
                    }
                }
                result2.setSelected(semanticsNode2.hasFlag(Flag.IS_SELECTED));
                if (Build.VERSION.SDK_INT >= 28) {
                    result2.setHeading(semanticsNode2.hasFlag(Flag.IS_HEADER));
                }
                SemanticsNode semanticsNode5 = this.accessibilityFocusedSemanticsNode;
                if (semanticsNode5 != null && semanticsNode5.id == virtualViewId) {
                    result2.addAction(128);
                } else {
                    result2.addAction(64);
                }
                if (Build.VERSION.SDK_INT >= 21 && semanticsNode2.customAccessibilityActions != null) {
                    for (CustomAccessibilityAction action : semanticsNode2.customAccessibilityActions) {
                        result2.addAction(new AccessibilityNodeInfo.AccessibilityAction(action.resourceId, action.label));
                    }
                }
                for (SemanticsNode child : semanticsNode2.childrenInTraversalOrder) {
                    if (!child.hasFlag(Flag.IS_HIDDEN)) {
                        if (child.platformViewId != -1) {
                            View embeddedView = this.platformViewsAccessibilityDelegate.getPlatformViewById(child.platformViewId);
                            if (!this.platformViewsAccessibilityDelegate.usesVirtualDisplay(child.platformViewId)) {
                                result2.addChild(embeddedView);
                            }
                        }
                        View embeddedView2 = this.rootAccessibilityView;
                        result2.addChild(embeddedView2, child.id);
                    }
                }
                return result2;
            }
            View embeddedView3 = this.platformViewsAccessibilityDelegate.getPlatformViewById(semanticsNode2.platformViewId);
            if (embeddedView3 == null) {
                return null;
            }
            return this.accessibilityViewEmbedder.getRootNode(embeddedView3, semanticsNode2.id, semanticsNode2.getGlobalRect());
        }
        return null;
    }

    private Rect getBoundsInScreen(Rect bounds) {
        Rect boundsInScreen = new Rect(bounds);
        int[] locationOnScreen = new int[2];
        this.rootAccessibilityView.getLocationOnScreen(locationOnScreen);
        boundsInScreen.offset(locationOnScreen[0], locationOnScreen[1]);
        return boundsInScreen;
    }

    @Override // android.view.accessibility.AccessibilityNodeProvider
    public boolean performAction(int virtualViewId, int accessibilityAction, Bundle arguments) {
        if (virtualViewId >= 65536) {
            boolean didPerform = this.accessibilityViewEmbedder.performAction(virtualViewId, accessibilityAction, arguments);
            if (didPerform && accessibilityAction == 128) {
                this.embeddedAccessibilityFocusedNodeId = null;
            }
            return didPerform;
        }
        SemanticsNode semanticsNode = this.flutterSemanticsTree.get(Integer.valueOf(virtualViewId));
        boolean hasSelection = false;
        if (semanticsNode == null) {
            return false;
        }
        switch (accessibilityAction) {
            case 16:
                this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.TAP);
                return true;
            case 32:
                this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.LONG_PRESS);
                return true;
            case 64:
                if (this.accessibilityFocusedSemanticsNode == null) {
                    this.rootAccessibilityView.invalidate();
                }
                this.accessibilityFocusedSemanticsNode = semanticsNode;
                this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.DID_GAIN_ACCESSIBILITY_FOCUS);
                sendAccessibilityEvent(virtualViewId, 32768);
                if (semanticsNode.hasAction(Action.INCREASE) || semanticsNode.hasAction(Action.DECREASE)) {
                    sendAccessibilityEvent(virtualViewId, 4);
                }
                return true;
            case 128:
                SemanticsNode semanticsNode2 = this.accessibilityFocusedSemanticsNode;
                if (semanticsNode2 != null && semanticsNode2.id == virtualViewId) {
                    this.accessibilityFocusedSemanticsNode = null;
                }
                Integer num = this.embeddedAccessibilityFocusedNodeId;
                if (num != null && num.intValue() == virtualViewId) {
                    this.embeddedAccessibilityFocusedNodeId = null;
                }
                this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.DID_LOSE_ACCESSIBILITY_FOCUS);
                sendAccessibilityEvent(virtualViewId, 65536);
                return true;
            case 256:
                if (Build.VERSION.SDK_INT < 18) {
                    return false;
                }
                return performCursorMoveAction(semanticsNode, virtualViewId, arguments, true);
            case 512:
                if (Build.VERSION.SDK_INT < 18) {
                    return false;
                }
                return performCursorMoveAction(semanticsNode, virtualViewId, arguments, false);
            case 4096:
                if (semanticsNode.hasAction(Action.SCROLL_UP)) {
                    this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.SCROLL_UP);
                } else if (semanticsNode.hasAction(Action.SCROLL_LEFT)) {
                    this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.SCROLL_LEFT);
                } else if (!semanticsNode.hasAction(Action.INCREASE)) {
                    return false;
                } else {
                    semanticsNode.value = semanticsNode.increasedValue;
                    semanticsNode.valueAttributes = semanticsNode.increasedValueAttributes;
                    sendAccessibilityEvent(virtualViewId, 4);
                    this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.INCREASE);
                }
                return true;
            case 8192:
                if (semanticsNode.hasAction(Action.SCROLL_DOWN)) {
                    this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.SCROLL_DOWN);
                } else if (semanticsNode.hasAction(Action.SCROLL_RIGHT)) {
                    this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.SCROLL_RIGHT);
                } else if (!semanticsNode.hasAction(Action.DECREASE)) {
                    return false;
                } else {
                    semanticsNode.value = semanticsNode.decreasedValue;
                    semanticsNode.valueAttributes = semanticsNode.decreasedValueAttributes;
                    sendAccessibilityEvent(virtualViewId, 4);
                    this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.DECREASE);
                }
                return true;
            case 16384:
                this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.COPY);
                return true;
            case 32768:
                this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.PASTE);
                return true;
            case 65536:
                this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.CUT);
                return true;
            case 131072:
                if (Build.VERSION.SDK_INT < 18) {
                    return false;
                }
                Map<String, Integer> selection = new HashMap<>();
                if (arguments != null && arguments.containsKey(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SELECTION_START_INT) && arguments.containsKey(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SELECTION_END_INT)) {
                    hasSelection = true;
                }
                if (hasSelection) {
                    selection.put("base", Integer.valueOf(arguments.getInt(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SELECTION_START_INT)));
                    selection.put("extent", Integer.valueOf(arguments.getInt(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SELECTION_END_INT)));
                } else {
                    selection.put("base", Integer.valueOf(semanticsNode.textSelectionExtent));
                    selection.put("extent", Integer.valueOf(semanticsNode.textSelectionExtent));
                }
                this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.SET_SELECTION, selection);
                SemanticsNode node = this.flutterSemanticsTree.get(Integer.valueOf(virtualViewId));
                node.textSelectionBase = selection.get("base").intValue();
                node.textSelectionExtent = selection.get("extent").intValue();
                return true;
            case 1048576:
                this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.DISMISS);
                return true;
            case 2097152:
                if (Build.VERSION.SDK_INT < 21) {
                    return false;
                }
                return performSetText(semanticsNode, virtualViewId, arguments);
            case ACTION_SHOW_ON_SCREEN /* 16908342 */:
                this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.SHOW_ON_SCREEN);
                return true;
            default:
                int flutterId = accessibilityAction - FIRST_RESOURCE_ID;
                CustomAccessibilityAction contextAction = this.customAccessibilityActions.get(Integer.valueOf(flutterId));
                if (contextAction == null) {
                    return false;
                }
                this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.CUSTOM_ACTION, Integer.valueOf(contextAction.id));
                return true;
        }
    }

    private boolean performCursorMoveAction(SemanticsNode semanticsNode, int virtualViewId, Bundle arguments, boolean forward) {
        int granularity = arguments.getInt(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT);
        boolean extendSelection = arguments.getBoolean(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN);
        int previousTextSelectionBase = semanticsNode.textSelectionBase;
        int previousTextSelectionExtent = semanticsNode.textSelectionExtent;
        predictCursorMovement(semanticsNode, granularity, forward, extendSelection);
        if (previousTextSelectionBase != semanticsNode.textSelectionBase || previousTextSelectionExtent != semanticsNode.textSelectionExtent) {
            String value = semanticsNode.value != null ? semanticsNode.value : "";
            AccessibilityEvent selectionEvent = obtainAccessibilityEvent(semanticsNode.id, 8192);
            selectionEvent.getText().add(value);
            selectionEvent.setFromIndex(semanticsNode.textSelectionBase);
            selectionEvent.setToIndex(semanticsNode.textSelectionExtent);
            selectionEvent.setItemCount(value.length());
            sendAccessibilityEvent(selectionEvent);
        }
        switch (granularity) {
            case 1:
                if (forward && semanticsNode.hasAction(Action.MOVE_CURSOR_FORWARD_BY_CHARACTER)) {
                    this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.MOVE_CURSOR_FORWARD_BY_CHARACTER, Boolean.valueOf(extendSelection));
                    return true;
                } else if (!forward && semanticsNode.hasAction(Action.MOVE_CURSOR_BACKWARD_BY_CHARACTER)) {
                    this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.MOVE_CURSOR_BACKWARD_BY_CHARACTER, Boolean.valueOf(extendSelection));
                    return true;
                } else {
                    return false;
                }
            case 2:
                if (forward && semanticsNode.hasAction(Action.MOVE_CURSOR_FORWARD_BY_WORD)) {
                    this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.MOVE_CURSOR_FORWARD_BY_WORD, Boolean.valueOf(extendSelection));
                    return true;
                } else if (!forward && semanticsNode.hasAction(Action.MOVE_CURSOR_BACKWARD_BY_WORD)) {
                    this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.MOVE_CURSOR_BACKWARD_BY_WORD, Boolean.valueOf(extendSelection));
                    return true;
                } else {
                    return false;
                }
            case 4:
            case 8:
            case 16:
                return true;
            default:
                return false;
        }
    }

    private void predictCursorMovement(SemanticsNode node, int granularity, boolean forward, boolean extendSelection) {
        if (node.textSelectionExtent < 0 || node.textSelectionBase < 0) {
            return;
        }
        switch (granularity) {
            case 1:
                if (forward && node.textSelectionExtent < node.value.length()) {
                    SemanticsNode.access$2212(node, 1);
                    break;
                } else if (!forward && node.textSelectionExtent > 0) {
                    SemanticsNode.access$2220(node, 1);
                    break;
                }
                break;
            case 2:
                if (forward && node.textSelectionExtent < node.value.length()) {
                    Pattern pattern = Pattern.compile("\\p{L}(\\b)");
                    Matcher result = pattern.matcher(node.value.substring(node.textSelectionExtent));
                    result.find();
                    if (result.find()) {
                        SemanticsNode.access$2212(node, result.start(1));
                        break;
                    } else {
                        node.textSelectionExtent = node.value.length();
                        break;
                    }
                } else if (!forward && node.textSelectionExtent > 0) {
                    Pattern pattern2 = Pattern.compile("(?s:.*)(\\b)\\p{L}");
                    Matcher result2 = pattern2.matcher(node.value.substring(0, node.textSelectionExtent));
                    if (result2.find()) {
                        node.textSelectionExtent = result2.start(1);
                        break;
                    }
                }
                break;
            case 4:
                if (forward && node.textSelectionExtent < node.value.length()) {
                    Pattern pattern3 = Pattern.compile("(?!^)(\\n)");
                    Matcher result3 = pattern3.matcher(node.value.substring(node.textSelectionExtent));
                    if (result3.find()) {
                        SemanticsNode.access$2212(node, result3.start(1));
                        break;
                    } else {
                        node.textSelectionExtent = node.value.length();
                        break;
                    }
                } else if (!forward && node.textSelectionExtent > 0) {
                    Pattern pattern4 = Pattern.compile("(?s:.*)(\\n)");
                    Matcher result4 = pattern4.matcher(node.value.substring(0, node.textSelectionExtent));
                    if (result4.find()) {
                        node.textSelectionExtent = result4.start(1);
                        break;
                    } else {
                        node.textSelectionExtent = 0;
                        break;
                    }
                }
                break;
            case 8:
            case 16:
                if (forward) {
                    node.textSelectionExtent = node.value.length();
                    break;
                } else {
                    node.textSelectionExtent = 0;
                    break;
                }
        }
        if (extendSelection) {
            return;
        }
        node.textSelectionBase = node.textSelectionExtent;
    }

    private boolean performSetText(SemanticsNode node, int virtualViewId, Bundle arguments) {
        String newText = "";
        if (arguments != null && arguments.containsKey(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE)) {
            newText = arguments.getString(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE);
        }
        this.accessibilityChannel.dispatchSemanticsAction(virtualViewId, Action.SET_TEXT, newText);
        node.value = newText;
        node.valueAttributes = null;
        return true;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.accessibility.AccessibilityNodeProvider
    public AccessibilityNodeInfo findFocus(int focus) {
        switch (focus) {
            case 1:
                SemanticsNode semanticsNode = this.inputFocusedSemanticsNode;
                if (semanticsNode == null) {
                    Integer num = this.embeddedInputFocusedNodeId;
                    if (num != null) {
                        return createAccessibilityNodeInfo(num.intValue());
                    }
                } else {
                    return createAccessibilityNodeInfo(semanticsNode.id);
                }
                break;
            case 2:
                break;
            default:
                return null;
        }
        SemanticsNode semanticsNode2 = this.accessibilityFocusedSemanticsNode;
        if (semanticsNode2 == null) {
            Integer num2 = this.embeddedAccessibilityFocusedNodeId;
            if (num2 != null) {
                return createAccessibilityNodeInfo(num2.intValue());
            }
            return null;
        }
        return createAccessibilityNodeInfo(semanticsNode2.id);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SemanticsNode getRootSemanticsNode() {
        if (!this.flutterSemanticsTree.containsKey(0)) {
            Log.e(TAG, "Attempted to getRootSemanticsNode without a root semantics node.");
        }
        return this.flutterSemanticsTree.get(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SemanticsNode getOrCreateSemanticsNode(int id) {
        SemanticsNode semanticsNode = this.flutterSemanticsTree.get(Integer.valueOf(id));
        if (semanticsNode == null) {
            SemanticsNode semanticsNode2 = new SemanticsNode(this);
            semanticsNode2.id = id;
            this.flutterSemanticsTree.put(Integer.valueOf(id), semanticsNode2);
            return semanticsNode2;
        }
        return semanticsNode;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CustomAccessibilityAction getOrCreateAccessibilityAction(int id) {
        CustomAccessibilityAction action = this.customAccessibilityActions.get(Integer.valueOf(id));
        if (action == null) {
            CustomAccessibilityAction action2 = new CustomAccessibilityAction();
            action2.id = id;
            action2.resourceId = FIRST_RESOURCE_ID + id;
            this.customAccessibilityActions.put(Integer.valueOf(id), action2);
            return action2;
        }
        return action;
    }

    public boolean onAccessibilityHoverEvent(MotionEvent event) {
        return onAccessibilityHoverEvent(event, false);
    }

    public boolean onAccessibilityHoverEvent(MotionEvent event, boolean ignorePlatformViews) {
        if (this.accessibilityManager.isTouchExplorationEnabled() && !this.flutterSemanticsTree.isEmpty()) {
            SemanticsNode semanticsNodeUnderCursor = getRootSemanticsNode().hitTest(new float[]{event.getX(), event.getY(), 0.0f, 1.0f}, ignorePlatformViews);
            if (semanticsNodeUnderCursor != null && semanticsNodeUnderCursor.platformViewId != -1) {
                if (ignorePlatformViews) {
                    return false;
                }
                return this.accessibilityViewEmbedder.onAccessibilityHoverEvent(semanticsNodeUnderCursor.id, event);
            }
            if (event.getAction() == 9 || event.getAction() == 7) {
                handleTouchExploration(event.getX(), event.getY(), ignorePlatformViews);
            } else if (event.getAction() == 10) {
                onTouchExplorationExit();
            } else {
                Log.d("flutter", "unexpected accessibility hover event: " + event);
                return false;
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTouchExplorationExit() {
        SemanticsNode semanticsNode = this.hoveredObject;
        if (semanticsNode == null) {
            return;
        }
        sendAccessibilityEvent(semanticsNode.id, 256);
        this.hoveredObject = null;
    }

    private void handleTouchExploration(float x, float y, boolean ignorePlatformViews) {
        SemanticsNode semanticsNodeUnderCursor;
        if (!this.flutterSemanticsTree.isEmpty() && (semanticsNodeUnderCursor = getRootSemanticsNode().hitTest(new float[]{x, y, 0.0f, 1.0f}, ignorePlatformViews)) != this.hoveredObject) {
            if (semanticsNodeUnderCursor != null) {
                sendAccessibilityEvent(semanticsNodeUnderCursor.id, 128);
            }
            SemanticsNode semanticsNode = this.hoveredObject;
            if (semanticsNode != null) {
                sendAccessibilityEvent(semanticsNode.id, 256);
            }
            this.hoveredObject = semanticsNodeUnderCursor;
        }
    }

    void updateCustomAccessibilityActions(ByteBuffer buffer, String[] strings) {
        while (buffer.hasRemaining()) {
            int id = buffer.getInt();
            CustomAccessibilityAction action = getOrCreateAccessibilityAction(id);
            action.overrideId = buffer.getInt();
            int stringIndex = buffer.getInt();
            String str = null;
            action.label = stringIndex == -1 ? null : strings[stringIndex];
            int stringIndex2 = buffer.getInt();
            if (stringIndex2 != -1) {
                str = strings[stringIndex2];
            }
            action.hint = str;
        }
    }

    void updateSemantics(ByteBuffer buffer, String[] strings, ByteBuffer[] stringAttributeArgs) {
        ArrayList<SemanticsNode> updated;
        Set<SemanticsNode> visitedObjects;
        Iterator<SemanticsNode> it;
        SemanticsNode semanticsNode;
        SemanticsNode semanticsNode2;
        float max;
        float position;
        WindowInsets insets;
        View embeddedView;
        ArrayList<SemanticsNode> updated2 = new ArrayList<>();
        while (buffer.hasRemaining()) {
            int id = buffer.getInt();
            SemanticsNode semanticsNode3 = getOrCreateSemanticsNode(id);
            semanticsNode3.updateWith(buffer, strings, stringAttributeArgs);
            if (!semanticsNode3.hasFlag(Flag.IS_HIDDEN)) {
                if (semanticsNode3.hasFlag(Flag.IS_FOCUSED)) {
                    this.inputFocusedSemanticsNode = semanticsNode3;
                }
                if (semanticsNode3.hadPreviousConfig) {
                    updated2.add(semanticsNode3);
                }
                if (semanticsNode3.platformViewId != -1 && !this.platformViewsAccessibilityDelegate.usesVirtualDisplay(semanticsNode3.platformViewId) && (embeddedView = this.platformViewsAccessibilityDelegate.getPlatformViewById(semanticsNode3.platformViewId)) != null) {
                    embeddedView.setImportantForAccessibility(0);
                }
            }
        }
        Set<SemanticsNode> visitedObjects2 = new HashSet<>();
        SemanticsNode rootObject = getRootSemanticsNode();
        List<SemanticsNode> newRoutes = new ArrayList<>();
        if (rootObject != null) {
            float[] identity = new float[16];
            Matrix.setIdentityM(identity, 0);
            if (Build.VERSION.SDK_INT >= 23) {
                boolean needsToApplyLeftCutoutInset = true;
                if (Build.VERSION.SDK_INT >= 28) {
                    needsToApplyLeftCutoutInset = doesLayoutInDisplayCutoutModeRequireLeftInset();
                }
                if (needsToApplyLeftCutoutInset && (insets = this.rootAccessibilityView.getRootWindowInsets()) != null) {
                    if (!this.lastLeftFrameInset.equals(Integer.valueOf(insets.getSystemWindowInsetLeft()))) {
                        rootObject.globalGeometryDirty = true;
                        rootObject.inverseTransformDirty = true;
                    }
                    Integer valueOf = Integer.valueOf(insets.getSystemWindowInsetLeft());
                    this.lastLeftFrameInset = valueOf;
                    Matrix.translateM(identity, 0, valueOf.intValue(), 0.0f, 0.0f);
                }
            }
            rootObject.updateRecursively(identity, visitedObjects2, false);
            rootObject.collectRoutes(newRoutes);
        }
        SemanticsNode lastAdded = null;
        for (SemanticsNode semanticsNode4 : newRoutes) {
            if (!this.flutterNavigationStack.contains(Integer.valueOf(semanticsNode4.id))) {
                lastAdded = semanticsNode4;
            }
        }
        if (lastAdded == null && newRoutes.size() > 0) {
            SemanticsNode lastAdded2 = newRoutes.get(newRoutes.size() - 1);
            lastAdded = lastAdded2;
        }
        if (lastAdded != null && (lastAdded.id != this.previousRouteId || newRoutes.size() != this.flutterNavigationStack.size())) {
            this.previousRouteId = lastAdded.id;
            onWindowNameChange(lastAdded);
        }
        this.flutterNavigationStack.clear();
        for (SemanticsNode semanticsNode5 : newRoutes) {
            this.flutterNavigationStack.add(Integer.valueOf(semanticsNode5.id));
        }
        Iterator<Map.Entry<Integer, SemanticsNode>> it2 = this.flutterSemanticsTree.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry<Integer, SemanticsNode> entry = it2.next();
            SemanticsNode object = entry.getValue();
            if (!visitedObjects2.contains(object)) {
                willRemoveSemanticsNode(object);
                it2.remove();
            }
        }
        sendWindowContentChangeEvent(0);
        Iterator<SemanticsNode> it3 = updated2.iterator();
        while (it3.hasNext()) {
            SemanticsNode object2 = it3.next();
            if (object2.didScroll()) {
                AccessibilityEvent event = obtainAccessibilityEvent(object2.id, 4096);
                float position2 = object2.scrollPosition;
                float max2 = object2.scrollExtentMax;
                if (Float.isInfinite(object2.scrollExtentMax)) {
                    max2 = SCROLL_EXTENT_FOR_INFINITY;
                    if (position2 > SCROLL_POSITION_CAP_FOR_INFINITY) {
                        position2 = SCROLL_POSITION_CAP_FOR_INFINITY;
                    }
                }
                if (Float.isInfinite(object2.scrollExtentMin)) {
                    max = max2 + SCROLL_EXTENT_FOR_INFINITY;
                    if (position2 < -70000.0f) {
                        position2 = -70000.0f;
                    }
                    position = position2 + SCROLL_EXTENT_FOR_INFINITY;
                } else {
                    max = max2 - object2.scrollExtentMin;
                    position = position2 - object2.scrollExtentMin;
                }
                if (object2.hadAction(Action.SCROLL_UP) || object2.hadAction(Action.SCROLL_DOWN)) {
                    event.setScrollY((int) position);
                    event.setMaxScrollY((int) max);
                } else if (object2.hadAction(Action.SCROLL_LEFT) || object2.hadAction(Action.SCROLL_RIGHT)) {
                    event.setScrollX((int) position);
                    event.setMaxScrollX((int) max);
                }
                if (object2.scrollChildren <= 0) {
                    updated = updated2;
                    visitedObjects = visitedObjects2;
                    it = it3;
                } else {
                    event.setItemCount(object2.scrollChildren);
                    event.setFromIndex(object2.scrollIndex);
                    int visibleChildren = 0;
                    for (SemanticsNode child : object2.childrenInHitTestOrder) {
                        ArrayList<SemanticsNode> updated3 = updated2;
                        Set<SemanticsNode> visitedObjects3 = visitedObjects2;
                        if (!child.hasFlag(Flag.IS_HIDDEN)) {
                            visibleChildren++;
                        }
                        visitedObjects2 = visitedObjects3;
                        updated2 = updated3;
                    }
                    updated = updated2;
                    visitedObjects = visitedObjects2;
                    it = it3;
                    if (object2.scrollIndex + visibleChildren > object2.scrollChildren) {
                        Log.e(TAG, "Scroll index is out of bounds.");
                    }
                    if (object2.childrenInHitTestOrder.isEmpty()) {
                        Log.e(TAG, "Had scrollChildren but no childrenInHitTestOrder");
                    }
                    event.setToIndex((object2.scrollIndex + visibleChildren) - 1);
                }
                sendAccessibilityEvent(event);
            } else {
                updated = updated2;
                visitedObjects = visitedObjects2;
                it = it3;
            }
            if (object2.hasFlag(Flag.IS_LIVE_REGION) && object2.didChangeLabel()) {
                sendWindowContentChangeEvent(object2.id);
            }
            SemanticsNode semanticsNode6 = this.accessibilityFocusedSemanticsNode;
            if (semanticsNode6 != null && semanticsNode6.id == object2.id && !object2.hadFlag(Flag.IS_SELECTED) && object2.hasFlag(Flag.IS_SELECTED)) {
                AccessibilityEvent event2 = obtainAccessibilityEvent(object2.id, 4);
                event2.getText().add(object2.label);
                sendAccessibilityEvent(event2);
            }
            SemanticsNode semanticsNode7 = this.inputFocusedSemanticsNode;
            if (semanticsNode7 != null && semanticsNode7.id == object2.id && ((semanticsNode2 = this.lastInputFocusedSemanticsNode) == null || semanticsNode2.id != this.inputFocusedSemanticsNode.id)) {
                this.lastInputFocusedSemanticsNode = this.inputFocusedSemanticsNode;
                sendAccessibilityEvent(obtainAccessibilityEvent(object2.id, 8));
            } else if (this.inputFocusedSemanticsNode == null) {
                this.lastInputFocusedSemanticsNode = null;
            }
            SemanticsNode semanticsNode8 = this.inputFocusedSemanticsNode;
            if (semanticsNode8 != null && semanticsNode8.id == object2.id && object2.hadFlag(Flag.IS_TEXT_FIELD) && object2.hasFlag(Flag.IS_TEXT_FIELD) && ((semanticsNode = this.accessibilityFocusedSemanticsNode) == null || semanticsNode.id == this.inputFocusedSemanticsNode.id)) {
                String oldValue = object2.previousValue != null ? object2.previousValue : "";
                String newValue = object2.value != null ? object2.value : "";
                AccessibilityEvent event3 = createTextChangedEvent(object2.id, oldValue, newValue);
                if (event3 != null) {
                    sendAccessibilityEvent(event3);
                }
                if (object2.previousTextSelectionBase != object2.textSelectionBase || object2.previousTextSelectionExtent != object2.textSelectionExtent) {
                    AccessibilityEvent selectionEvent = obtainAccessibilityEvent(object2.id, 8192);
                    selectionEvent.getText().add(newValue);
                    selectionEvent.setFromIndex(object2.textSelectionBase);
                    selectionEvent.setToIndex(object2.textSelectionExtent);
                    selectionEvent.setItemCount(newValue.length());
                    sendAccessibilityEvent(selectionEvent);
                }
            }
            it3 = it;
            visitedObjects2 = visitedObjects;
            updated2 = updated;
        }
    }

    private AccessibilityEvent createTextChangedEvent(int id, String oldValue, String newValue) {
        AccessibilityEvent e = obtainAccessibilityEvent(id, 16);
        e.setBeforeText(oldValue);
        e.getText().add(newValue);
        int i = 0;
        while (i < oldValue.length() && i < newValue.length() && oldValue.charAt(i) == newValue.charAt(i)) {
            i++;
        }
        if (i >= oldValue.length() && i >= newValue.length()) {
            return null;
        }
        int firstDifference = i;
        e.setFromIndex(firstDifference);
        int oldIndex = oldValue.length() - 1;
        int newIndex = newValue.length() - 1;
        while (oldIndex >= firstDifference && newIndex >= firstDifference && oldValue.charAt(oldIndex) == newValue.charAt(newIndex)) {
            oldIndex--;
            newIndex--;
        }
        e.setRemovedCount((oldIndex - firstDifference) + 1);
        e.setAddedCount((newIndex - firstDifference) + 1);
        return e;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendAccessibilityEvent(int viewId, int eventType) {
        if (!this.accessibilityManager.isEnabled()) {
            return;
        }
        sendAccessibilityEvent(obtainAccessibilityEvent(viewId, eventType));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendAccessibilityEvent(AccessibilityEvent event) {
        if (!this.accessibilityManager.isEnabled()) {
            return;
        }
        this.rootAccessibilityView.getParent().requestSendAccessibilityEvent(this.rootAccessibilityView, event);
    }

    private void onWindowNameChange(SemanticsNode route) {
        String routeName = route.getRouteName();
        if (routeName == null) {
            routeName = " ";
        }
        if (Build.VERSION.SDK_INT >= 28) {
            setAccessibilityPaneTitle(routeName);
            return;
        }
        AccessibilityEvent event = obtainAccessibilityEvent(route.id, 32);
        event.getText().add(routeName);
        sendAccessibilityEvent(event);
    }

    private void setAccessibilityPaneTitle(String title) {
        this.rootAccessibilityView.setAccessibilityPaneTitle(title);
    }

    private void sendWindowContentChangeEvent(int virtualViewId) {
        AccessibilityEvent event = obtainAccessibilityEvent(virtualViewId, 2048);
        if (Build.VERSION.SDK_INT >= 19) {
            event.setContentChangeTypes(1);
        }
        sendAccessibilityEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AccessibilityEvent obtainAccessibilityEvent(int virtualViewId, int eventType) {
        AccessibilityEvent event = AccessibilityEvent.obtain(eventType);
        event.setPackageName(this.rootAccessibilityView.getContext().getPackageName());
        event.setSource(this.rootAccessibilityView, virtualViewId);
        return event;
    }

    private boolean doesLayoutInDisplayCutoutModeRequireLeftInset() {
        Context context = this.rootAccessibilityView.getContext();
        Activity activity = ViewUtils.getActivity(context);
        if (activity == null || activity.getWindow() == null) {
            return false;
        }
        int layoutInDisplayCutoutMode = activity.getWindow().getAttributes().layoutInDisplayCutoutMode;
        return layoutInDisplayCutoutMode == 2 || layoutInDisplayCutoutMode == 0;
    }

    private void willRemoveSemanticsNode(SemanticsNode semanticsNodeToBeRemoved) {
        View embeddedView;
        Integer num;
        if (!this.flutterSemanticsTree.containsKey(Integer.valueOf(semanticsNodeToBeRemoved.id))) {
            Log.e(TAG, "Attempted to remove a node that is not in the tree.");
        }
        if (this.flutterSemanticsTree.get(Integer.valueOf(semanticsNodeToBeRemoved.id)) != semanticsNodeToBeRemoved) {
            Log.e(TAG, "Flutter semantics tree failed to get expected node when searching by id.");
        }
        semanticsNodeToBeRemoved.parent = null;
        if (semanticsNodeToBeRemoved.platformViewId != -1 && (num = this.embeddedAccessibilityFocusedNodeId) != null && this.accessibilityViewEmbedder.platformViewOfNode(num.intValue()) == this.platformViewsAccessibilityDelegate.getPlatformViewById(semanticsNodeToBeRemoved.platformViewId)) {
            sendAccessibilityEvent(this.embeddedAccessibilityFocusedNodeId.intValue(), 65536);
            this.embeddedAccessibilityFocusedNodeId = null;
        }
        if (semanticsNodeToBeRemoved.platformViewId != -1 && (embeddedView = this.platformViewsAccessibilityDelegate.getPlatformViewById(semanticsNodeToBeRemoved.platformViewId)) != null) {
            embeddedView.setImportantForAccessibility(4);
        }
        SemanticsNode semanticsNode = this.accessibilityFocusedSemanticsNode;
        if (semanticsNode == semanticsNodeToBeRemoved) {
            sendAccessibilityEvent(semanticsNode.id, 65536);
            this.accessibilityFocusedSemanticsNode = null;
        }
        if (this.inputFocusedSemanticsNode == semanticsNodeToBeRemoved) {
            this.inputFocusedSemanticsNode = null;
        }
        if (this.hoveredObject == semanticsNodeToBeRemoved) {
            this.hoveredObject = null;
        }
    }

    public void reset() {
        this.flutterSemanticsTree.clear();
        SemanticsNode semanticsNode = this.accessibilityFocusedSemanticsNode;
        if (semanticsNode != null) {
            sendAccessibilityEvent(semanticsNode.id, 65536);
        }
        this.accessibilityFocusedSemanticsNode = null;
        this.hoveredObject = null;
        sendWindowContentChangeEvent(0);
    }

    /* loaded from: classes.dex */
    public enum Action {
        TAP(1),
        LONG_PRESS(2),
        SCROLL_LEFT(4),
        SCROLL_RIGHT(8),
        SCROLL_UP(16),
        SCROLL_DOWN(32),
        INCREASE(64),
        DECREASE(128),
        SHOW_ON_SCREEN(256),
        MOVE_CURSOR_FORWARD_BY_CHARACTER(512),
        MOVE_CURSOR_BACKWARD_BY_CHARACTER(1024),
        SET_SELECTION(2048),
        COPY(4096),
        CUT(8192),
        PASTE(16384),
        DID_GAIN_ACCESSIBILITY_FOCUS(32768),
        DID_LOSE_ACCESSIBILITY_FOCUS(65536),
        CUSTOM_ACTION(131072),
        DISMISS(262144),
        MOVE_CURSOR_FORWARD_BY_WORD(524288),
        MOVE_CURSOR_BACKWARD_BY_WORD(1048576),
        SET_TEXT(2097152);
        
        public final int value;

        Action(int value) {
            this.value = value;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum Flag {
        HAS_CHECKED_STATE(1),
        IS_CHECKED(2),
        IS_SELECTED(4),
        IS_BUTTON(8),
        IS_TEXT_FIELD(16),
        IS_FOCUSED(32),
        HAS_ENABLED_STATE(64),
        IS_ENABLED(128),
        IS_IN_MUTUALLY_EXCLUSIVE_GROUP(256),
        IS_HEADER(512),
        IS_OBSCURED(1024),
        SCOPES_ROUTE(2048),
        NAMES_ROUTE(4096),
        IS_HIDDEN(8192),
        IS_IMAGE(16384),
        IS_LIVE_REGION(32768),
        HAS_TOGGLED_STATE(65536),
        IS_TOGGLED(131072),
        HAS_IMPLICIT_SCROLLING(262144),
        IS_MULTILINE(524288),
        IS_READ_ONLY(1048576),
        IS_FOCUSABLE(2097152),
        IS_LINK(4194304),
        IS_SLIDER(8388608),
        IS_KEYBOARD_KEY(16777216),
        IS_CHECK_STATE_MIXED(33554432);
        
        final int value;

        Flag(int value) {
            this.value = value;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum AccessibilityFeature {
        ACCESSIBLE_NAVIGATION(1),
        INVERT_COLORS(2),
        DISABLE_ANIMATIONS(4),
        BOLD_TEXT(8),
        REDUCE_MOTION(16),
        HIGH_CONTRAST(32),
        ON_OFF_SWITCH_LABELS(64);
        
        final int value;

        AccessibilityFeature(int value) {
            this.value = value;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum TextDirection {
        UNKNOWN,
        LTR,
        RTL;

        public static TextDirection fromInt(int value) {
            switch (value) {
                case 1:
                    return RTL;
                case 2:
                    return LTR;
                default:
                    return UNKNOWN;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CustomAccessibilityAction {
        private String hint;
        private String label;
        private int resourceId = -1;
        private int id = -1;
        private int overrideId = -1;

        CustomAccessibilityAction() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class StringAttribute {
        int end;
        int start;
        StringAttributeType type;

        private StringAttribute() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SpellOutStringAttribute extends StringAttribute {
        private SpellOutStringAttribute() {
            super();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class LocaleStringAttribute extends StringAttribute {
        String locale;

        private LocaleStringAttribute() {
            super();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SemanticsNode {
        final AccessibilityBridge accessibilityBridge;
        private int actions;
        private float bottom;
        private int currentValueLength;
        private List<CustomAccessibilityAction> customAccessibilityActions;
        private String decreasedValue;
        private List<StringAttribute> decreasedValueAttributes;
        private int flags;
        private Rect globalRect;
        private float[] globalTransform;
        private String hint;
        private List<StringAttribute> hintAttributes;
        private String increasedValue;
        private List<StringAttribute> increasedValueAttributes;
        private float[] inverseTransform;
        private String label;
        private List<StringAttribute> labelAttributes;
        private float left;
        private int maxValueLength;
        private CustomAccessibilityAction onLongPressOverride;
        private CustomAccessibilityAction onTapOverride;
        private SemanticsNode parent;
        private int platformViewId;
        private int previousActions;
        private int previousFlags;
        private String previousLabel;
        private float previousScrollExtentMax;
        private float previousScrollExtentMin;
        private float previousScrollPosition;
        private int previousTextSelectionBase;
        private int previousTextSelectionExtent;
        private String previousValue;
        private float right;
        private int scrollChildren;
        private float scrollExtentMax;
        private float scrollExtentMin;
        private int scrollIndex;
        private float scrollPosition;
        private TextDirection textDirection;
        private int textSelectionBase;
        private int textSelectionExtent;
        private String tooltip;
        private float top;
        private float[] transform;
        private String value;
        private List<StringAttribute> valueAttributes;
        private int id = -1;
        private int previousNodeId = -1;
        private boolean hadPreviousConfig = false;
        private List<SemanticsNode> childrenInTraversalOrder = new ArrayList();
        private List<SemanticsNode> childrenInHitTestOrder = new ArrayList();
        private boolean inverseTransformDirty = true;
        private boolean globalGeometryDirty = true;

        static /* synthetic */ int access$2212(SemanticsNode x0, int x1) {
            int i = x0.textSelectionExtent + x1;
            x0.textSelectionExtent = i;
            return i;
        }

        static /* synthetic */ int access$2220(SemanticsNode x0, int x1) {
            int i = x0.textSelectionExtent - x1;
            x0.textSelectionExtent = i;
            return i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static boolean nullableHasAncestor(SemanticsNode target, Predicate<SemanticsNode> tester) {
            return (target == null || target.getAncestor(tester) == null) ? false : true;
        }

        SemanticsNode(AccessibilityBridge accessibilityBridge) {
            this.accessibilityBridge = accessibilityBridge;
        }

        private SemanticsNode getAncestor(Predicate<SemanticsNode> tester) {
            for (SemanticsNode nextAncestor = this.parent; nextAncestor != null; nextAncestor = nextAncestor.parent) {
                if (tester.test(nextAncestor)) {
                    return nextAncestor;
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean hasAction(Action action) {
            return (this.actions & action.value) != 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean hadAction(Action action) {
            return (this.previousActions & action.value) != 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean hasFlag(Flag flag) {
            return (this.flags & flag.value) != 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean hadFlag(Flag flag) {
            if (!this.hadPreviousConfig) {
                Log.e(AccessibilityBridge.TAG, "Attempted to check hadFlag but had no previous config.");
            }
            return (this.previousFlags & flag.value) != 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean didScroll() {
            return (Float.isNaN(this.scrollPosition) || Float.isNaN(this.previousScrollPosition) || this.previousScrollPosition == this.scrollPosition) ? false : true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean didChangeLabel() {
            String str;
            String str2 = this.label;
            if (str2 == null && this.previousLabel == null) {
                return false;
            }
            return str2 == null || (str = this.previousLabel) == null || !str2.equals(str);
        }

        private void log(String indent, boolean recursive) {
            Log.i(AccessibilityBridge.TAG, indent + "SemanticsNode id=" + this.id + " label=" + this.label + " actions=" + this.actions + " flags=" + this.flags + "\n" + indent + "  +-- textDirection=" + this.textDirection + "\n" + indent + "  +-- rect.ltrb=(" + this.left + ", " + this.top + ", " + this.right + ", " + this.bottom + ")\n" + indent + "  +-- transform=" + Arrays.toString(this.transform) + "\n");
            if (recursive) {
                String childIndent = indent + "  ";
                for (SemanticsNode child : this.childrenInTraversalOrder) {
                    child.log(childIndent, recursive);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateWith(ByteBuffer buffer, String[] strings, ByteBuffer[] stringAttributeArgs) {
            this.hadPreviousConfig = true;
            this.previousValue = this.value;
            this.previousLabel = this.label;
            this.previousFlags = this.flags;
            this.previousActions = this.actions;
            this.previousTextSelectionBase = this.textSelectionBase;
            this.previousTextSelectionExtent = this.textSelectionExtent;
            this.previousScrollPosition = this.scrollPosition;
            this.previousScrollExtentMax = this.scrollExtentMax;
            this.previousScrollExtentMin = this.scrollExtentMin;
            this.flags = buffer.getInt();
            this.actions = buffer.getInt();
            this.maxValueLength = buffer.getInt();
            this.currentValueLength = buffer.getInt();
            this.textSelectionBase = buffer.getInt();
            this.textSelectionExtent = buffer.getInt();
            this.platformViewId = buffer.getInt();
            this.scrollChildren = buffer.getInt();
            this.scrollIndex = buffer.getInt();
            this.scrollPosition = buffer.getFloat();
            this.scrollExtentMax = buffer.getFloat();
            this.scrollExtentMin = buffer.getFloat();
            int stringIndex = buffer.getInt();
            this.label = stringIndex == -1 ? null : strings[stringIndex];
            this.labelAttributes = getStringAttributesFromBuffer(buffer, stringAttributeArgs);
            int stringIndex2 = buffer.getInt();
            this.value = stringIndex2 == -1 ? null : strings[stringIndex2];
            this.valueAttributes = getStringAttributesFromBuffer(buffer, stringAttributeArgs);
            int stringIndex3 = buffer.getInt();
            this.increasedValue = stringIndex3 == -1 ? null : strings[stringIndex3];
            this.increasedValueAttributes = getStringAttributesFromBuffer(buffer, stringAttributeArgs);
            int stringIndex4 = buffer.getInt();
            this.decreasedValue = stringIndex4 == -1 ? null : strings[stringIndex4];
            this.decreasedValueAttributes = getStringAttributesFromBuffer(buffer, stringAttributeArgs);
            int stringIndex5 = buffer.getInt();
            this.hint = stringIndex5 == -1 ? null : strings[stringIndex5];
            this.hintAttributes = getStringAttributesFromBuffer(buffer, stringAttributeArgs);
            int stringIndex6 = buffer.getInt();
            this.tooltip = stringIndex6 == -1 ? null : strings[stringIndex6];
            this.textDirection = TextDirection.fromInt(buffer.getInt());
            this.left = buffer.getFloat();
            this.top = buffer.getFloat();
            this.right = buffer.getFloat();
            this.bottom = buffer.getFloat();
            if (this.transform == null) {
                this.transform = new float[16];
            }
            for (int i = 0; i < 16; i++) {
                this.transform[i] = buffer.getFloat();
            }
            this.inverseTransformDirty = true;
            this.globalGeometryDirty = true;
            int childCount = buffer.getInt();
            this.childrenInTraversalOrder.clear();
            this.childrenInHitTestOrder.clear();
            for (int i2 = 0; i2 < childCount; i2++) {
                SemanticsNode child = this.accessibilityBridge.getOrCreateSemanticsNode(buffer.getInt());
                child.parent = this;
                this.childrenInTraversalOrder.add(child);
            }
            for (int i3 = 0; i3 < childCount; i3++) {
                SemanticsNode child2 = this.accessibilityBridge.getOrCreateSemanticsNode(buffer.getInt());
                child2.parent = this;
                this.childrenInHitTestOrder.add(child2);
            }
            int actionCount = buffer.getInt();
            if (actionCount == 0) {
                this.customAccessibilityActions = null;
                return;
            }
            List<CustomAccessibilityAction> list = this.customAccessibilityActions;
            if (list == null) {
                this.customAccessibilityActions = new ArrayList(actionCount);
            } else {
                list.clear();
            }
            for (int i4 = 0; i4 < actionCount; i4++) {
                CustomAccessibilityAction action = this.accessibilityBridge.getOrCreateAccessibilityAction(buffer.getInt());
                if (action.overrideId != Action.TAP.value) {
                    if (action.overrideId != Action.LONG_PRESS.value) {
                        if (action.overrideId != -1) {
                            Log.e(AccessibilityBridge.TAG, "Expected action.overrideId to be -1.");
                        }
                        this.customAccessibilityActions.add(action);
                    } else {
                        this.onLongPressOverride = action;
                    }
                } else {
                    this.onTapOverride = action;
                }
                this.customAccessibilityActions.add(action);
            }
        }

        private List<StringAttribute> getStringAttributesFromBuffer(ByteBuffer buffer, ByteBuffer[] stringAttributeArgs) {
            int attributesCount = buffer.getInt();
            if (attributesCount == -1) {
                return null;
            }
            List<StringAttribute> result = new ArrayList<>(attributesCount);
            for (int i = 0; i < attributesCount; i++) {
                int start = buffer.getInt();
                int end = buffer.getInt();
                StringAttributeType type = StringAttributeType.values()[buffer.getInt()];
                switch (AnonymousClass5.$SwitchMap$io$flutter$view$AccessibilityBridge$StringAttributeType[type.ordinal()]) {
                    case 1:
                        buffer.getInt();
                        SpellOutStringAttribute attribute = new SpellOutStringAttribute();
                        attribute.start = start;
                        attribute.end = end;
                        attribute.type = type;
                        result.add(attribute);
                        break;
                    case 2:
                        int argsIndex = buffer.getInt();
                        ByteBuffer args = stringAttributeArgs[argsIndex];
                        LocaleStringAttribute attribute2 = new LocaleStringAttribute();
                        attribute2.start = start;
                        attribute2.end = end;
                        attribute2.type = type;
                        attribute2.locale = Charset.forName("UTF-8").decode(args).toString();
                        result.add(attribute2);
                        break;
                }
            }
            return result;
        }

        private void ensureInverseTransform() {
            if (!this.inverseTransformDirty) {
                return;
            }
            this.inverseTransformDirty = false;
            if (this.inverseTransform == null) {
                this.inverseTransform = new float[16];
            }
            if (!Matrix.invertM(this.inverseTransform, 0, this.transform, 0)) {
                Arrays.fill(this.inverseTransform, 0.0f);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Rect getGlobalRect() {
            if (this.globalGeometryDirty) {
                Log.e(AccessibilityBridge.TAG, "Attempted to getGlobalRect with a dirty geometry.");
            }
            return this.globalRect;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public SemanticsNode hitTest(float[] point, boolean stopAtPlatformView) {
            float w = point[3];
            float x = point[0] / w;
            float y = point[1] / w;
            if (x < this.left || x >= this.right || y < this.top || y >= this.bottom) {
                return null;
            }
            float[] transformedPoint = new float[4];
            for (SemanticsNode child : this.childrenInHitTestOrder) {
                if (!child.hasFlag(Flag.IS_HIDDEN)) {
                    child.ensureInverseTransform();
                    Matrix.multiplyMV(transformedPoint, 0, child.inverseTransform, 0, point, 0);
                    SemanticsNode result = child.hitTest(transformedPoint, stopAtPlatformView);
                    if (result != null) {
                        return result;
                    }
                }
            }
            boolean foundPlatformView = stopAtPlatformView && this.platformViewId != -1;
            if (isFocusable() || foundPlatformView) {
                return this;
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isFocusable() {
            String str;
            String str2;
            String str3;
            if (hasFlag(Flag.SCOPES_ROUTE)) {
                return false;
            }
            return (!hasFlag(Flag.IS_FOCUSABLE) && (this.actions & (AccessibilityBridge.SCROLLABLE_ACTIONS ^ (-1))) == 0 && (this.flags & AccessibilityBridge.FOCUSABLE_FLAGS) == 0 && ((str = this.label) == null || str.isEmpty()) && (((str2 = this.value) == null || str2.isEmpty()) && ((str3 = this.hint) == null || str3.isEmpty()))) ? false : true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void collectRoutes(List<SemanticsNode> edges) {
            if (hasFlag(Flag.SCOPES_ROUTE)) {
                edges.add(this);
            }
            for (SemanticsNode child : this.childrenInTraversalOrder) {
                child.collectRoutes(edges);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getRouteName() {
            String str;
            if (hasFlag(Flag.NAMES_ROUTE) && (str = this.label) != null && !str.isEmpty()) {
                return this.label;
            }
            for (SemanticsNode child : this.childrenInTraversalOrder) {
                String newName = child.getRouteName();
                if (newName != null && !newName.isEmpty()) {
                    return newName;
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateRecursively(float[] ancestorTransform, Set<SemanticsNode> visitedObjects, boolean forceUpdate) {
            boolean forceUpdate2;
            visitedObjects.add(this);
            if (!this.globalGeometryDirty) {
                forceUpdate2 = forceUpdate;
            } else {
                forceUpdate2 = true;
            }
            if (forceUpdate2) {
                if (this.globalTransform == null) {
                    this.globalTransform = new float[16];
                }
                if (this.transform == null) {
                    Log.e(AccessibilityBridge.TAG, "transform has not been initialized for id = " + this.id);
                    this.accessibilityBridge.getRootSemanticsNode().log("Semantics tree:", true);
                    this.transform = new float[16];
                }
                Matrix.multiplyMM(this.globalTransform, 0, ancestorTransform, 0, this.transform, 0);
                float[] sample = {this.left, this.top, 0.0f, 1.0f};
                float[] point1 = new float[4];
                float[] point2 = new float[4];
                float[] point3 = new float[4];
                float[] point4 = new float[4];
                transformPoint(point1, this.globalTransform, sample);
                sample[0] = this.right;
                sample[1] = this.top;
                transformPoint(point2, this.globalTransform, sample);
                sample[0] = this.right;
                sample[1] = this.bottom;
                transformPoint(point3, this.globalTransform, sample);
                sample[0] = this.left;
                sample[1] = this.bottom;
                transformPoint(point4, this.globalTransform, sample);
                if (this.globalRect == null) {
                    this.globalRect = new Rect();
                }
                this.globalRect.set(Math.round(min(point1[0], point2[0], point3[0], point4[0])), Math.round(min(point1[1], point2[1], point3[1], point4[1])), Math.round(max(point1[0], point2[0], point3[0], point4[0])), Math.round(max(point1[1], point2[1], point3[1], point4[1])));
                this.globalGeometryDirty = false;
            }
            if (this.globalTransform == null) {
                Log.e(AccessibilityBridge.TAG, "Expected globalTransform to not be null.");
            }
            if (this.globalRect == null) {
                Log.e(AccessibilityBridge.TAG, "Expected globalRect to not be null.");
            }
            int previousNodeId = -1;
            for (SemanticsNode child : this.childrenInTraversalOrder) {
                child.previousNodeId = previousNodeId;
                previousNodeId = child.id;
                child.updateRecursively(this.globalTransform, visitedObjects, forceUpdate2);
            }
        }

        private void transformPoint(float[] result, float[] transform, float[] point) {
            Matrix.multiplyMV(result, 0, transform, 0, point, 0);
            float w = result[3];
            result[0] = result[0] / w;
            result[1] = result[1] / w;
            result[2] = result[2] / w;
            result[3] = 0.0f;
        }

        private float min(float a, float b, float c, float d) {
            return Math.min(a, Math.min(b, Math.min(c, d)));
        }

        private float max(float a, float b, float c, float d) {
            return Math.max(a, Math.max(b, Math.max(c, d)));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CharSequence getValue() {
            if (Build.VERSION.SDK_INT < 21) {
                return this.value;
            }
            return createSpannableString(this.value, this.valueAttributes);
        }

        private CharSequence getLabel() {
            if (Build.VERSION.SDK_INT < 21) {
                return this.label;
            }
            return createSpannableString(this.label, this.labelAttributes);
        }

        private CharSequence getHint() {
            if (Build.VERSION.SDK_INT < 21) {
                return this.hint;
            }
            return createSpannableString(this.hint, this.hintAttributes);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CharSequence getValueLabelHint() {
            CharSequence[] array = {getValue(), getLabel(), getHint()};
            CharSequence result = null;
            for (CharSequence word : array) {
                if (word != null && word.length() > 0) {
                    result = (result == null || result.length() == 0) ? word : TextUtils.concat(result, ", ", word);
                }
            }
            return result;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CharSequence getTextFieldHint() {
            CharSequence[] array = {getLabel(), getHint()};
            CharSequence result = null;
            for (CharSequence word : array) {
                if (word != null && word.length() > 0) {
                    result = (result == null || result.length() == 0) ? word : TextUtils.concat(result, ", ", word);
                }
            }
            return result;
        }

        private SpannableString createSpannableString(String string, List<StringAttribute> attributes) {
            if (string == null) {
                return null;
            }
            SpannableString spannableString = new SpannableString(string);
            if (attributes != null) {
                for (StringAttribute attribute : attributes) {
                    switch (AnonymousClass5.$SwitchMap$io$flutter$view$AccessibilityBridge$StringAttributeType[attribute.type.ordinal()]) {
                        case 1:
                            TtsSpan ttsSpan = new TtsSpan.Builder("android.type.verbatim").build();
                            spannableString.setSpan(ttsSpan, attribute.start, attribute.end, 0);
                            break;
                        case 2:
                            LocaleStringAttribute localeAttribute = (LocaleStringAttribute) attribute;
                            Locale locale = Locale.forLanguageTag(localeAttribute.locale);
                            LocaleSpan localeSpan = new LocaleSpan(locale);
                            spannableString.setSpan(localeSpan, attribute.start, attribute.end, 0);
                            break;
                    }
                }
            }
            return spannableString;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: io.flutter.view.AccessibilityBridge$5  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$io$flutter$view$AccessibilityBridge$StringAttributeType;

        static {
            int[] iArr = new int[StringAttributeType.values().length];
            $SwitchMap$io$flutter$view$AccessibilityBridge$StringAttributeType = iArr;
            try {
                iArr[StringAttributeType.SPELLOUT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$io$flutter$view$AccessibilityBridge$StringAttributeType[StringAttributeType.LOCALE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public boolean externalViewRequestSendAccessibilityEvent(View embeddedView, View eventOrigin, AccessibilityEvent event) {
        Integer virtualNodeId;
        if (this.accessibilityViewEmbedder.requestSendAccessibilityEvent(embeddedView, eventOrigin, event) && (virtualNodeId = this.accessibilityViewEmbedder.getRecordFlutterId(embeddedView, event)) != null) {
            switch (event.getEventType()) {
                case 8:
                    this.embeddedInputFocusedNodeId = virtualNodeId;
                    this.inputFocusedSemanticsNode = null;
                    return true;
                case 128:
                    this.hoveredObject = null;
                    return true;
                case 32768:
                    this.embeddedAccessibilityFocusedNodeId = virtualNodeId;
                    this.accessibilityFocusedSemanticsNode = null;
                    return true;
                case 65536:
                    this.embeddedInputFocusedNodeId = null;
                    this.embeddedAccessibilityFocusedNodeId = null;
                    return true;
                default:
                    return true;
            }
        }
        return false;
    }
}
