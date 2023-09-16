package com.mc.securepin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class FlagActivity extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_flag);
        TextView textView = (TextView) findViewById(R.id.textView);
        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.getString("PIN").equals("showmetheflag")) {
            return;
        }
        String flag = getFlag(getString(R.string.build_number), getString(R.string.app_string));
        Log.d("[Kapi]", "[!] Plain: " + flag);
        Toast.makeText(getApplication(), "Check device logs", 1).show();
        textView.setText(flag);
    }

    public static String getFlag(String str, String str2) {
        int parseInt;
        int i;
        String sb = new StringBuilder(" abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+`{[}]:;'\"<,>.?/|ABCDEFGHIJKLMNOPQRSTUVWXYZ").reverse().toString();
        String substring = str.substring(0, 1);
        try {
            Integer.parseInt(str.substring(1, 2));
            parseInt = Integer.parseInt(substring + str.substring(1, 2));
            i = 1;
        } catch (Exception unused) {
            parseInt = Integer.parseInt(substring);
            i = 0;
        }
        double pow = Math.pow(parseInt, 2.0d) - 1.0d;
        String substring2 = str.substring(i + 2, i + 3);
        int i2 = i + 1;
        String substring3 = str.substring(i2 + 2, i2 + 4);
        int parseInt2 = Integer.parseInt(substring3);
        int i3 = i2 + 6;
        String substring4 = str.substring(i2 + 5, i3);
        int i4 = i2 + 7;
        try {
            substring4 = substring4 + str.substring(i3, i4);
            Integer.parseInt(substring4);
        } catch (Exception unused2) {
        }
        int i5 = i2 + 8;
        try {
            substring4 = substring4 + str.substring(i4, i5);
            Integer.parseInt(substring4);
        } catch (Exception unused3) {
        }
        try {
            substring4 = substring4 + str.substring(i5, i2 + 9);
            Integer.parseInt(substring4);
        } catch (Exception unused4) {
        }
        int parseInt3 = Integer.parseInt(substring4);
        if (parseInt3 <= pow) {
            String str3 = parseInt + "(" + substring2 + substring3 + ")" + parseInt3;
        }
        ArrayList arrayList = new ArrayList();
        for (int i6 = 0; i6 < pow + 1.0d; i6++) {
            arrayList.add(Integer.valueOf(i6));
        }
        String str4 = str2;
        int i7 = parseInt3;
        int i8 = 0;
        while (i8 < str2.length()) {
            int i9 = i8 + 0;
            i8++;
            String substring5 = str2.substring(i9, i8);
            if (substring2.equals("-")) {
                i7 %= 93;
                int indexOf = (" abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+`{[}]:;'\"<,>.?/|ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(substring5) + i7) % 93;
                str4 = str4 + " abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+`{[}]:;'\"<,>.?/|ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(indexOf, indexOf + 1);
            } else if (substring2.equals("+")) {
                i7 %= 93;
                int indexOf2 = (sb.indexOf(substring5) + i7) % 93;
                str4 = str4 + sb.substring(indexOf2, indexOf2 + 1);
            }
            i7 += parseInt + parseInt2;
        }
        return str4.substring(str2.length(), str4.length());
    }
}
