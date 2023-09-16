package android.support.design.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.R;
import android.support.design.internal.SnackbarContentLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
/* loaded from: classes.dex */
public final class Snackbar extends BaseTransientBottomBar<Snackbar> {
    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_LONG = 0;
    public static final int LENGTH_SHORT = -1;
    private BaseTransientBottomBar.BaseCallback<Snackbar> mCallback;

    /* loaded from: classes.dex */
    public static class Callback extends BaseTransientBottomBar.BaseCallback<Snackbar> {
        public static final int DISMISS_EVENT_ACTION = 1;
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;
        public static final int DISMISS_EVENT_MANUAL = 3;
        public static final int DISMISS_EVENT_SWIPE = 0;
        public static final int DISMISS_EVENT_TIMEOUT = 2;

        @Override // android.support.design.widget.BaseTransientBottomBar.BaseCallback
        public void onDismissed(Snackbar snackbar, int i) {
        }

        @Override // android.support.design.widget.BaseTransientBottomBar.BaseCallback
        public void onShown(Snackbar snackbar) {
        }
    }

    private Snackbar(ViewGroup viewGroup, View view, BaseTransientBottomBar.ContentViewCallback contentViewCallback) {
        super(viewGroup, view, contentViewCallback);
    }

    public static Snackbar make(View view, CharSequence charSequence, int i) {
        ViewGroup findSuitableParent = findSuitableParent(view);
        if (findSuitableParent == null) {
            throw new IllegalArgumentException("No suitable parent found from the given view. Please provide a valid view.");
        }
        SnackbarContentLayout snackbarContentLayout = (SnackbarContentLayout) LayoutInflater.from(findSuitableParent.getContext()).inflate(R.layout.design_layout_snackbar_include, findSuitableParent, false);
        Snackbar snackbar = new Snackbar(findSuitableParent, snackbarContentLayout, snackbarContentLayout);
        snackbar.setText(charSequence);
        snackbar.setDuration(i);
        return snackbar;
    }

    public static Snackbar make(View view, int i, int i2) {
        return make(view, view.getResources().getText(i), i2);
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup viewGroup = null;
        while (!(view instanceof CoordinatorLayout)) {
            if (view instanceof FrameLayout) {
                if (view.getId() == 16908290) {
                    return (ViewGroup) view;
                }
                viewGroup = (ViewGroup) view;
            }
            if (view != null) {
                ViewParent parent = view.getParent();
                if (parent instanceof View) {
                    view = (View) parent;
                    continue;
                } else {
                    view = null;
                    continue;
                }
            }
            if (view == null) {
                return viewGroup;
            }
        }
        return (ViewGroup) view;
    }

    public Snackbar setText(CharSequence charSequence) {
        ((SnackbarContentLayout) this.mView.getChildAt(0)).getMessageView().setText(charSequence);
        return this;
    }

    public Snackbar setText(int i) {
        return setText(getContext().getText(i));
    }

    public Snackbar setAction(int i, View.OnClickListener onClickListener) {
        return setAction(getContext().getText(i), onClickListener);
    }

    public Snackbar setAction(CharSequence charSequence, final View.OnClickListener onClickListener) {
        Button actionView = ((SnackbarContentLayout) this.mView.getChildAt(0)).getActionView();
        if (TextUtils.isEmpty(charSequence) || onClickListener == null) {
            actionView.setVisibility(8);
            actionView.setOnClickListener(null);
        } else {
            actionView.setVisibility(0);
            actionView.setText(charSequence);
            actionView.setOnClickListener(new View.OnClickListener() { // from class: android.support.design.widget.Snackbar.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    onClickListener.onClick(view);
                    Snackbar.this.dispatchDismiss(1);
                }
            });
        }
        return this;
    }

    public Snackbar setActionTextColor(ColorStateList colorStateList) {
        ((SnackbarContentLayout) this.mView.getChildAt(0)).getActionView().setTextColor(colorStateList);
        return this;
    }

    public Snackbar setActionTextColor(int i) {
        ((SnackbarContentLayout) this.mView.getChildAt(0)).getActionView().setTextColor(i);
        return this;
    }

    @Deprecated
    public Snackbar setCallback(Callback callback) {
        BaseTransientBottomBar.BaseCallback<Snackbar> baseCallback = this.mCallback;
        if (baseCallback != null) {
            removeCallback(baseCallback);
        }
        if (callback != null) {
            addCallback(callback);
        }
        this.mCallback = callback;
        return this;
    }

    /* loaded from: classes.dex */
    public static final class SnackbarLayout extends BaseTransientBottomBar.SnackbarBaseLayout {
        public SnackbarLayout(Context context) {
            super(context);
        }

        public SnackbarLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            int childCount = getChildCount();
            int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                if (childAt.getLayoutParams().width == -1) {
                    childAt.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(childAt.getMeasuredHeight(), 1073741824));
                }
            }
        }
    }
}
