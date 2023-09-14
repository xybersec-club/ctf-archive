package androidx.lifecycle;

import android.view.View;
import android.view.ViewParent;
import androidx.lifecycle.runtime.R;
/* loaded from: classes.dex */
public class ViewTreeLifecycleOwner {
    private ViewTreeLifecycleOwner() {
    }

    public static void set(View view, LifecycleOwner lifecycleOwner) {
        view.setTag(R.id.view_tree_lifecycle_owner, lifecycleOwner);
    }

    public static LifecycleOwner get(View view) {
        LifecycleOwner lifecycleOwner = (LifecycleOwner) view.getTag(R.id.view_tree_lifecycle_owner);
        if (lifecycleOwner != null) {
            return lifecycleOwner;
        }
        ViewParent parent = view.getParent();
        while (lifecycleOwner == null && (parent instanceof View)) {
            View view2 = (View) parent;
            lifecycleOwner = (LifecycleOwner) view2.getTag(R.id.view_tree_lifecycle_owner);
            parent = view2.getParent();
        }
        return lifecycleOwner;
    }
}
