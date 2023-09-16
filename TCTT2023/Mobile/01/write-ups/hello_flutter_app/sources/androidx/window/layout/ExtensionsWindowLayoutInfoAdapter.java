package androidx.window.layout;

import android.app.Activity;
import android.graphics.Rect;
import androidx.window.core.Bounds;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.HardwareFoldingFeature;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: ExtensionsWindowLayoutInfoAdapter.kt */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\bÀ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001f\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0000¢\u0006\u0002\b\tJ\u001d\u0010\u0003\u001a\u00020\n2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\fH\u0000¢\u0006\u0002\b\tJ\u0018\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u0010H\u0002¨\u0006\u0011"}, d2 = {"Landroidx/window/layout/ExtensionsWindowLayoutInfoAdapter;", "", "()V", "translate", "Landroidx/window/layout/FoldingFeature;", "activity", "Landroid/app/Activity;", "oemFeature", "Landroidx/window/extensions/layout/FoldingFeature;", "translate$window_release", "Landroidx/window/layout/WindowLayoutInfo;", "info", "Landroidx/window/extensions/layout/WindowLayoutInfo;", "validBounds", "", "bounds", "Landroidx/window/core/Bounds;", "window_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes.dex */
public final class ExtensionsWindowLayoutInfoAdapter {
    public static final ExtensionsWindowLayoutInfoAdapter INSTANCE = new ExtensionsWindowLayoutInfoAdapter();

    private ExtensionsWindowLayoutInfoAdapter() {
    }

    public final FoldingFeature translate$window_release(Activity activity, androidx.window.extensions.layout.FoldingFeature oemFeature) {
        HardwareFoldingFeature.Type type;
        FoldingFeature.State state;
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(oemFeature, "oemFeature");
        switch (oemFeature.getType()) {
            case 1:
                type = HardwareFoldingFeature.Type.Companion.getFOLD();
                break;
            case 2:
                type = HardwareFoldingFeature.Type.Companion.getHINGE();
                break;
            default:
                return null;
        }
        switch (oemFeature.getState()) {
            case 1:
                state = FoldingFeature.State.FLAT;
                break;
            case 2:
                state = FoldingFeature.State.HALF_OPENED;
                break;
            default:
                return null;
        }
        Rect bounds = oemFeature.getBounds();
        Intrinsics.checkNotNullExpressionValue(bounds, "oemFeature.bounds");
        Bounds bounds2 = new Bounds(bounds);
        if (validBounds(activity, bounds2)) {
            Rect bounds3 = oemFeature.getBounds();
            Intrinsics.checkNotNullExpressionValue(bounds3, "oemFeature.bounds");
            return new HardwareFoldingFeature(new Bounds(bounds3), type, state);
        }
        return null;
    }

    public final WindowLayoutInfo translate$window_release(Activity activity, androidx.window.extensions.layout.WindowLayoutInfo info) {
        Iterable $this$mapNotNull$iv;
        FoldingFeature foldingFeature;
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(info, "info");
        Iterable displayFeatures = info.getDisplayFeatures();
        Intrinsics.checkNotNullExpressionValue(displayFeatures, "info.displayFeatures");
        Iterable $this$mapNotNull$iv2 = displayFeatures;
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv$iv : $this$mapNotNull$iv2) {
            androidx.window.extensions.layout.FoldingFeature feature = (androidx.window.extensions.layout.DisplayFeature) element$iv$iv$iv;
            if (feature instanceof androidx.window.extensions.layout.FoldingFeature) {
                ExtensionsWindowLayoutInfoAdapter extensionsWindowLayoutInfoAdapter = INSTANCE;
                $this$mapNotNull$iv = $this$mapNotNull$iv2;
                Intrinsics.checkNotNullExpressionValue(feature, "feature");
                foldingFeature = extensionsWindowLayoutInfoAdapter.translate$window_release(activity, feature);
            } else {
                $this$mapNotNull$iv = $this$mapNotNull$iv2;
                foldingFeature = null;
            }
            if (foldingFeature != null) {
                destination$iv$iv.add(foldingFeature);
            }
            $this$mapNotNull$iv2 = $this$mapNotNull$iv;
        }
        List features = (List) destination$iv$iv;
        return new WindowLayoutInfo(features);
    }

    private final boolean validBounds(Activity activity, Bounds bounds) {
        Rect windowBounds = WindowMetricsCalculatorCompat.INSTANCE.computeCurrentWindowMetrics(activity).getBounds();
        if (bounds.isZero()) {
            return false;
        }
        if (bounds.getWidth() == windowBounds.width() || bounds.getHeight() == windowBounds.height()) {
            if (bounds.getWidth() >= windowBounds.width() || bounds.getHeight() >= windowBounds.height()) {
                return (bounds.getWidth() == windowBounds.width() && bounds.getHeight() == windowBounds.height()) ? false : true;
            }
            return false;
        }
        return false;
    }
}
