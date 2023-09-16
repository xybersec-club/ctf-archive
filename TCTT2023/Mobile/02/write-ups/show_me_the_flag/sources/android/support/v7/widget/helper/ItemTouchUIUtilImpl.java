package android.support.v7.widget.helper;

import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v7.recyclerview.R;
import android.support.v7.widget.RecyclerView;
import android.view.View;
/* loaded from: classes.dex */
class ItemTouchUIUtilImpl {
    ItemTouchUIUtilImpl() {
    }

    /* loaded from: classes.dex */
    static class Api21Impl extends BaseImpl {
        @Override // android.support.v7.widget.helper.ItemTouchUIUtilImpl.BaseImpl, android.support.v7.widget.helper.ItemTouchUIUtil
        public void onDraw(Canvas canvas, RecyclerView recyclerView, View view, float f, float f2, int i, boolean z) {
            if (z && view.getTag(R.id.item_touch_helper_previous_elevation) == null) {
                Float valueOf = Float.valueOf(ViewCompat.getElevation(view));
                ViewCompat.setElevation(view, findMaxElevation(recyclerView, view) + 1.0f);
                view.setTag(R.id.item_touch_helper_previous_elevation, valueOf);
            }
            super.onDraw(canvas, recyclerView, view, f, f2, i, z);
        }

        private float findMaxElevation(RecyclerView recyclerView, View view) {
            int childCount = recyclerView.getChildCount();
            float f = 0.0f;
            for (int i = 0; i < childCount; i++) {
                View childAt = recyclerView.getChildAt(i);
                if (childAt != view) {
                    float elevation = ViewCompat.getElevation(childAt);
                    if (elevation > f) {
                        f = elevation;
                    }
                }
            }
            return f;
        }

        @Override // android.support.v7.widget.helper.ItemTouchUIUtilImpl.BaseImpl, android.support.v7.widget.helper.ItemTouchUIUtil
        public void clearView(View view) {
            Object tag = view.getTag(R.id.item_touch_helper_previous_elevation);
            if (tag != null && (tag instanceof Float)) {
                ViewCompat.setElevation(view, ((Float) tag).floatValue());
            }
            view.setTag(R.id.item_touch_helper_previous_elevation, null);
            super.clearView(view);
        }
    }

    /* loaded from: classes.dex */
    static class BaseImpl implements ItemTouchUIUtil {
        @Override // android.support.v7.widget.helper.ItemTouchUIUtil
        public void onDrawOver(Canvas canvas, RecyclerView recyclerView, View view, float f, float f2, int i, boolean z) {
        }

        @Override // android.support.v7.widget.helper.ItemTouchUIUtil
        public void onSelected(View view) {
        }

        @Override // android.support.v7.widget.helper.ItemTouchUIUtil
        public void clearView(View view) {
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
        }

        @Override // android.support.v7.widget.helper.ItemTouchUIUtil
        public void onDraw(Canvas canvas, RecyclerView recyclerView, View view, float f, float f2, int i, boolean z) {
            view.setTranslationX(f);
            view.setTranslationY(f2);
        }
    }
}
