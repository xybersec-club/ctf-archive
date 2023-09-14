package androidx.constraintlayout.helper.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Helper;
import androidx.constraintlayout.core.widgets.Placeholder;
import androidx.constraintlayout.widget.VirtualLayout;
/* loaded from: classes.dex */
public class MotionPlaceholder extends VirtualLayout {
    private static final String TAG = "MotionPlaceholder";
    Placeholder mPlaceholder;

    @Override // androidx.constraintlayout.widget.ConstraintHelper
    public void updatePreLayout(ConstraintWidgetContainer container, Helper helper, SparseArray<ConstraintWidget> map) {
    }

    public MotionPlaceholder(Context context) {
        super(context);
    }

    public MotionPlaceholder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MotionPlaceholder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MotionPlaceholder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
    }

    @Override // androidx.constraintlayout.widget.ConstraintHelper, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        onMeasure(this.mPlaceholder, widthMeasureSpec, heightMeasureSpec);
    }

    @Override // androidx.constraintlayout.widget.VirtualLayout
    public void onMeasure(androidx.constraintlayout.core.widgets.VirtualLayout layout, int widthMeasureSpec, int heightMeasureSpec) {
        int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        int mode2 = View.MeasureSpec.getMode(heightMeasureSpec);
        int size2 = View.MeasureSpec.getSize(heightMeasureSpec);
        if (layout != null) {
            layout.measure(mode, size, mode2, size2);
            setMeasuredDimension(layout.getMeasuredWidth(), layout.getMeasuredHeight());
            return;
        }
        setMeasuredDimension(0, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.constraintlayout.widget.VirtualLayout, androidx.constraintlayout.widget.ConstraintHelper
    public void init(AttributeSet attrs) {
        super.init(attrs);
        this.mHelperWidget = new Placeholder();
        validateParams();
    }
}
