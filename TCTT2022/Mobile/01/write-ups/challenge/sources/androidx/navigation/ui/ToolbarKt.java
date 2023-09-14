package androidx.navigation.ui;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: Toolbar.kt */
@Metadata(d1 = {"\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u001c\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u001a\u001c\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\b¨\u0006\t"}, d2 = {"setupWithNavController", "", "Landroidx/appcompat/widget/Toolbar;", "navController", "Landroidx/navigation/NavController;", "drawerLayout", "Landroidx/drawerlayout/widget/DrawerLayout;", "configuration", "Landroidx/navigation/ui/AppBarConfiguration;", "navigation-ui_release"}, k = 2, mv = {1, 6, 0}, xi = 48)
/* loaded from: classes.dex */
public final class ToolbarKt {
    public static final void setupWithNavController(Toolbar toolbar, NavController navController, DrawerLayout drawerLayout) {
        Intrinsics.checkNotNullParameter(toolbar, "<this>");
        Intrinsics.checkNotNullParameter(navController, "navController");
        NavigationUI.setupWithNavController(toolbar, navController, new AppBarConfiguration.Builder(navController.getGraph()).setOpenableLayout(drawerLayout).setFallbackOnNavigateUpListener(new AppBarConfigurationKt$sam$i$androidx_navigation_ui_AppBarConfiguration_OnNavigateUpListener$0(AppBarConfigurationKt$AppBarConfiguration$1.INSTANCE)).build());
    }

    public static /* synthetic */ void setupWithNavController$default(Toolbar toolbar, NavController navController, AppBarConfiguration appBarConfiguration, int i, Object obj) {
        if ((i & 2) != 0) {
            appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).setOpenableLayout(null).setFallbackOnNavigateUpListener(new AppBarConfigurationKt$sam$i$androidx_navigation_ui_AppBarConfiguration_OnNavigateUpListener$0(AppBarConfigurationKt$AppBarConfiguration$1.INSTANCE)).build();
        }
        setupWithNavController(toolbar, navController, appBarConfiguration);
    }

    public static final void setupWithNavController(Toolbar toolbar, NavController navController, AppBarConfiguration configuration) {
        Intrinsics.checkNotNullParameter(toolbar, "<this>");
        Intrinsics.checkNotNullParameter(navController, "navController");
        Intrinsics.checkNotNullParameter(configuration, "configuration");
        NavigationUI.setupWithNavController(toolbar, navController, configuration);
    }
}
