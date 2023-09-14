package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.myapplication.databinding.FragmentFirstBinding;
/* loaded from: classes.dex */
public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FragmentFirstBinding inflate = FragmentFirstBinding.inflate(layoutInflater, viewGroup, false);
        this.binding = inflate;
        return inflate.getRoot();
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.binding.buttonFirst.setOnClickListener(new View.OnClickListener() { // from class: com.example.myapplication.FirstFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.binding = null;
    }
}
