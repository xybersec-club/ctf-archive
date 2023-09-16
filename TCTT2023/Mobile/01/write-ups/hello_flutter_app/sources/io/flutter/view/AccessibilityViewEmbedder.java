package io.flutter.view;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.accessibility.AccessibilityRecord;
import io.flutter.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
class AccessibilityViewEmbedder {
    private static final String TAG = "AccessibilityBridge";
    private int nextFlutterId;
    private final View rootAccessibilityView;
    private final ReflectionAccessors reflectionAccessors = new ReflectionAccessors();
    private final SparseArray<ViewAndId> flutterIdToOrigin = new SparseArray<>();
    private final Map<ViewAndId, Integer> originToFlutterId = new HashMap();
    private final Map<View, Rect> embeddedViewToDisplayBounds = new HashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AccessibilityViewEmbedder(View rootAccessibiiltyView, int firstVirtualNodeId) {
        this.rootAccessibilityView = rootAccessibiiltyView;
        this.nextFlutterId = firstVirtualNodeId;
    }

    public AccessibilityNodeInfo getRootNode(View embeddedView, int flutterId, Rect displayBounds) {
        AccessibilityNodeInfo originNode = embeddedView.createAccessibilityNodeInfo();
        Long originPackedId = this.reflectionAccessors.getSourceNodeId(originNode);
        if (originPackedId == null) {
            return null;
        }
        this.embeddedViewToDisplayBounds.put(embeddedView, displayBounds);
        int originId = ReflectionAccessors.getVirtualNodeId(originPackedId.longValue());
        cacheVirtualIdMappings(embeddedView, originId, flutterId);
        return convertToFlutterNode(originNode, flutterId, embeddedView);
    }

    public AccessibilityNodeInfo createAccessibilityNodeInfo(int flutterId) {
        AccessibilityNodeInfo originNode;
        ViewAndId origin = this.flutterIdToOrigin.get(flutterId);
        if (origin == null || !this.embeddedViewToDisplayBounds.containsKey(origin.view)) {
            return null;
        }
        AccessibilityNodeProvider provider = origin.view.getAccessibilityNodeProvider();
        if (provider == null || (originNode = origin.view.getAccessibilityNodeProvider().createAccessibilityNodeInfo(origin.id)) == null) {
            return null;
        }
        return convertToFlutterNode(originNode, flutterId, origin.view);
    }

    private AccessibilityNodeInfo convertToFlutterNode(AccessibilityNodeInfo originNode, int flutterId, View embeddedView) {
        AccessibilityNodeInfo result = AccessibilityNodeInfo.obtain(this.rootAccessibilityView, flutterId);
        result.setPackageName(this.rootAccessibilityView.getContext().getPackageName());
        result.setSource(this.rootAccessibilityView, flutterId);
        result.setClassName(originNode.getClassName());
        Rect displayBounds = this.embeddedViewToDisplayBounds.get(embeddedView);
        copyAccessibilityFields(originNode, result);
        setFlutterNodesTranslateBounds(originNode, displayBounds, result);
        addChildrenToFlutterNode(originNode, embeddedView, result);
        setFlutterNodeParent(originNode, embeddedView, result);
        return result;
    }

    private void setFlutterNodeParent(AccessibilityNodeInfo originNode, View embeddedView, AccessibilityNodeInfo result) {
        Long parentOriginPackedId = this.reflectionAccessors.getParentNodeId(originNode);
        if (parentOriginPackedId == null) {
            return;
        }
        int parentOriginId = ReflectionAccessors.getVirtualNodeId(parentOriginPackedId.longValue());
        Integer parentFlutterId = this.originToFlutterId.get(new ViewAndId(embeddedView, parentOriginId));
        if (parentFlutterId != null) {
            result.setParent(this.rootAccessibilityView, parentFlutterId.intValue());
        }
    }

    private void addChildrenToFlutterNode(AccessibilityNodeInfo originNode, View embeddedView, AccessibilityNodeInfo resultNode) {
        int childFlutterId;
        for (int i = 0; i < originNode.getChildCount(); i++) {
            Long originPackedId = this.reflectionAccessors.getChildId(originNode, i);
            if (originPackedId != null) {
                int originId = ReflectionAccessors.getVirtualNodeId(originPackedId.longValue());
                ViewAndId origin = new ViewAndId(embeddedView, originId);
                if (this.originToFlutterId.containsKey(origin)) {
                    childFlutterId = this.originToFlutterId.get(origin).intValue();
                } else {
                    childFlutterId = this.nextFlutterId;
                    this.nextFlutterId = childFlutterId + 1;
                    cacheVirtualIdMappings(embeddedView, originId, childFlutterId);
                }
                resultNode.addChild(this.rootAccessibilityView, childFlutterId);
            }
        }
    }

    private void cacheVirtualIdMappings(View embeddedView, int originId, int flutterId) {
        ViewAndId origin = new ViewAndId(embeddedView, originId);
        this.originToFlutterId.put(origin, Integer.valueOf(flutterId));
        this.flutterIdToOrigin.put(flutterId, origin);
    }

    private void setFlutterNodesTranslateBounds(AccessibilityNodeInfo originNode, Rect displayBounds, AccessibilityNodeInfo resultNode) {
        Rect boundsInParent = new Rect();
        originNode.getBoundsInParent(boundsInParent);
        resultNode.setBoundsInParent(boundsInParent);
        Rect boundsInScreen = new Rect();
        originNode.getBoundsInScreen(boundsInScreen);
        boundsInScreen.offset(displayBounds.left, displayBounds.top);
        resultNode.setBoundsInScreen(boundsInScreen);
    }

    private void copyAccessibilityFields(AccessibilityNodeInfo input, AccessibilityNodeInfo output) {
        output.setAccessibilityFocused(input.isAccessibilityFocused());
        output.setCheckable(input.isCheckable());
        output.setChecked(input.isChecked());
        output.setContentDescription(input.getContentDescription());
        output.setEnabled(input.isEnabled());
        output.setClickable(input.isClickable());
        output.setFocusable(input.isFocusable());
        output.setFocused(input.isFocused());
        output.setLongClickable(input.isLongClickable());
        output.setMovementGranularities(input.getMovementGranularities());
        output.setPassword(input.isPassword());
        output.setScrollable(input.isScrollable());
        output.setSelected(input.isSelected());
        output.setText(input.getText());
        output.setVisibleToUser(input.isVisibleToUser());
        if (Build.VERSION.SDK_INT >= 18) {
            output.setEditable(input.isEditable());
        }
        if (Build.VERSION.SDK_INT >= 19) {
            output.setCanOpenPopup(input.canOpenPopup());
            output.setCollectionInfo(input.getCollectionInfo());
            output.setCollectionItemInfo(input.getCollectionItemInfo());
            output.setContentInvalid(input.isContentInvalid());
            output.setDismissable(input.isDismissable());
            output.setInputType(input.getInputType());
            output.setLiveRegion(input.getLiveRegion());
            output.setMultiLine(input.isMultiLine());
            output.setRangeInfo(input.getRangeInfo());
        }
        if (Build.VERSION.SDK_INT >= 21) {
            output.setError(input.getError());
            output.setMaxTextLength(input.getMaxTextLength());
        }
        if (Build.VERSION.SDK_INT >= 23) {
            output.setContextClickable(input.isContextClickable());
        }
        if (Build.VERSION.SDK_INT >= 24) {
            output.setDrawingOrder(input.getDrawingOrder());
            output.setImportantForAccessibility(input.isImportantForAccessibility());
        }
        if (Build.VERSION.SDK_INT >= 26) {
            output.setAvailableExtraData(input.getAvailableExtraData());
            output.setHintText(input.getHintText());
            output.setShowingHintText(input.isShowingHintText());
        }
    }

    public boolean requestSendAccessibilityEvent(View embeddedView, View eventOrigin, AccessibilityEvent event) {
        AccessibilityEvent translatedEvent = AccessibilityEvent.obtain(event);
        Long originPackedId = this.reflectionAccessors.getRecordSourceNodeId(event);
        if (originPackedId == null) {
            return false;
        }
        int originVirtualId = ReflectionAccessors.getVirtualNodeId(originPackedId.longValue());
        Integer flutterId = this.originToFlutterId.get(new ViewAndId(embeddedView, originVirtualId));
        if (flutterId == null) {
            int i = this.nextFlutterId;
            this.nextFlutterId = i + 1;
            flutterId = Integer.valueOf(i);
            cacheVirtualIdMappings(embeddedView, originVirtualId, flutterId.intValue());
        }
        translatedEvent.setSource(this.rootAccessibilityView, flutterId.intValue());
        translatedEvent.setClassName(event.getClassName());
        translatedEvent.setPackageName(event.getPackageName());
        for (int i2 = 0; i2 < translatedEvent.getRecordCount(); i2++) {
            AccessibilityRecord record = translatedEvent.getRecord(i2);
            Long recordOriginPackedId = this.reflectionAccessors.getRecordSourceNodeId(record);
            if (recordOriginPackedId == null) {
                return false;
            }
            int recordOriginVirtualID = ReflectionAccessors.getVirtualNodeId(recordOriginPackedId.longValue());
            ViewAndId originViewAndId = new ViewAndId(embeddedView, recordOriginVirtualID);
            if (!this.originToFlutterId.containsKey(originViewAndId)) {
                return false;
            }
            int recordFlutterId = this.originToFlutterId.get(originViewAndId).intValue();
            record.setSource(this.rootAccessibilityView, recordFlutterId);
        }
        return this.rootAccessibilityView.getParent().requestSendAccessibilityEvent(eventOrigin, translatedEvent);
    }

    public boolean performAction(int flutterId, int accessibilityAction, Bundle arguments) {
        ViewAndId origin = this.flutterIdToOrigin.get(flutterId);
        if (origin == null) {
            return false;
        }
        View embeddedView = origin.view;
        AccessibilityNodeProvider provider = embeddedView.getAccessibilityNodeProvider();
        if (provider == null) {
            return false;
        }
        return provider.performAction(origin.id, accessibilityAction, arguments);
    }

    public Integer getRecordFlutterId(View embeddedView, AccessibilityRecord record) {
        Long originPackedId = this.reflectionAccessors.getRecordSourceNodeId(record);
        if (originPackedId == null) {
            return null;
        }
        int originVirtualId = ReflectionAccessors.getVirtualNodeId(originPackedId.longValue());
        return this.originToFlutterId.get(new ViewAndId(embeddedView, originVirtualId));
    }

    public boolean onAccessibilityHoverEvent(int rootFlutterId, MotionEvent event) {
        ViewAndId origin = this.flutterIdToOrigin.get(rootFlutterId);
        if (origin == null) {
            return false;
        }
        Rect displayBounds = this.embeddedViewToDisplayBounds.get(origin.view);
        int pointerCount = event.getPointerCount();
        MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[pointerCount];
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[pointerCount];
        for (int i = 0; i < event.getPointerCount(); i++) {
            pointerProperties[i] = new MotionEvent.PointerProperties();
            event.getPointerProperties(i, pointerProperties[i]);
            MotionEvent.PointerCoords originCoords = new MotionEvent.PointerCoords();
            event.getPointerCoords(i, originCoords);
            pointerCoords[i] = new MotionEvent.PointerCoords(originCoords);
            pointerCoords[i].x -= displayBounds.left;
            pointerCoords[i].y -= displayBounds.top;
        }
        MotionEvent translatedEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getPointerCount(), pointerProperties, pointerCoords, event.getMetaState(), event.getButtonState(), event.getXPrecision(), event.getYPrecision(), event.getDeviceId(), event.getEdgeFlags(), event.getSource(), event.getFlags());
        return origin.view.dispatchGenericMotionEvent(translatedEvent);
    }

    public View platformViewOfNode(int flutterId) {
        ViewAndId viewAndId = this.flutterIdToOrigin.get(flutterId);
        if (viewAndId == null) {
            return null;
        }
        return viewAndId.view;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ViewAndId {
        final int id;
        final View view;

        private ViewAndId(View view, int id) {
            this.view = view;
            this.id = id;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof ViewAndId) {
                ViewAndId viewAndId = (ViewAndId) o;
                return this.id == viewAndId.id && this.view.equals(viewAndId.view);
            }
            return false;
        }

        public int hashCode() {
            int result = (1 * 31) + this.view.hashCode();
            return (result * 31) + this.id;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ReflectionAccessors {
        private final Field childNodeIdsField;
        private final Method getChildId;
        private final Method getParentNodeId;
        private final Method getRecordSourceNodeId;
        private final Method getSourceNodeId;
        private final Method longArrayGetIndex;

        private ReflectionAccessors() {
            Method getSourceNodeId = null;
            Method getParentNodeId = null;
            Method getRecordSourceNodeId = null;
            Method getChildId = null;
            Field childNodeIdsField = null;
            Method longArrayGetIndex = null;
            try {
                getSourceNodeId = AccessibilityNodeInfo.class.getMethod("getSourceNodeId", new Class[0]);
            } catch (NoSuchMethodException e) {
                Log.w(AccessibilityViewEmbedder.TAG, "can't invoke AccessibilityNodeInfo#getSourceNodeId with reflection");
            }
            try {
                getRecordSourceNodeId = AccessibilityRecord.class.getMethod("getSourceNodeId", new Class[0]);
            } catch (NoSuchMethodException e2) {
                Log.w(AccessibilityViewEmbedder.TAG, "can't invoke AccessibiiltyRecord#getSourceNodeId with reflection");
            }
            if (Build.VERSION.SDK_INT <= 26) {
                try {
                    getParentNodeId = AccessibilityNodeInfo.class.getMethod("getParentNodeId", new Class[0]);
                } catch (NoSuchMethodException e3) {
                    Log.w(AccessibilityViewEmbedder.TAG, "can't invoke getParentNodeId with reflection");
                }
                try {
                    getChildId = AccessibilityNodeInfo.class.getMethod("getChildId", Integer.TYPE);
                } catch (NoSuchMethodException e4) {
                    Log.w(AccessibilityViewEmbedder.TAG, "can't invoke getChildId with reflection");
                }
            } else {
                try {
                    childNodeIdsField = AccessibilityNodeInfo.class.getDeclaredField("mChildNodeIds");
                    childNodeIdsField.setAccessible(true);
                    longArrayGetIndex = Class.forName("android.util.LongArray").getMethod("get", Integer.TYPE);
                } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException | NullPointerException e5) {
                    Log.w(AccessibilityViewEmbedder.TAG, "can't access childNodeIdsField with reflection");
                    childNodeIdsField = null;
                }
            }
            this.getSourceNodeId = getSourceNodeId;
            this.getParentNodeId = getParentNodeId;
            this.getRecordSourceNodeId = getRecordSourceNodeId;
            this.getChildId = getChildId;
            this.childNodeIdsField = childNodeIdsField;
            this.longArrayGetIndex = longArrayGetIndex;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static int getVirtualNodeId(long nodeId) {
            return (int) (nodeId >> 32);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Long getSourceNodeId(AccessibilityNodeInfo node) {
            Method method = this.getSourceNodeId;
            if (method == null) {
                return null;
            }
            try {
                return (Long) method.invoke(node, new Object[0]);
            } catch (IllegalAccessException e) {
                Log.w(AccessibilityViewEmbedder.TAG, "Failed to access getSourceNodeId method.", e);
                return null;
            } catch (InvocationTargetException e2) {
                Log.w(AccessibilityViewEmbedder.TAG, "The getSourceNodeId method threw an exception when invoked.", e2);
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Long getChildId(AccessibilityNodeInfo node, int child) {
            Method method = this.getChildId;
            if (method == null && (this.childNodeIdsField == null || this.longArrayGetIndex == null)) {
                return null;
            }
            if (method != null) {
                try {
                    return (Long) method.invoke(node, Integer.valueOf(child));
                } catch (IllegalAccessException e) {
                    Log.w(AccessibilityViewEmbedder.TAG, "Failed to access getChildId method.", e);
                } catch (InvocationTargetException e2) {
                    Log.w(AccessibilityViewEmbedder.TAG, "The getChildId method threw an exception when invoked.", e2);
                }
            } else {
                try {
                    return Long.valueOf(((Long) this.longArrayGetIndex.invoke(this.childNodeIdsField.get(node), Integer.valueOf(child))).longValue());
                } catch (ArrayIndexOutOfBoundsException e3) {
                    e = e3;
                    Log.w(AccessibilityViewEmbedder.TAG, "The longArrayGetIndex method threw an exception when invoked.", e);
                    return null;
                } catch (IllegalAccessException e4) {
                    Log.w(AccessibilityViewEmbedder.TAG, "Failed to access longArrayGetIndex method or the childNodeId field.", e4);
                } catch (InvocationTargetException e5) {
                    e = e5;
                    Log.w(AccessibilityViewEmbedder.TAG, "The longArrayGetIndex method threw an exception when invoked.", e);
                    return null;
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Long getParentNodeId(AccessibilityNodeInfo node) {
            Method method = this.getParentNodeId;
            if (method != null) {
                try {
                    return Long.valueOf(((Long) method.invoke(node, new Object[0])).longValue());
                } catch (IllegalAccessException e) {
                    Log.w(AccessibilityViewEmbedder.TAG, "Failed to access getParentNodeId method.", e);
                } catch (InvocationTargetException e2) {
                    Log.w(AccessibilityViewEmbedder.TAG, "The getParentNodeId method threw an exception when invoked.", e2);
                }
            }
            return yoinkParentIdFromParcel(node);
        }

        private static Long yoinkParentIdFromParcel(AccessibilityNodeInfo node) {
            if (Build.VERSION.SDK_INT < 26) {
                Log.w(AccessibilityViewEmbedder.TAG, "Unexpected Android version. Unable to find the parent ID.");
                return null;
            }
            AccessibilityNodeInfo copy = AccessibilityNodeInfo.obtain(node);
            Parcel parcel = Parcel.obtain();
            parcel.setDataPosition(0);
            copy.writeToParcel(parcel, 0);
            Long parentNodeId = null;
            parcel.setDataPosition(0);
            long nonDefaultFields = parcel.readLong();
            int fieldIndex = 0 + 1;
            if (isBitSet(nonDefaultFields, 0)) {
                parcel.readInt();
            }
            int fieldIndex2 = fieldIndex + 1;
            if (isBitSet(nonDefaultFields, fieldIndex)) {
                parcel.readLong();
            }
            int fieldIndex3 = fieldIndex2 + 1;
            if (isBitSet(nonDefaultFields, fieldIndex2)) {
                parcel.readInt();
            }
            int i = fieldIndex3 + 1;
            if (isBitSet(nonDefaultFields, fieldIndex3)) {
                parentNodeId = Long.valueOf(parcel.readLong());
            }
            parcel.recycle();
            return parentNodeId;
        }

        private static boolean isBitSet(long flags, int bitIndex) {
            return ((1 << bitIndex) & flags) != 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Long getRecordSourceNodeId(AccessibilityRecord node) {
            Method method = this.getRecordSourceNodeId;
            if (method == null) {
                return null;
            }
            try {
                return (Long) method.invoke(node, new Object[0]);
            } catch (IllegalAccessException e) {
                Log.w(AccessibilityViewEmbedder.TAG, "Failed to access the getRecordSourceNodeId method.", e);
                return null;
            } catch (InvocationTargetException e2) {
                Log.w(AccessibilityViewEmbedder.TAG, "The getRecordSourceNodeId method threw an exception when invoked.", e2);
                return null;
            }
        }
    }
}
