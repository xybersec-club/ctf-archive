package com.example.myapplication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewbinding.ViewBinding;
import com.example.myapplication.R;
/* loaded from: classes.dex */
public final class ContentScrollingBinding implements ViewBinding {
    private final View rootView;

    private ContentScrollingBinding(View view) {
        this.rootView = view;
    }

    @Override // androidx.viewbinding.ViewBinding
    public View getRoot() {
        return this.rootView;
    }

    public static ContentScrollingBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ContentScrollingBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View inflate = layoutInflater.inflate(R.layout.content_scrolling, viewGroup, false);
        if (z) {
            viewGroup.addView(inflate);
        }
        return bind(inflate);
    }

    public static ContentScrollingBinding bind(View view) {
        if (view == null) {
            throw new NullPointerException("rootView");
        }
        return new ContentScrollingBinding(view);
    }
}
