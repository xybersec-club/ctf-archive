package android.support.design.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.design.R;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.TooltipCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
/* loaded from: classes.dex */
public class BottomNavigationItemView extends FrameLayout implements MenuView.ItemView {
    private static final int[] CHECKED_STATE_SET = {16842912};
    public static final int INVALID_ITEM_POSITION = -1;
    private final int mDefaultMargin;
    private ImageView mIcon;
    private ColorStateList mIconTint;
    private MenuItemImpl mItemData;
    private int mItemPosition;
    private final TextView mLargeLabel;
    private final float mScaleDownFactor;
    private final float mScaleUpFactor;
    private final int mShiftAmount;
    private boolean mShiftingMode;
    private final TextView mSmallLabel;

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public boolean prefersCondensedTitle() {
        return false;
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public void setShortcut(boolean z, char c) {
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public boolean showsIcon() {
        return true;
    }

    public BottomNavigationItemView(Context context) {
        this(context, null);
    }

    public BottomNavigationItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BottomNavigationItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mItemPosition = -1;
        Resources resources = getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_text_size);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_active_text_size);
        this.mDefaultMargin = resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_margin);
        this.mShiftAmount = dimensionPixelSize - dimensionPixelSize2;
        float f = dimensionPixelSize2;
        float f2 = dimensionPixelSize;
        this.mScaleUpFactor = (f * 1.0f) / f2;
        this.mScaleDownFactor = (f2 * 1.0f) / f;
        LayoutInflater.from(context).inflate(R.layout.design_bottom_navigation_item, (ViewGroup) this, true);
        setBackgroundResource(R.drawable.design_bottom_navigation_item_background);
        this.mIcon = (ImageView) findViewById(R.id.icon);
        this.mSmallLabel = (TextView) findViewById(R.id.smallLabel);
        this.mLargeLabel = (TextView) findViewById(R.id.largeLabel);
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public void initialize(MenuItemImpl menuItemImpl, int i) {
        this.mItemData = menuItemImpl;
        setCheckable(menuItemImpl.isCheckable());
        setChecked(menuItemImpl.isChecked());
        setEnabled(menuItemImpl.isEnabled());
        setIcon(menuItemImpl.getIcon());
        setTitle(menuItemImpl.getTitle());
        setId(menuItemImpl.getItemId());
        setContentDescription(menuItemImpl.getContentDescription());
        TooltipCompat.setTooltipText(this, menuItemImpl.getTooltipText());
    }

    public void setItemPosition(int i) {
        this.mItemPosition = i;
    }

    public int getItemPosition() {
        return this.mItemPosition;
    }

    public void setShiftingMode(boolean z) {
        this.mShiftingMode = z;
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public MenuItemImpl getItemData() {
        return this.mItemData;
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public void setTitle(CharSequence charSequence) {
        this.mSmallLabel.setText(charSequence);
        this.mLargeLabel.setText(charSequence);
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public void setCheckable(boolean z) {
        refreshDrawableState();
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public void setChecked(boolean z) {
        TextView textView = this.mLargeLabel;
        textView.setPivotX(textView.getWidth() / 2);
        TextView textView2 = this.mLargeLabel;
        textView2.setPivotY(textView2.getBaseline());
        TextView textView3 = this.mSmallLabel;
        textView3.setPivotX(textView3.getWidth() / 2);
        TextView textView4 = this.mSmallLabel;
        textView4.setPivotY(textView4.getBaseline());
        if (this.mShiftingMode) {
            if (z) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mIcon.getLayoutParams();
                layoutParams.gravity = 49;
                layoutParams.topMargin = this.mDefaultMargin;
                this.mIcon.setLayoutParams(layoutParams);
                this.mLargeLabel.setVisibility(0);
                this.mLargeLabel.setScaleX(1.0f);
                this.mLargeLabel.setScaleY(1.0f);
            } else {
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mIcon.getLayoutParams();
                layoutParams2.gravity = 17;
                layoutParams2.topMargin = this.mDefaultMargin;
                this.mIcon.setLayoutParams(layoutParams2);
                this.mLargeLabel.setVisibility(4);
                this.mLargeLabel.setScaleX(0.5f);
                this.mLargeLabel.setScaleY(0.5f);
            }
            this.mSmallLabel.setVisibility(4);
        } else if (z) {
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mIcon.getLayoutParams();
            layoutParams3.gravity = 49;
            layoutParams3.topMargin = this.mDefaultMargin + this.mShiftAmount;
            this.mIcon.setLayoutParams(layoutParams3);
            this.mLargeLabel.setVisibility(0);
            this.mSmallLabel.setVisibility(4);
            this.mLargeLabel.setScaleX(1.0f);
            this.mLargeLabel.setScaleY(1.0f);
            this.mSmallLabel.setScaleX(this.mScaleUpFactor);
            this.mSmallLabel.setScaleY(this.mScaleUpFactor);
        } else {
            FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.mIcon.getLayoutParams();
            layoutParams4.gravity = 49;
            layoutParams4.topMargin = this.mDefaultMargin;
            this.mIcon.setLayoutParams(layoutParams4);
            this.mLargeLabel.setVisibility(4);
            this.mSmallLabel.setVisibility(0);
            this.mLargeLabel.setScaleX(this.mScaleDownFactor);
            this.mLargeLabel.setScaleY(this.mScaleDownFactor);
            this.mSmallLabel.setScaleX(1.0f);
            this.mSmallLabel.setScaleY(1.0f);
        }
        refreshDrawableState();
    }

    @Override // android.view.View, android.support.v7.view.menu.MenuView.ItemView
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.mSmallLabel.setEnabled(z);
        this.mLargeLabel.setEnabled(z);
        this.mIcon.setEnabled(z);
        if (z) {
            ViewCompat.setPointerIcon(this, PointerIconCompat.getSystemIcon(getContext(), PointerIconCompat.TYPE_HAND));
        } else {
            ViewCompat.setPointerIcon(this, null);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public int[] onCreateDrawableState(int i) {
        int[] onCreateDrawableState = super.onCreateDrawableState(i + 1);
        MenuItemImpl menuItemImpl = this.mItemData;
        if (menuItemImpl != null && menuItemImpl.isCheckable() && this.mItemData.isChecked()) {
            mergeDrawableStates(onCreateDrawableState, CHECKED_STATE_SET);
        }
        return onCreateDrawableState;
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public void setIcon(Drawable drawable) {
        if (drawable != null) {
            Drawable.ConstantState constantState = drawable.getConstantState();
            if (constantState != null) {
                drawable = constantState.newDrawable();
            }
            drawable = DrawableCompat.wrap(drawable).mutate();
            DrawableCompat.setTintList(drawable, this.mIconTint);
        }
        this.mIcon.setImageDrawable(drawable);
    }

    public void setIconTintList(ColorStateList colorStateList) {
        this.mIconTint = colorStateList;
        MenuItemImpl menuItemImpl = this.mItemData;
        if (menuItemImpl != null) {
            setIcon(menuItemImpl.getIcon());
        }
    }

    public void setTextColor(ColorStateList colorStateList) {
        this.mSmallLabel.setTextColor(colorStateList);
        this.mLargeLabel.setTextColor(colorStateList);
    }

    public void setItemBackground(int i) {
        ViewCompat.setBackground(this, i == 0 ? null : ContextCompat.getDrawable(getContext(), i));
    }
}
