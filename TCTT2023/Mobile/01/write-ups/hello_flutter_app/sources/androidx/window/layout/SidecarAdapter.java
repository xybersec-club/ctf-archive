package androidx.window.layout;

import android.graphics.Rect;
import androidx.window.core.Bounds;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.HardwareFoldingFeature;
import androidx.window.sidecar.SidecarDeviceState;
import androidx.window.sidecar.SidecarDisplayFeature;
import androidx.window.sidecar.SidecarWindowLayoutInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: SidecarAdapter.kt */
@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0000\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015B\u0005¢\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006J\u001c\u0010\b\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\t2\b\u0010\u0007\u001a\u0004\u0018\u00010\tH\u0002J(\u0010\n\u001a\u00020\u00042\u000e\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\u000b2\u000e\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\u000bH\u0002J\u001a\u0010\f\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\r2\b\u0010\u0007\u001a\u0004\u0018\u00010\rJ\u0018\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\r2\u0006\u0010\u0011\u001a\u00020\u0006J\"\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00120\u000b2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\u0006\u0010\u0014\u001a\u00020\u0006¨\u0006\u0016"}, d2 = {"Landroidx/window/layout/SidecarAdapter;", "", "()V", "isEqualSidecarDeviceState", "", "first", "Landroidx/window/sidecar/SidecarDeviceState;", "second", "isEqualSidecarDisplayFeature", "Landroidx/window/sidecar/SidecarDisplayFeature;", "isEqualSidecarDisplayFeatures", "", "isEqualSidecarWindowLayoutInfo", "Landroidx/window/sidecar/SidecarWindowLayoutInfo;", "translate", "Landroidx/window/layout/WindowLayoutInfo;", "extensionInfo", "state", "Landroidx/window/layout/DisplayFeature;", "sidecarDisplayFeatures", "deviceState", "Companion", "window_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes.dex */
public final class SidecarAdapter {
    public static final Companion Companion = new Companion(null);
    private static final String TAG = SidecarAdapter.class.getSimpleName();

    public final List<DisplayFeature> translate(List<SidecarDisplayFeature> sidecarDisplayFeatures, SidecarDeviceState deviceState) {
        Intrinsics.checkNotNullParameter(sidecarDisplayFeatures, "sidecarDisplayFeatures");
        Intrinsics.checkNotNullParameter(deviceState, "deviceState");
        List<SidecarDisplayFeature> $this$mapNotNull$iv = sidecarDisplayFeatures;
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv$iv : $this$mapNotNull$iv) {
            SidecarDisplayFeature sidecarFeature = (SidecarDisplayFeature) element$iv$iv$iv;
            DisplayFeature translate$window_release = Companion.translate$window_release(sidecarFeature, deviceState);
            if (translate$window_release != null) {
                destination$iv$iv.add(translate$window_release);
            }
        }
        return (List) destination$iv$iv;
    }

    public final WindowLayoutInfo translate(SidecarWindowLayoutInfo extensionInfo, SidecarDeviceState state) {
        Intrinsics.checkNotNullParameter(state, "state");
        if (extensionInfo == null) {
            return new WindowLayoutInfo(CollectionsKt.emptyList());
        }
        SidecarDeviceState deviceState = new SidecarDeviceState();
        Companion companion = Companion;
        int devicePosture = companion.getSidecarDevicePosture$window_release(state);
        companion.setSidecarDevicePosture(deviceState, devicePosture);
        List sidecarDisplayFeatures = companion.getSidecarDisplayFeatures(extensionInfo);
        List displayFeatures = translate(sidecarDisplayFeatures, deviceState);
        return new WindowLayoutInfo(displayFeatures);
    }

    public final boolean isEqualSidecarDeviceState(SidecarDeviceState first, SidecarDeviceState second) {
        if (Intrinsics.areEqual(first, second)) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        Companion companion = Companion;
        int firstPosture = companion.getSidecarDevicePosture$window_release(first);
        int secondPosture = companion.getSidecarDevicePosture$window_release(second);
        return firstPosture == secondPosture;
    }

    public final boolean isEqualSidecarWindowLayoutInfo(SidecarWindowLayoutInfo first, SidecarWindowLayoutInfo second) {
        if (Intrinsics.areEqual(first, second)) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        Companion companion = Companion;
        List firstDisplayFeatures = companion.getSidecarDisplayFeatures(first);
        List secondDisplayFeatures = companion.getSidecarDisplayFeatures(second);
        return isEqualSidecarDisplayFeatures(firstDisplayFeatures, secondDisplayFeatures);
    }

    private final boolean isEqualSidecarDisplayFeatures(List<SidecarDisplayFeature> list, List<SidecarDisplayFeature> list2) {
        if (list == list2) {
            return true;
        }
        if (list == null || list2 == null || list.size() != list2.size()) {
            return false;
        }
        int size = list.size() - 1;
        if (size < 0) {
            return true;
        }
        int i = 0;
        do {
            int i2 = i;
            i++;
            SidecarDisplayFeature firstFeature = list.get(i2);
            SidecarDisplayFeature secondFeature = list2.get(i2);
            if (!isEqualSidecarDisplayFeature(firstFeature, secondFeature)) {
                return false;
            }
        } while (i <= size);
        return true;
    }

    private final boolean isEqualSidecarDisplayFeature(SidecarDisplayFeature first, SidecarDisplayFeature second) {
        if (Intrinsics.areEqual(first, second)) {
            return true;
        }
        return first != null && second != null && first.getType() == second.getType() && Intrinsics.areEqual(first.getRect(), second.getRect());
    }

    /* compiled from: SidecarAdapter.kt */
    @Metadata(d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0007J\u0015\u0010\n\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0000¢\u0006\u0002\b\u000bJ\u0016\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u0006\u0010\u000f\u001a\u00020\u0010H\u0007J\u0018\u0010\u0011\u001a\u00020\u00122\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0013\u001a\u00020\u0007H\u0007J \u0010\u0014\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u00102\u000e\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\rH\u0007J\u001f\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0018\u001a\u00020\u000e2\u0006\u0010\u0019\u001a\u00020\tH\u0000¢\u0006\u0002\b\u001aR\u0016\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001b"}, d2 = {"Landroidx/window/layout/SidecarAdapter$Companion;", "", "()V", "TAG", "", "kotlin.jvm.PlatformType", "getRawSidecarDevicePosture", "", "sidecarDeviceState", "Landroidx/window/sidecar/SidecarDeviceState;", "getSidecarDevicePosture", "getSidecarDevicePosture$window_release", "getSidecarDisplayFeatures", "", "Landroidx/window/sidecar/SidecarDisplayFeature;", "info", "Landroidx/window/sidecar/SidecarWindowLayoutInfo;", "setSidecarDevicePosture", "", "posture", "setSidecarDisplayFeatures", "displayFeatures", "translate", "Landroidx/window/layout/DisplayFeature;", "feature", "deviceState", "translate$window_release", "window_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final List<SidecarDisplayFeature> getSidecarDisplayFeatures(SidecarWindowLayoutInfo info) {
            Intrinsics.checkNotNullParameter(info, "info");
            try {
                List<SidecarDisplayFeature> list = info.displayFeatures;
                return list == null ? CollectionsKt.emptyList() : list;
            } catch (NoSuchFieldError e) {
                try {
                    Method methodGetFeatures = SidecarWindowLayoutInfo.class.getMethod("getDisplayFeatures", new Class[0]);
                    Object invoke = methodGetFeatures.invoke(info, new Object[0]);
                    if (invoke != null) {
                        return (List) invoke;
                    }
                    throw new NullPointerException("null cannot be cast to non-null type kotlin.collections.List<androidx.window.sidecar.SidecarDisplayFeature>");
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e2) {
                    return CollectionsKt.emptyList();
                }
            }
        }

        public final void setSidecarDisplayFeatures(SidecarWindowLayoutInfo info, List<SidecarDisplayFeature> displayFeatures) {
            Intrinsics.checkNotNullParameter(info, "info");
            Intrinsics.checkNotNullParameter(displayFeatures, "displayFeatures");
            try {
                info.displayFeatures = displayFeatures;
            } catch (NoSuchFieldError e) {
                try {
                    Method methodSetFeatures = SidecarWindowLayoutInfo.class.getMethod("setDisplayFeatures", List.class);
                    methodSetFeatures.invoke(info, displayFeatures);
                } catch (IllegalAccessException e2) {
                } catch (NoSuchMethodException e3) {
                } catch (InvocationTargetException e4) {
                }
            }
        }

        public final int getSidecarDevicePosture$window_release(SidecarDeviceState sidecarDeviceState) {
            Intrinsics.checkNotNullParameter(sidecarDeviceState, "sidecarDeviceState");
            int rawPosture = getRawSidecarDevicePosture(sidecarDeviceState);
            if (rawPosture < 0 || rawPosture > 4) {
                return 0;
            }
            return rawPosture;
        }

        public final int getRawSidecarDevicePosture(SidecarDeviceState sidecarDeviceState) {
            Intrinsics.checkNotNullParameter(sidecarDeviceState, "sidecarDeviceState");
            try {
                return sidecarDeviceState.posture;
            } catch (NoSuchFieldError e) {
                try {
                    Method methodGetPosture = SidecarDeviceState.class.getMethod("getPosture", new Class[0]);
                    Object invoke = methodGetPosture.invoke(sidecarDeviceState, new Object[0]);
                    if (invoke != null) {
                        return ((Integer) invoke).intValue();
                    }
                    throw new NullPointerException("null cannot be cast to non-null type kotlin.Int");
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e2) {
                    return 0;
                }
            }
        }

        public final void setSidecarDevicePosture(SidecarDeviceState sidecarDeviceState, int posture) {
            Intrinsics.checkNotNullParameter(sidecarDeviceState, "sidecarDeviceState");
            try {
                sidecarDeviceState.posture = posture;
            } catch (NoSuchFieldError e) {
                try {
                    Method methodSetPosture = SidecarDeviceState.class.getMethod("setPosture", Integer.TYPE);
                    methodSetPosture.invoke(sidecarDeviceState, Integer.valueOf(posture));
                } catch (IllegalAccessException e2) {
                } catch (NoSuchMethodException e3) {
                } catch (InvocationTargetException e4) {
                }
            }
        }

        public final DisplayFeature translate$window_release(SidecarDisplayFeature feature, SidecarDeviceState deviceState) {
            HardwareFoldingFeature.Type type;
            FoldingFeature.State state;
            Intrinsics.checkNotNullParameter(feature, "feature");
            Intrinsics.checkNotNullParameter(deviceState, "deviceState");
            Rect bounds = feature.getRect();
            Intrinsics.checkNotNullExpressionValue(bounds, "feature.rect");
            if (bounds.width() == 0 && bounds.height() == 0) {
                return null;
            }
            if (feature.getType() != 1 || bounds.width() == 0 || bounds.height() == 0) {
                if ((feature.getType() != 2 && feature.getType() != 1) || bounds.left == 0 || bounds.top == 0) {
                    switch (feature.getType()) {
                        case 1:
                            type = HardwareFoldingFeature.Type.Companion.getFOLD();
                            break;
                        case 2:
                            type = HardwareFoldingFeature.Type.Companion.getHINGE();
                            break;
                        default:
                            return null;
                    }
                    switch (getSidecarDevicePosture$window_release(deviceState)) {
                        case 0:
                        case 1:
                        case 4:
                            return null;
                        case 2:
                            state = FoldingFeature.State.HALF_OPENED;
                            break;
                        case 3:
                            state = FoldingFeature.State.FLAT;
                            break;
                        default:
                            state = FoldingFeature.State.FLAT;
                            break;
                    }
                    Rect rect = feature.getRect();
                    Intrinsics.checkNotNullExpressionValue(rect, "feature.rect");
                    return new HardwareFoldingFeature(new Bounds(rect), type, state);
                }
                return null;
            }
            return null;
        }
    }
}
