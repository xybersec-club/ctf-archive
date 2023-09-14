package com.example.myapplication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.myapplication.R;
/* loaded from: classes.dex */
public final class FragmentSecondBinding implements ViewBinding {
    public final Button buttonSecond;
    private final ConstraintLayout rootView;
    public final TextView textviewSecond;

    private FragmentSecondBinding(ConstraintLayout constraintLayout, Button button, TextView textView) {
        this.rootView = constraintLayout;
        this.buttonSecond = button;
        this.textviewSecond = textView;
    }

    @Override // androidx.viewbinding.ViewBinding
    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static FragmentSecondBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static FragmentSecondBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View inflate = layoutInflater.inflate(R.layout.fragment_second, viewGroup, false);
        if (z) {
            viewGroup.addView(inflate);
        }
        return bind(inflate);
    }

    public static FragmentSecondBinding bind(View view) {
        int i = R.id.button_second;
        Button button = (Button) ViewBindings.findChildViewById(view, R.id.button_second);
        if (button != null) {
            i = R.id.textview_second;
            TextView textView = (TextView) ViewBindings.findChildViewById(view, R.id.textview_second);
            if (textView != null) {
                return new FragmentSecondBinding((ConstraintLayout) view, button, textView);
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
