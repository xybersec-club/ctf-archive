package com.example.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.databinding.ActivityScrollingBinding;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public class ScrollingActivity extends AppCompatActivity {
    private ActivityScrollingBinding binding;

    public static void getNothings(String str) {
        Matcher matcher = Pattern.compile("\\.(.?)\\.").matcher(str);
        ArrayList<String> arrayList = new ArrayList();
        while (matcher.find()) {
            arrayList.add(matcher.group(0));
        }
        System.out.println("TODO");
        for (String str2 : arrayList) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityScrollingBinding inflate = ActivityScrollingBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
        setSupportActionBar(this.binding.toolbar);
        this.binding.toolbarLayout.setTitle(getTitle());
        this.binding.fab.setOnClickListener(new View.OnClickListener() { // from class: com.example.myapplication.ScrollingActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", 0).setAction("Action", (View.OnClickListener) null).show();
                ScrollingActivity.getNothings(String.valueOf((int) R.string.large_text));
                System.out.println("Is this correct!");
            }
        });
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
