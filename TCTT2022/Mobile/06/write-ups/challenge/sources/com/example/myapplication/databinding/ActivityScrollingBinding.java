package com.example.myapplication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.myapplication.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
/* loaded from: classes.dex */
public final class ActivityScrollingBinding implements ViewBinding {
    public final AppBarLayout appBar;
    public final FloatingActionButton fab;
    private final CoordinatorLayout rootView;
    public final Toolbar toolbar;
    public final CollapsingToolbarLayout toolbarLayout;

    private ActivityScrollingBinding(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, FloatingActionButton floatingActionButton, Toolbar toolbar, CollapsingToolbarLayout collapsingToolbarLayout) {
        this.rootView = coordinatorLayout;
        this.appBar = appBarLayout;
        this.fab = floatingActionButton;
        this.toolbar = toolbar;
        this.toolbarLayout = collapsingToolbarLayout;
    }

    @Override // androidx.viewbinding.ViewBinding
    public CoordinatorLayout getRoot() {
        return this.rootView;
    }

    public static ActivityScrollingBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ActivityScrollingBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View inflate = layoutInflater.inflate(R.layout.activity_scrolling, viewGroup, false);
        if (z) {
            viewGroup.addView(inflate);
        }
        return bind(inflate);
    }

    public static ActivityScrollingBinding bind(View view) {
        int i = R.id.app_bar;
        AppBarLayout appBarLayout = (AppBarLayout) ViewBindings.findChildViewById(view, R.id.app_bar);
        if (appBarLayout != null) {
            i = R.id.fab;
            FloatingActionButton floatingActionButton = (FloatingActionButton) ViewBindings.findChildViewById(view, R.id.fab);
            if (floatingActionButton != null) {
                i = R.id.toolbar;
                Toolbar toolbar = (Toolbar) ViewBindings.findChildViewById(view, R.id.toolbar);
                if (toolbar != null) {
                    i = R.id.toolbar_layout;
                    CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) ViewBindings.findChildViewById(view, R.id.toolbar_layout);
                    if (collapsingToolbarLayout != null) {
                        return new ActivityScrollingBinding((CoordinatorLayout) view, appBarLayout, floatingActionButton, toolbar, collapsingToolbarLayout);
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
