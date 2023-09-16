package io.flutter.plugin.platform;

import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import io.flutter.view.AccessibilityBridge;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AccessibilityEventsDelegate {
    private AccessibilityBridge accessibilityBridge;

    public boolean requestSendAccessibilityEvent(View embeddedView, View eventOrigin, AccessibilityEvent event) {
        AccessibilityBridge accessibilityBridge = this.accessibilityBridge;
        if (accessibilityBridge == null) {
            return false;
        }
        return accessibilityBridge.externalViewRequestSendAccessibilityEvent(embeddedView, eventOrigin, event);
    }

    public boolean onAccessibilityHoverEvent(MotionEvent event, boolean ignorePlatformViews) {
        AccessibilityBridge accessibilityBridge = this.accessibilityBridge;
        if (accessibilityBridge == null) {
            return false;
        }
        return accessibilityBridge.onAccessibilityHoverEvent(event, ignorePlatformViews);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAccessibilityBridge(AccessibilityBridge accessibilityBridge) {
        this.accessibilityBridge = accessibilityBridge;
    }
}
