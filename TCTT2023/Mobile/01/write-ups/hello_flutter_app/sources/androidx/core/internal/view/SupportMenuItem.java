package androidx.core.internal.view;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.view.MenuItem;
import android.view.View;
import androidx.core.view.ActionProvider;
import com.android.tools.r8.annotations.SynthesizedClassV2;
/* loaded from: classes.dex */
public interface SupportMenuItem extends MenuItem {
    public static final int SHOW_AS_ACTION_ALWAYS = 2;
    public static final int SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW = 8;
    public static final int SHOW_AS_ACTION_IF_ROOM = 1;
    public static final int SHOW_AS_ACTION_NEVER = 0;
    public static final int SHOW_AS_ACTION_WITH_TEXT = 4;

    @Override // android.view.MenuItem
    boolean collapseActionView();

    @Override // android.view.MenuItem
    boolean expandActionView();

    @Override // android.view.MenuItem
    View getActionView();

    @Override // android.view.MenuItem
    int getAlphabeticModifiers();

    @Override // android.view.MenuItem
    CharSequence getContentDescription();

    @Override // android.view.MenuItem
    ColorStateList getIconTintList();

    @Override // android.view.MenuItem
    PorterDuff.Mode getIconTintMode();

    @Override // android.view.MenuItem
    int getNumericModifiers();

    ActionProvider getSupportActionProvider();

    @Override // android.view.MenuItem
    CharSequence getTooltipText();

    @Override // android.view.MenuItem
    boolean isActionViewExpanded();

    boolean requiresActionButton();

    boolean requiresOverflow();

    @Override // android.view.MenuItem
    MenuItem setActionView(int resId);

    @Override // android.view.MenuItem
    MenuItem setActionView(View view);

    @Override // android.view.MenuItem
    MenuItem setAlphabeticShortcut(char alphaChar, int alphaModifiers);

    @Override // android.view.MenuItem
    SupportMenuItem setContentDescription(CharSequence contentDescription);

    @Override // android.view.MenuItem
    MenuItem setIconTintList(ColorStateList tint);

    @Override // android.view.MenuItem
    MenuItem setIconTintMode(PorterDuff.Mode tintMode);

    @Override // android.view.MenuItem
    MenuItem setNumericShortcut(char numericChar, int numericModifiers);

    @Override // android.view.MenuItem
    MenuItem setShortcut(char numericChar, char alphaChar, int numericModifiers, int alphaModifiers);

    @Override // android.view.MenuItem
    void setShowAsAction(int actionEnum);

    @Override // android.view.MenuItem
    MenuItem setShowAsActionFlags(int actionEnum);

    SupportMenuItem setSupportActionProvider(ActionProvider actionProvider);

    @Override // android.view.MenuItem
    SupportMenuItem setTooltipText(CharSequence tooltipText);

    @SynthesizedClassV2(kind = 7, versionHash = "15f1483824cf4085ddca5a8529d873fc59a8ced2cbce67fb2b3dd9033ea03442")
    /* renamed from: androidx.core.internal.view.SupportMenuItem$-CC  reason: invalid class name */
    /* loaded from: classes.dex */
    public final /* synthetic */ class CC {
    }
}
