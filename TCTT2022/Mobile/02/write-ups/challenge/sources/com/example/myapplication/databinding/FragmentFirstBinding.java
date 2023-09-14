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
public final class FragmentFirstBinding implements ViewBinding {
    public final Button buttonFirst;
    private final ConstraintLayout rootView;
    public final TextView textviewFirst;

    private FragmentFirstBinding(ConstraintLayout constraintLayout, Button button, TextView textView) {
        this.rootView = constraintLayout;
        this.buttonFirst = button;
        this.textviewFirst = textView;
    }

    @Override // androidx.viewbinding.ViewBinding
    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static FragmentFirstBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static FragmentFirstBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View inflate = layoutInflater.inflate(R.layout.fragment_first, viewGroup, false);
        if (z) {
            viewGroup.addView(inflate);
        }
        return bind(inflate);
    }

    public static FragmentFirstBinding bind(View view) {
        int i = R.id.button_first;
        Button button = (Button) ViewBindings.findChildViewById(view, R.id.button_first);
        if (button != null) {
            i = R.id.textview_first;
            TextView textView = (TextView) ViewBindings.findChildViewById(view, R.id.textview_first);
            if (textView != null) {
                return new FragmentFirstBinding((ConstraintLayout) view, button, textView);
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
