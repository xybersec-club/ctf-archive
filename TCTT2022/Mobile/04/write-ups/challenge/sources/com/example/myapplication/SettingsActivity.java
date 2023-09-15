package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import java.math.BigInteger;
/* loaded from: classes.dex */
public class SettingsActivity extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.settings_activity);
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
        }
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        BigInteger valueOf = BigInteger.valueOf(116L);
        BigInteger valueOf2 = BigInteger.valueOf(99L);
        BigInteger valueOf3 = BigInteger.valueOf(116L);
        BigInteger valueOf4 = BigInteger.valueOf(116L);
        BigInteger valueOf5 = BigInteger.valueOf(50L);
        BigInteger valueOf6 = BigInteger.valueOf(48L);
        BigInteger valueOf7 = BigInteger.valueOf(50L);
        BigInteger valueOf8 = BigInteger.valueOf(50L);
        BigInteger valueOf9 = BigInteger.valueOf(123L);
        BigInteger valueOf10 = BigInteger.valueOf(55L);
        BigInteger valueOf11 = BigInteger.valueOf(72L);
        BigInteger valueOf12 = BigInteger.valueOf(105L);
        BigInteger valueOf13 = BigInteger.valueOf(115L);
        BigInteger valueOf14 = BigInteger.valueOf(95L);
        BigInteger valueOf15 = BigInteger.valueOf(73L);
        BigInteger valueOf16 = BigInteger.valueOf(83L);
        BigInteger valueOf17 = BigInteger.valueOf(95L);
        BigInteger valueOf18 = BigInteger.valueOf(83L);
        BigInteger valueOf19 = BigInteger.valueOf(48L);
        BigInteger valueOf20 = BigInteger.valueOf(77L);
        BigInteger valueOf21 = BigInteger.valueOf(51L);
        BigInteger valueOf22 = BigInteger.valueOf(72L);
        BigInteger valueOf23 = BigInteger.valueOf(48L);
        BigInteger valueOf24 = BigInteger.valueOf(119L);
        BigInteger valueOf25 = BigInteger.valueOf(95L);
        BigInteger multiply = valueOf.multiply(valueOf2).multiply(valueOf3).multiply(valueOf4).multiply(valueOf5).multiply(valueOf6).multiply(valueOf7).multiply(valueOf8).multiply(valueOf9).multiply(valueOf10).multiply(valueOf11).multiply(valueOf12).multiply(valueOf13).multiply(valueOf14).multiply(valueOf15).multiply(valueOf16).multiply(valueOf17).multiply(valueOf18).multiply(valueOf19).multiply(valueOf20).multiply(valueOf21).multiply(valueOf22).multiply(valueOf23).multiply(valueOf24).multiply(valueOf25).multiply(BigInteger.valueOf(52L));
        BigInteger subtract = multiply.subtract(new BigInteger("9478156768108809020312608277716464950855189431875"));
        System.out.println("sec = " + multiply + "\n");
        System.out.println("ret = " + subtract + "\n");
        System.out.println("where is its secret?\n");
    }

    /* loaded from: classes.dex */
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override // androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
            setPreferencesFromResource(R.xml.root_preferences, str);
        }
    }
}
