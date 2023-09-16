package io.flutter.plugin.platform;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import io.flutter.embedding.android.FlutterImageView;
/* loaded from: classes.dex */
public class PlatformOverlayView extends FlutterImageView {
    private AccessibilityEventsDelegate accessibilityDelegate;

    public PlatformOverlayView(Context context, int width, int height, AccessibilityEventsDelegate accessibilityDelegate) {
        super(context, width, height, FlutterImageView.SurfaceKind.overlay);
        this.accessibilityDelegate = accessibilityDelegate;
    }

    public PlatformOverlayView(Context context) {
        this(context, 1, 1, null);
    }

    public PlatformOverlayView(Context context, AttributeSet attrs) {
        this(context, 1, 1, null);
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent event) {
        AccessibilityEventsDelegate accessibilityEventsDelegate = this.accessibilityDelegate;
        if (accessibilityEventsDelegate != null && accessibilityEventsDelegate.onAccessibilityHoverEvent(event, true)) {
            return true;
        }
        return super.onHoverEvent(event);
    }
}
