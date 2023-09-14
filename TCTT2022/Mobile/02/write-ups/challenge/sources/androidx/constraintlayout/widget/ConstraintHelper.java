package androidx.constraintlayout.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Helper;
import androidx.constraintlayout.core.widgets.HelperWidget;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.R;
import java.util.Arrays;
import java.util.HashMap;
/* loaded from: classes.dex */
public abstract class ConstraintHelper extends View {
    protected int mCount;
    protected Helper mHelperWidget;
    protected int[] mIds;
    protected HashMap<Integer, String> mMap;
    protected String mReferenceIds;
    protected String mReferenceTags;
    protected boolean mUseViewMeasure;
    private View[] mViews;
    protected Context myContext;

    /* JADX INFO: Access modifiers changed from: protected */
    public void applyLayoutFeaturesInConstraintSet(ConstraintLayout container) {
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
    }

    public void resolveRtl(ConstraintWidget widget, boolean isRtl) {
    }

    public void updatePostConstraints(ConstraintLayout container) {
    }

    public void updatePostLayout(ConstraintLayout container) {
    }

    public void updatePostMeasure(ConstraintLayout container) {
    }

    public void updatePreDraw(ConstraintLayout container) {
    }

    public ConstraintHelper(Context context) {
        super(context);
        this.mIds = new int[32];
        this.mUseViewMeasure = false;
        this.mViews = null;
        this.mMap = new HashMap<>();
        this.myContext = context;
        init(null);
    }

    public ConstraintHelper(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIds = new int[32];
        this.mUseViewMeasure = false;
        this.mViews = null;
        this.mMap = new HashMap<>();
        this.myContext = context;
        init(attrs);
    }

    public ConstraintHelper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIds = new int[32];
        this.mUseViewMeasure = false;
        this.mViews = null;
        this.mMap = new HashMap<>();
        this.myContext = context;
        init(attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout);
            int indexCount = obtainStyledAttributes.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int index = obtainStyledAttributes.getIndex(i);
                if (index == R.styleable.ConstraintLayout_Layout_constraint_referenced_ids) {
                    String string = obtainStyledAttributes.getString(index);
                    this.mReferenceIds = string;
                    setIds(string);
                } else if (index == R.styleable.ConstraintLayout_Layout_constraint_referenced_tags) {
                    String string2 = obtainStyledAttributes.getString(index);
                    this.mReferenceTags = string2;
                    setReferenceTags(string2);
                }
            }
            obtainStyledAttributes.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        String str = this.mReferenceIds;
        if (str != null) {
            setIds(str);
        }
        String str2 = this.mReferenceTags;
        if (str2 != null) {
            setReferenceTags(str2);
        }
    }

    public void addView(View view) {
        if (view == this) {
            return;
        }
        if (view.getId() == -1) {
            Log.e("ConstraintHelper", "Views added to a ConstraintHelper need to have an id");
        } else if (view.getParent() == null) {
            Log.e("ConstraintHelper", "Views added to a ConstraintHelper need to have a parent");
        } else {
            this.mReferenceIds = null;
            addRscID(view.getId());
            requestLayout();
        }
    }

    public int removeView(View view) {
        int i;
        int id = view.getId();
        int i2 = -1;
        if (id == -1) {
            return -1;
        }
        this.mReferenceIds = null;
        int i3 = 0;
        while (true) {
            if (i3 >= this.mCount) {
                break;
            } else if (this.mIds[i3] == id) {
                int i4 = i3;
                while (true) {
                    i = this.mCount;
                    if (i4 >= i - 1) {
                        break;
                    }
                    int[] iArr = this.mIds;
                    int i5 = i4 + 1;
                    iArr[i4] = iArr[i5];
                    i4 = i5;
                }
                this.mIds[i - 1] = 0;
                this.mCount = i - 1;
                i2 = i3;
            } else {
                i3++;
            }
        }
        requestLayout();
        return i2;
    }

    public int[] getReferencedIds() {
        return Arrays.copyOf(this.mIds, this.mCount);
    }

    public void setReferencedIds(int[] ids) {
        this.mReferenceIds = null;
        this.mCount = 0;
        for (int i : ids) {
            addRscID(i);
        }
    }

    private void addRscID(int id) {
        if (id == getId()) {
            return;
        }
        int i = this.mCount + 1;
        int[] iArr = this.mIds;
        if (i > iArr.length) {
            this.mIds = Arrays.copyOf(iArr, iArr.length * 2);
        }
        int[] iArr2 = this.mIds;
        int i2 = this.mCount;
        iArr2[i2] = id;
        this.mCount = i2 + 1;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mUseViewMeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            setMeasuredDimension(0, 0);
        }
    }

    public void validateParams() {
        if (this.mHelperWidget == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams instanceof ConstraintLayout.LayoutParams) {
            ((ConstraintLayout.LayoutParams) layoutParams).widget = (ConstraintWidget) this.mHelperWidget;
        }
    }

    private void addID(String idString) {
        if (idString == null || idString.length() == 0 || this.myContext == null) {
            return;
        }
        String trim = idString.trim();
        if (getParent() instanceof ConstraintLayout) {
            ConstraintLayout constraintLayout = (ConstraintLayout) getParent();
        }
        int findId = findId(trim);
        if (findId != 0) {
            this.mMap.put(Integer.valueOf(findId), trim);
            addRscID(findId);
            return;
        }
        Log.w("ConstraintHelper", "Could not find id of \"" + trim + "\"");
    }

    private void addTag(String tagString) {
        if (tagString == null || tagString.length() == 0 || this.myContext == null) {
            return;
        }
        String trim = tagString.trim();
        ConstraintLayout constraintLayout = getParent() instanceof ConstraintLayout ? (ConstraintLayout) getParent() : null;
        if (constraintLayout == null) {
            Log.w("ConstraintHelper", "Parent not a ConstraintLayout");
            return;
        }
        int childCount = constraintLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = constraintLayout.getChildAt(i);
            ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
            if ((layoutParams instanceof ConstraintLayout.LayoutParams) && trim.equals(((ConstraintLayout.LayoutParams) layoutParams).constraintTag)) {
                if (childAt.getId() == -1) {
                    Log.w("ConstraintHelper", "to use ConstraintTag view " + childAt.getClass().getSimpleName() + " must have an ID");
                } else {
                    addRscID(childAt.getId());
                }
            }
        }
    }

    private int findId(String referenceId) {
        ConstraintLayout constraintLayout = getParent() instanceof ConstraintLayout ? (ConstraintLayout) getParent() : null;
        int i = 0;
        if (isInEditMode() && constraintLayout != null) {
            Object designInformation = constraintLayout.getDesignInformation(0, referenceId);
            if (designInformation instanceof Integer) {
                i = ((Integer) designInformation).intValue();
            }
        }
        if (i == 0 && constraintLayout != null) {
            i = findId(constraintLayout, referenceId);
        }
        if (i == 0) {
            try {
                i = R.id.class.getField(referenceId).getInt(null);
            } catch (Exception unused) {
            }
        }
        return i == 0 ? this.myContext.getResources().getIdentifier(referenceId, "id", this.myContext.getPackageName()) : i;
    }

    private int findId(ConstraintLayout container, String idString) {
        Resources resources;
        if (idString == null || container == null || (resources = this.myContext.getResources()) == null) {
            return 0;
        }
        int childCount = container.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = container.getChildAt(i);
            if (childAt.getId() != -1) {
                String str = null;
                try {
                    str = resources.getResourceEntryName(childAt.getId());
                } catch (Resources.NotFoundException unused) {
                }
                if (idString.equals(str)) {
                    return childAt.getId();
                }
            }
        }
        return 0;
    }

    protected void setIds(String idList) {
        this.mReferenceIds = idList;
        if (idList == null) {
            return;
        }
        int i = 0;
        this.mCount = 0;
        while (true) {
            int indexOf = idList.indexOf(44, i);
            if (indexOf == -1) {
                addID(idList.substring(i));
                return;
            } else {
                addID(idList.substring(i, indexOf));
                i = indexOf + 1;
            }
        }
    }

    protected void setReferenceTags(String tagList) {
        this.mReferenceTags = tagList;
        if (tagList == null) {
            return;
        }
        int i = 0;
        this.mCount = 0;
        while (true) {
            int indexOf = tagList.indexOf(44, i);
            if (indexOf == -1) {
                addTag(tagList.substring(i));
                return;
            } else {
                addTag(tagList.substring(i, indexOf));
                i = indexOf + 1;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void applyLayoutFeatures(ConstraintLayout container) {
        int visibility = getVisibility();
        float elevation = Build.VERSION.SDK_INT >= 21 ? getElevation() : 0.0f;
        for (int i = 0; i < this.mCount; i++) {
            View viewById = container.getViewById(this.mIds[i]);
            if (viewById != null) {
                viewById.setVisibility(visibility);
                if (elevation > 0.0f && Build.VERSION.SDK_INT >= 21) {
                    viewById.setTranslationZ(viewById.getTranslationZ() + elevation);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void applyLayoutFeatures() {
        ViewParent parent = getParent();
        if (parent == null || !(parent instanceof ConstraintLayout)) {
            return;
        }
        applyLayoutFeatures((ConstraintLayout) parent);
    }

    public void updatePreLayout(ConstraintLayout container) {
        String str;
        int findId;
        if (isInEditMode()) {
            setIds(this.mReferenceIds);
        }
        Helper helper = this.mHelperWidget;
        if (helper == null) {
            return;
        }
        helper.removeAllIds();
        for (int i = 0; i < this.mCount; i++) {
            int i2 = this.mIds[i];
            View viewById = container.getViewById(i2);
            if (viewById == null && (findId = findId(container, (str = this.mMap.get(Integer.valueOf(i2))))) != 0) {
                this.mIds[i] = findId;
                this.mMap.put(Integer.valueOf(findId), str);
                viewById = container.getViewById(findId);
            }
            if (viewById != null) {
                this.mHelperWidget.add(container.getViewWidget(viewById));
            }
        }
        this.mHelperWidget.updateConstraints(container.mLayoutWidget);
    }

    public void updatePreLayout(ConstraintWidgetContainer container, Helper helper, SparseArray<ConstraintWidget> map) {
        helper.removeAllIds();
        for (int i = 0; i < this.mCount; i++) {
            helper.add(map.get(this.mIds[i]));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public View[] getViews(ConstraintLayout layout) {
        View[] viewArr = this.mViews;
        if (viewArr == null || viewArr.length != this.mCount) {
            this.mViews = new View[this.mCount];
        }
        for (int i = 0; i < this.mCount; i++) {
            this.mViews[i] = layout.getViewById(this.mIds[i]);
        }
        return this.mViews;
    }

    public void loadParameters(ConstraintSet.Constraint constraint, HelperWidget child, ConstraintLayout.LayoutParams layoutParams, SparseArray<ConstraintWidget> mapIdToWidget) {
        if (constraint.layout.mReferenceIds != null) {
            setReferencedIds(constraint.layout.mReferenceIds);
        } else if (constraint.layout.mReferenceIdString != null) {
            if (constraint.layout.mReferenceIdString.length() > 0) {
                constraint.layout.mReferenceIds = convertReferenceString(this, constraint.layout.mReferenceIdString);
            } else {
                constraint.layout.mReferenceIds = null;
            }
        }
        if (child != null) {
            child.removeAllIds();
            if (constraint.layout.mReferenceIds != null) {
                for (int i = 0; i < constraint.layout.mReferenceIds.length; i++) {
                    ConstraintWidget constraintWidget = mapIdToWidget.get(constraint.layout.mReferenceIds[i]);
                    if (constraintWidget != null) {
                        child.add(constraintWidget);
                    }
                }
            }
        }
    }

    private int[] convertReferenceString(View view, String referenceIdString) {
        String[] split = referenceIdString.split(",");
        view.getContext();
        int[] iArr = new int[split.length];
        int i = 0;
        for (String str : split) {
            int findId = findId(str.trim());
            if (findId != 0) {
                iArr[i] = findId;
                i++;
            }
        }
        return i != split.length ? Arrays.copyOf(iArr, i) : iArr;
    }

    @Override // android.view.View
    public void setTag(int key, Object tag) {
        super.setTag(key, tag);
        if (tag == null && this.mReferenceIds == null) {
            addRscID(key);
        }
    }

    public boolean containsId(final int id) {
        for (int i : this.mIds) {
            if (i == id) {
                return true;
            }
        }
        return false;
    }

    public int indexFromId(final int id) {
        int i = -1;
        for (int i2 : this.mIds) {
            i++;
            if (i2 == id) {
                return i;
            }
        }
        return i;
    }
}
