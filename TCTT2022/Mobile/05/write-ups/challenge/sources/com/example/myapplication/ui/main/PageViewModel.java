package com.example.myapplication.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import com.example.myapplication.R;
/* loaded from: classes.dex */
public class PageViewModel extends ViewModel {
    private MutableLiveData<Integer> mIndex;
    private LiveData<String> mText;

    public PageViewModel() {
        MutableLiveData<Integer> mutableLiveData = new MutableLiveData<>();
        this.mIndex = mutableLiveData;
        this.mText = Transformations.map(mutableLiveData, new Function<Integer, String>() { // from class: com.example.myapplication.ui.main.PageViewModel.1
            @Override // androidx.arch.core.util.Function
            public String apply(Integer num) {
                switch (num.intValue() - 1) {
                    case 0:
                        return "...t...c...t...t...2...0...2...2...{".replace(".", "");
                    case 1:
                        return "3741625f";
                    case 2:
                        return "41576159";
                    case 3:
                        return "5f663072";
                    case 4:
                        return "5f374833";
                    case 5:
                        return String.valueOf((int) R.string.int_5);
                    case 6:
                        return String.valueOf((int) R.string.int_6);
                    case 7:
                        return String.valueOf((int) R.string.int_7);
                    case 8:
                        return String.valueOf((int) R.string.int_8);
                    case 9:
                        return "}";
                    default:
                        return "Hello world from section: " + (num.intValue() - 1);
                }
            }
        });
    }

    public void setIndex(int i) {
        this.mIndex.setValue(Integer.valueOf(i));
    }

    public LiveData<String> getText() {
        return this.mText;
    }
}
