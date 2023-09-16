package com.mc.passcodeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
/* loaded from: classes.dex */
public class CircleView extends View {
    private int color;
    private Paint mPaint;

    public CircleView(Context context) {
        super(context);
        this.color = ViewCompat.MEASURED_STATE_MASK;
        init();
    }

    public CircleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.color = ViewCompat.MEASURED_STATE_MASK;
        init();
    }

    public CircleView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.color = ViewCompat.MEASURED_STATE_MASK;
        init();
    }

    private void init() {
        this.mPaint = new Paint(1);
    }

    public void setColor(int i) {
        this.color = i;
        invalidate();
    }

    public int getColor() {
        return this.color;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        double d;
        double d2;
        double width = (getWidth() - getPaddingLeft()) - getPaddingRight();
        Double.isNaN(width);
        double d3 = width * 0.5d;
        double height = (getHeight() - getPaddingTop()) - getPaddingBottom();
        Double.isNaN(height);
        double d4 = height * 0.5d;
        Double.isNaN(getPaddingLeft());
        Double.isNaN(getPaddingTop());
        this.mPaint.setColor(this.color);
        canvas.drawCircle((int) (d + d3), (int) (d2 + d4), (int) Math.min(d3, d4), this.mPaint);
    }
}
