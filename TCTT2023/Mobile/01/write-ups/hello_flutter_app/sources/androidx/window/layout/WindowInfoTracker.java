package androidx.window.layout;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import androidx.window.extensions.WindowExtensionsProvider;
import androidx.window.extensions.layout.WindowLayoutComponent;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlinx.coroutines.flow.Flow;
/* compiled from: WindowInfoTracker.kt */
@Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007J\u0016\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&¨\u0006\b"}, d2 = {"Landroidx/window/layout/WindowInfoTracker;", "", "windowLayoutInfo", "Lkotlinx/coroutines/flow/Flow;", "Landroidx/window/layout/WindowLayoutInfo;", "activity", "Landroid/app/Activity;", "Companion", "window_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes.dex */
public interface WindowInfoTracker {
    public static final Companion Companion = Companion.$$INSTANCE;

    /* compiled from: WindowInfoTracker.kt */
    @SynthesizedClassV2(kind = 7, versionHash = "15f1483824cf4085ddca5a8529d873fc59a8ced2cbce67fb2b3dd9033ea03442")
    /* renamed from: androidx.window.layout.WindowInfoTracker$-CC  reason: invalid class name */
    /* loaded from: classes.dex */
    public final /* synthetic */ class CC {
        static {
            Companion companion = WindowInfoTracker.Companion;
        }

        @JvmStatic
        public static WindowInfoTracker getOrCreate(Context context) {
            return WindowInfoTracker.Companion.getOrCreate(context);
        }

        @JvmStatic
        public static void overrideDecorator(WindowInfoTrackerDecorator windowInfoTrackerDecorator) {
            WindowInfoTracker.Companion.overrideDecorator(windowInfoTrackerDecorator);
        }

        @JvmStatic
        public static void reset() {
            WindowInfoTracker.Companion.reset();
        }
    }

    Flow<WindowLayoutInfo> windowLayoutInfo(Activity activity);

    /* compiled from: WindowInfoTracker.kt */
    @Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0007J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\bH\u0007J\b\u0010\u0010\u001a\u00020\u000eH\u0007J\u0015\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u000b\u001a\u00020\fH\u0000¢\u0006\u0002\b\u0013R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0014"}, d2 = {"Landroidx/window/layout/WindowInfoTracker$Companion;", "", "()V", "DEBUG", "", "TAG", "", "decorator", "Landroidx/window/layout/WindowInfoTrackerDecorator;", "getOrCreate", "Landroidx/window/layout/WindowInfoTracker;", "context", "Landroid/content/Context;", "overrideDecorator", "", "overridingDecorator", "reset", "windowBackend", "Landroidx/window/layout/WindowBackend;", "windowBackend$window_release", "window_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
    /* loaded from: classes.dex */
    public static final class Companion {
        private static final boolean DEBUG = false;
        static final /* synthetic */ Companion $$INSTANCE = new Companion();
        private static final String TAG = Reflection.getOrCreateKotlinClass(WindowInfoTracker.class).getSimpleName();
        private static WindowInfoTrackerDecorator decorator = EmptyDecorator.INSTANCE;

        private Companion() {
        }

        @JvmStatic
        public final WindowInfoTracker getOrCreate(Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            WindowInfoTrackerImpl repo = new WindowInfoTrackerImpl(WindowMetricsCalculatorCompat.INSTANCE, windowBackend$window_release(context));
            return decorator.decorate(repo);
        }

        public final WindowBackend windowBackend$window_release(Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            ExtensionWindowLayoutInfoBackend extensionBackend = null;
            try {
                WindowLayoutComponent p0 = WindowExtensionsProvider.getWindowExtensions().getWindowLayoutComponent();
                if (p0 != null) {
                    extensionBackend = new ExtensionWindowLayoutInfoBackend(p0);
                }
            } catch (Throwable th) {
                if (DEBUG) {
                    Log.d(TAG, "Failed to load WindowExtensions");
                }
            }
            return extensionBackend == null ? SidecarWindowBackend.Companion.getInstance(context) : extensionBackend;
        }

        @JvmStatic
        public final void overrideDecorator(WindowInfoTrackerDecorator overridingDecorator) {
            Intrinsics.checkNotNullParameter(overridingDecorator, "overridingDecorator");
            decorator = overridingDecorator;
        }

        @JvmStatic
        public final void reset() {
            decorator = EmptyDecorator.INSTANCE;
        }
    }
}
