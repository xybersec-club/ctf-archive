package androidx.navigation;

import androidx.lifecycle.ViewModelProvider;
import kotlin.Lazy;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
/* compiled from: NavGraphViewModelLazy.kt */
@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001\"\n\b\u0000\u0010\u0002\u0018\u0001*\u00020\u0003H\n¢\u0006\u0002\b\u0004"}, d2 = {"<anonymous>", "Landroidx/lifecycle/ViewModelProvider$Factory;", "VM", "Landroidx/lifecycle/ViewModel;", "invoke"}, k = 3, mv = {1, 6, 0}, xi = 176)
/* loaded from: classes.dex */
public final class NavGraphViewModelLazyKt$navGraphViewModels$8 extends Lambda implements Function0<ViewModelProvider.Factory> {
    final /* synthetic */ Lazy<NavBackStackEntry> $backStackEntry$delegate;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public NavGraphViewModelLazyKt$navGraphViewModels$8(Lazy<NavBackStackEntry> lazy) {
        super(0);
        this.$backStackEntry$delegate = lazy;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // kotlin.jvm.functions.Function0
    public final ViewModelProvider.Factory invoke() {
        NavBackStackEntry m71navGraphViewModels$lambda3;
        m71navGraphViewModels$lambda3 = NavGraphViewModelLazyKt.m71navGraphViewModels$lambda3(this.$backStackEntry$delegate);
        return m71navGraphViewModels$lambda3.getDefaultViewModelProviderFactory();
    }
}
