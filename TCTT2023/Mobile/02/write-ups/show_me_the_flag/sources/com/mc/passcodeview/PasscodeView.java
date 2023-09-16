package com.mc.passcodeview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public class PasscodeView extends FrameLayout implements View.OnClickListener {
    private String correctInputTip;
    private int correctStatusColor;
    private View cursor;
    private String firstInputTip;
    private ImageView iv_lock;
    private ImageView iv_ok;
    private ViewGroup layout_psd;
    private PasscodeViewListener listener;
    private String localPasscode;
    private int normalStatusColor;
    private TextView number0;
    private TextView number1;
    private TextView number2;
    private TextView number3;
    private TextView number4;
    private TextView number5;
    private TextView number6;
    private TextView number7;
    private TextView number8;
    private TextView number9;
    private ImageView numberB;
    private ImageView numberOK;
    private int numberTextColor;
    private int passcodeLength;
    private int passcodeType;
    private boolean secondInput;
    private String secondInputTip;
    private TextView tv_input_tip;
    private String wrongInputTip;
    private String wrongLengthTip;
    private int wrongStatusColor;

    /* loaded from: classes.dex */
    public interface PasscodeViewListener {
        void onFail(String str);

        void onSuccess(String str);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PasscodeViewType {
        public static final int TYPE_CHECK_PASSCODE = 1;
        public static final int TYPE_SET_PASSCODE = 0;
    }

    public PasscodeView(Context context) {
        this(context, null);
    }

    public PasscodeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.localPasscode = "";
        this.firstInputTip = "Enter a passcode of 4 digits";
        this.secondInputTip = "Re-enter new passcode";
        this.wrongLengthTip = "Enter a passcode of 4 digits";
        this.wrongInputTip = "Passcode do not match";
        this.correctInputTip = "Passcode is correct";
        this.passcodeLength = 4;
        this.correctStatusColor = -10369696;
        this.wrongStatusColor = -901035;
        this.normalStatusColor = -1;
        this.numberTextColor = -9145228;
        this.passcodeType = 0;
        inflate(getContext(), R.layout.layout_passcode_view, this);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.PasscodeView);
        try {
            this.passcodeType = obtainStyledAttributes.getInt(R.styleable.PasscodeView_passcodeViewType, this.passcodeType);
            this.passcodeLength = obtainStyledAttributes.getInt(R.styleable.PasscodeView_passcodeLength, this.passcodeLength);
            this.normalStatusColor = obtainStyledAttributes.getColor(R.styleable.PasscodeView_normalStateColor, this.normalStatusColor);
            this.wrongStatusColor = obtainStyledAttributes.getColor(R.styleable.PasscodeView_wrongStateColor, this.wrongStatusColor);
            this.correctStatusColor = obtainStyledAttributes.getColor(R.styleable.PasscodeView_correctStateColor, this.correctStatusColor);
            this.numberTextColor = obtainStyledAttributes.getColor(R.styleable.PasscodeView_numberTextColor, this.numberTextColor);
            this.firstInputTip = obtainStyledAttributes.getString(R.styleable.PasscodeView_firstInputTip);
            this.secondInputTip = obtainStyledAttributes.getString(R.styleable.PasscodeView_secondInputTip);
            this.wrongLengthTip = obtainStyledAttributes.getString(R.styleable.PasscodeView_wrongLengthTip);
            this.wrongInputTip = obtainStyledAttributes.getString(R.styleable.PasscodeView_wrongInputTip);
            this.correctInputTip = obtainStyledAttributes.getString(R.styleable.PasscodeView_correctInputTip);
            obtainStyledAttributes.recycle();
            String str = this.firstInputTip;
            this.firstInputTip = str == null ? "Enter a passcode of 4 digits" : str;
            String str2 = this.secondInputTip;
            this.secondInputTip = str2 == null ? "Re-enter new passcode" : str2;
            String str3 = this.wrongLengthTip;
            this.wrongLengthTip = str3 == null ? this.firstInputTip : str3;
            String str4 = this.wrongInputTip;
            this.wrongInputTip = str4 == null ? "Passcode do not match" : str4;
            String str5 = this.correctInputTip;
            this.correctInputTip = str5 == null ? "Passcode is correct" : str5;
            init();
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    private void init() {
        this.layout_psd = (ViewGroup) findViewById(R.id.layout_psd);
        this.tv_input_tip = (TextView) findViewById(R.id.tv_input_tip);
        this.cursor = findViewById(R.id.cursor);
        this.iv_lock = (ImageView) findViewById(R.id.iv_lock);
        this.iv_ok = (ImageView) findViewById(R.id.iv_ok);
        this.tv_input_tip.setText(this.firstInputTip);
        this.number0 = (TextView) findViewById(R.id.number0);
        this.number1 = (TextView) findViewById(R.id.number1);
        this.number2 = (TextView) findViewById(R.id.number2);
        this.number3 = (TextView) findViewById(R.id.number3);
        this.number4 = (TextView) findViewById(R.id.number4);
        this.number5 = (TextView) findViewById(R.id.number5);
        this.number6 = (TextView) findViewById(R.id.number6);
        this.number7 = (TextView) findViewById(R.id.number7);
        this.number8 = (TextView) findViewById(R.id.number8);
        this.number9 = (TextView) findViewById(R.id.number9);
        this.numberOK = (ImageView) findViewById(R.id.numberOK);
        this.numberB = (ImageView) findViewById(R.id.numberB);
        this.number0.setOnClickListener(this);
        this.number1.setOnClickListener(this);
        this.number2.setOnClickListener(this);
        this.number3.setOnClickListener(this);
        this.number4.setOnClickListener(this);
        this.number5.setOnClickListener(this);
        this.number6.setOnClickListener(this);
        this.number7.setOnClickListener(this);
        this.number8.setOnClickListener(this);
        this.number9.setOnClickListener(this);
        this.numberB.setOnClickListener(new View.OnClickListener() { // from class: com.mc.passcodeview.PasscodeView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PasscodeView.this.deleteChar();
            }
        });
        this.numberB.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.mc.passcodeview.PasscodeView.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                PasscodeView.this.deleteAllChars();
                return true;
            }
        });
        this.numberOK.setOnClickListener(new View.OnClickListener() { // from class: com.mc.passcodeview.PasscodeView.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PasscodeView.this.next();
            }
        });
        tintImageView(this.iv_lock, this.numberTextColor);
        tintImageView(this.numberB, this.numberTextColor);
        tintImageView(this.numberOK, this.numberTextColor);
        tintImageView(this.iv_ok, this.correctStatusColor);
        this.number0.setTag(0);
        this.number1.setTag(1);
        this.number2.setTag(2);
        this.number3.setTag(3);
        this.number4.setTag(4);
        this.number5.setTag(5);
        this.number6.setTag(6);
        this.number7.setTag(7);
        this.number8.setTag(8);
        this.number9.setTag(9);
        this.number0.setTextColor(this.numberTextColor);
        this.number1.setTextColor(this.numberTextColor);
        this.number2.setTextColor(this.numberTextColor);
        this.number3.setTextColor(this.numberTextColor);
        this.number4.setTextColor(this.numberTextColor);
        this.number5.setTextColor(this.numberTextColor);
        this.number6.setTextColor(this.numberTextColor);
        this.number7.setTextColor(this.numberTextColor);
        this.number8.setTextColor(this.numberTextColor);
        this.number9.setTextColor(this.numberTextColor);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        addChar(((Integer) view.getTag()).intValue());
    }

    public String getLocalPasscode() {
        return this.localPasscode;
    }

    public PasscodeView setLocalPasscode(String str) {
        for (int i = 0; i < str.length(); i++) {
            str.charAt(i);
        }
        this.localPasscode = str;
        this.passcodeType = 1;
        return this;
    }

    public PasscodeViewListener getListener() {
        return this.listener;
    }

    public PasscodeView setListener(PasscodeViewListener passcodeViewListener) {
        this.listener = passcodeViewListener;
        return this;
    }

    public String getFirstInputTip() {
        return this.firstInputTip;
    }

    public PasscodeView setFirstInputTip(String str) {
        this.firstInputTip = str;
        return this;
    }

    public String getSecondInputTip() {
        return this.secondInputTip;
    }

    public PasscodeView setSecondInputTip(String str) {
        this.secondInputTip = str;
        return this;
    }

    public String getWrongLengthTip() {
        return this.wrongLengthTip;
    }

    public PasscodeView setWrongLengthTip(String str) {
        this.wrongLengthTip = str;
        return this;
    }

    public String getWrongInputTip() {
        return this.wrongInputTip;
    }

    public PasscodeView setWrongInputTip(String str) {
        this.wrongInputTip = str;
        return this;
    }

    public String getCorrectInputTip() {
        return this.correctInputTip;
    }

    public PasscodeView setCorrectInputTip(String str) {
        this.correctInputTip = str;
        return this;
    }

    public int getPasscodeLength() {
        return this.passcodeLength;
    }

    public PasscodeView setPasscodeLength(int i) {
        this.passcodeLength = i;
        return this;
    }

    public int getCorrectStatusColor() {
        return this.correctStatusColor;
    }

    public PasscodeView setCorrectStatusColor(int i) {
        this.correctStatusColor = i;
        return this;
    }

    public int getWrongStatusColor() {
        return this.wrongStatusColor;
    }

    public PasscodeView setWrongStatusColor(int i) {
        this.wrongStatusColor = i;
        return this;
    }

    public int getNormalStatusColor() {
        return this.normalStatusColor;
    }

    public PasscodeView setNormalStatusColor(int i) {
        this.normalStatusColor = i;
        return this;
    }

    public int getNumberTextColor() {
        return this.numberTextColor;
    }

    public PasscodeView setNumberTextColor(int i) {
        this.numberTextColor = i;
        return this;
    }

    public int getPasscodeType() {
        return this.passcodeType;
    }

    public PasscodeView setPasscodeType(int i) {
        this.passcodeType = i;
        return this;
    }

    protected boolean equals(String str) {
        return this.localPasscode.equals(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void next() {
        if (this.passcodeType == 1 && TextUtils.isEmpty(this.localPasscode)) {
            throw new RuntimeException("must set localPasscode when type is TYPE_CHECK_PASSCODE");
        }
        String passcodeFromView = getPasscodeFromView();
        if (passcodeFromView.length() != this.passcodeLength) {
            this.tv_input_tip.setText(this.wrongLengthTip);
            runTipTextAnimation();
        } else if (this.passcodeType == 0 && !this.secondInput) {
            this.tv_input_tip.setText(this.secondInputTip);
            this.localPasscode = passcodeFromView;
            clearChar();
            this.secondInput = true;
        } else if (equals(passcodeFromView)) {
            runOkAnimation();
        } else {
            runWrongAnimation();
        }
    }

    private void addChar(int i) {
        if (this.layout_psd.getChildCount() >= this.passcodeLength) {
            return;
        }
        CircleView circleView = new CircleView(getContext());
        int dpToPx = dpToPx(8.0f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpToPx, dpToPx);
        layoutParams.setMargins(dpToPx, 0, dpToPx, 0);
        circleView.setLayoutParams(layoutParams);
        circleView.setColor(this.normalStatusColor);
        circleView.setTag(Integer.valueOf(i));
        this.layout_psd.addView(circleView);
    }

    private int dpToPx(float f) {
        return (int) TypedValue.applyDimension(1, f, getResources().getDisplayMetrics());
    }

    private void tintImageView(ImageView imageView, int i) {
        imageView.getDrawable().mutate().setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
    }

    private void clearChar() {
        this.layout_psd.removeAllViews();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteChar() {
        int childCount = this.layout_psd.getChildCount();
        if (childCount <= 0) {
            return;
        }
        this.layout_psd.removeViewAt(childCount - 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteAllChars() {
        if (this.layout_psd.getChildCount() <= 0) {
            return;
        }
        this.layout_psd.removeAllViews();
    }

    public void runTipTextAnimation() {
        shakeAnimator(this.tv_input_tip).start();
    }

    public void runWrongAnimation() {
        this.cursor.setTranslationX(0.0f);
        this.cursor.setVisibility(0);
        this.cursor.animate().translationX(this.layout_psd.getWidth()).setDuration(600L).setListener(new AnimatorListenerAdapter() { // from class: com.mc.passcodeview.PasscodeView.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                PasscodeView.this.cursor.setVisibility(4);
                PasscodeView.this.tv_input_tip.setText(PasscodeView.this.wrongInputTip);
                PasscodeView passcodeView = PasscodeView.this;
                passcodeView.setPSDViewBackgroundResource(passcodeView.wrongStatusColor);
                PasscodeView passcodeView2 = PasscodeView.this;
                Animator shakeAnimator = passcodeView2.shakeAnimator(passcodeView2.layout_psd);
                shakeAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.mc.passcodeview.PasscodeView.4.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator2) {
                        super.onAnimationEnd(animator2);
                        PasscodeView.this.setPSDViewBackgroundResource(PasscodeView.this.normalStatusColor);
                        if (!PasscodeView.this.secondInput || PasscodeView.this.listener == null) {
                            return;
                        }
                        PasscodeView.this.listener.onFail(PasscodeView.this.getPasscodeFromView());
                    }
                });
                shakeAnimator.start();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Animator shakeAnimator(View view) {
        return ObjectAnimator.ofFloat(view, "translationX", 0.0f, 25.0f, -25.0f, 25.0f, -25.0f, 15.0f, -15.0f, 6.0f, -6.0f, 0.0f).setDuration(500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPSDViewBackgroundResource(int i) {
        int childCount = this.layout_psd.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            ((CircleView) this.layout_psd.getChildAt(i2)).setColor(i);
        }
    }

    public void runOkAnimation() {
        this.cursor.setTranslationX(0.0f);
        this.cursor.setVisibility(0);
        this.cursor.animate().setDuration(600L).translationX(this.layout_psd.getWidth()).setListener(new AnimatorListenerAdapter() { // from class: com.mc.passcodeview.PasscodeView.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                PasscodeView.this.cursor.setVisibility(4);
                PasscodeView passcodeView = PasscodeView.this;
                passcodeView.setPSDViewBackgroundResource(passcodeView.correctStatusColor);
                PasscodeView.this.tv_input_tip.setText(PasscodeView.this.correctInputTip);
                PasscodeView.this.iv_lock.animate().alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setDuration(500L).start();
                PasscodeView.this.iv_ok.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(500L).setListener(new AnimatorListenerAdapter() { // from class: com.mc.passcodeview.PasscodeView.5.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator2) {
                        super.onAnimationEnd(animator2);
                        if (PasscodeView.this.listener != null) {
                            PasscodeView.this.listener.onSuccess(PasscodeView.this.getPasscodeFromView());
                        }
                    }
                }).start();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getPasscodeFromView() {
        StringBuilder sb = new StringBuilder();
        int childCount = this.layout_psd.getChildCount();
        for (int i = 0; i < childCount; i++) {
            sb.append(((Integer) this.layout_psd.getChildAt(i).getTag()).intValue());
        }
        return sb.toString();
    }
}
