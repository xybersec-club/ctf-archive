package androidx.navigation.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.customview.widget.Openable;
import androidx.navigation.FloatingWindow;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Typography;
/* compiled from: AbstractAppBarOnDestinationChangedListener.kt */
@Metadata(d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\r\n\u0000\b \u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\"\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0016J\u0010\u0010\u001a\u001a\u00020\u00132\u0006\u0010\u001b\u001a\u00020\u001cH\u0003J\u001c\u0010\u001d\u001a\u00020\u00132\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\b\b\u0001\u0010 \u001a\u00020\u0011H$J\u0012\u0010!\u001a\u00020\u00132\b\u0010\"\u001a\u0004\u0018\u00010#H$R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u000b\u001a\u0012\u0012\f\u0012\n \u000e*\u0004\u0018\u00010\r0\r\u0018\u00010\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006$"}, d2 = {"Landroidx/navigation/ui/AbstractAppBarOnDestinationChangedListener;", "Landroidx/navigation/NavController$OnDestinationChangedListener;", "context", "Landroid/content/Context;", "configuration", "Landroidx/navigation/ui/AppBarConfiguration;", "(Landroid/content/Context;Landroidx/navigation/ui/AppBarConfiguration;)V", "animator", "Landroid/animation/ValueAnimator;", "arrowDrawable", "Landroidx/appcompat/graphics/drawable/DrawerArrowDrawable;", "openableLayoutWeakReference", "Ljava/lang/ref/WeakReference;", "Landroidx/customview/widget/Openable;", "kotlin.jvm.PlatformType", "topLevelDestinations", "", "", "onDestinationChanged", "", "controller", "Landroidx/navigation/NavController;", "destination", "Landroidx/navigation/NavDestination;", "arguments", "Landroid/os/Bundle;", "setActionBarUpIndicator", "showAsDrawerIndicator", "", "setNavigationIcon", "icon", "Landroid/graphics/drawable/Drawable;", "contentDescription", "setTitle", "title", "", "navigation-ui_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* loaded from: classes.dex */
public abstract class AbstractAppBarOnDestinationChangedListener implements NavController.OnDestinationChangedListener {
    private ValueAnimator animator;
    private DrawerArrowDrawable arrowDrawable;
    private final Context context;
    private final WeakReference<Openable> openableLayoutWeakReference;
    private final Set<Integer> topLevelDestinations;

    protected abstract void setNavigationIcon(Drawable drawable, int i);

    protected abstract void setTitle(CharSequence charSequence);

    public AbstractAppBarOnDestinationChangedListener(Context context, AppBarConfiguration configuration) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(configuration, "configuration");
        this.context = context;
        this.topLevelDestinations = configuration.getTopLevelDestinations();
        Openable openableLayout = configuration.getOpenableLayout();
        this.openableLayoutWeakReference = openableLayout != null ? new WeakReference<>(openableLayout) : null;
    }

    @Override // androidx.navigation.NavController.OnDestinationChangedListener
    public void onDestinationChanged(NavController controller, NavDestination destination, Bundle bundle) {
        Intrinsics.checkNotNullParameter(controller, "controller");
        Intrinsics.checkNotNullParameter(destination, "destination");
        if (destination instanceof FloatingWindow) {
            return;
        }
        WeakReference<Openable> weakReference = this.openableLayoutWeakReference;
        Openable openable = weakReference != null ? weakReference.get() : null;
        if (this.openableLayoutWeakReference != null && openable == null) {
            controller.removeOnDestinationChangedListener(this);
            return;
        }
        CharSequence label = destination.getLabel();
        boolean z = true;
        if (label != null) {
            StringBuffer stringBuffer = new StringBuffer();
            Matcher matcher = Pattern.compile("\\{(.+?)\\}").matcher(label);
            while (matcher.find()) {
                String group = matcher.group(1);
                if (bundle != null && bundle.containsKey(group)) {
                    matcher.appendReplacement(stringBuffer, "");
                    stringBuffer.append(String.valueOf(bundle.get(group)));
                } else {
                    throw new IllegalArgumentException("Could not find \"" + group + "\" in " + bundle + " to fill label \"" + ((Object) label) + Typography.quote);
                }
            }
            matcher.appendTail(stringBuffer);
            setTitle(stringBuffer);
        }
        boolean matchDestinations$navigation_ui_release = NavigationUI.matchDestinations$navigation_ui_release(destination, this.topLevelDestinations);
        if (openable == null && matchDestinations$navigation_ui_release) {
            setNavigationIcon(null, 0);
            return;
        }
        if (openable == null || !matchDestinations$navigation_ui_release) {
            z = false;
        }
        setActionBarUpIndicator(z);
    }

    private final void setActionBarUpIndicator(boolean z) {
        Pair pair;
        int i;
        DrawerArrowDrawable drawerArrowDrawable = this.arrowDrawable;
        if (drawerArrowDrawable == null || (pair = TuplesKt.to(drawerArrowDrawable, true)) == null) {
            DrawerArrowDrawable drawerArrowDrawable2 = new DrawerArrowDrawable(this.context);
            this.arrowDrawable = drawerArrowDrawable2;
            pair = TuplesKt.to(drawerArrowDrawable2, false);
        }
        DrawerArrowDrawable drawerArrowDrawable3 = (DrawerArrowDrawable) pair.component1();
        boolean booleanValue = ((Boolean) pair.component2()).booleanValue();
        DrawerArrowDrawable drawerArrowDrawable4 = drawerArrowDrawable3;
        if (z) {
            i = R.string.nav_app_bar_open_drawer_description;
        } else {
            i = R.string.nav_app_bar_navigate_up_description;
        }
        setNavigationIcon(drawerArrowDrawable4, i);
        float f = z ? 0.0f : 1.0f;
        if (booleanValue) {
            float progress = drawerArrowDrawable3.getProgress();
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(drawerArrowDrawable3, "progress", progress, f);
            this.animator = ofFloat;
            Objects.requireNonNull(ofFloat, "null cannot be cast to non-null type android.animation.ObjectAnimator");
            ofFloat.start();
            return;
        }
        drawerArrowDrawable3.setProgress(f);
    }
}
