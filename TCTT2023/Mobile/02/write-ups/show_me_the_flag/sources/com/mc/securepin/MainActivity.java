package com.mc.securepin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.mc.passcodeview.PasscodeView;
/* loaded from: classes.dex */
public class MainActivity extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(1280);
            getWindow().setStatusBarColor(0);
        }
        setContentView(R.layout.activity_main);
        ((PasscodeView) findViewById(R.id.passcodeView)).setPasscodeLength(4).setLocalPasscode("YourPINNeverMatched").setListener(new PasscodeView.PasscodeViewListener() { // from class: com.mc.securepin.MainActivity.1
            @Override // com.mc.passcodeview.PasscodeView.PasscodeViewListener
            public void onFail(String str) {
                Toast.makeText(MainActivity.this.getApplication(), "Incorrect PIN!!", 0).show();
            }

            @Override // com.mc.passcodeview.PasscodeView.PasscodeViewListener
            public void onSuccess(String str) {
                Toast.makeText(MainActivity.this.getApplication(), "Correct PIN!!", 0).show();
                Intent intent = new Intent(MainActivity.this, FlagActivity.class);
                intent.putExtra("PIN", "Hello world");
                MainActivity.this.startActivity(intent);
            }
        });
    }
}
