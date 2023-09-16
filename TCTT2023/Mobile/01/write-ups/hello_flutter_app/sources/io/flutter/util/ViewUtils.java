package io.flutter.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
/* loaded from: classes.dex */
public final class ViewUtils {

    /* loaded from: classes.dex */
    public interface ViewVisitor {
        boolean run(View view);
    }

    public static Activity getActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (!(context instanceof ContextWrapper)) {
            return null;
        }
        return getActivity(((ContextWrapper) context).getBaseContext());
    }

    public static int generateViewId(int fallbackId) {
        if (Build.VERSION.SDK_INT >= 17) {
            return View.generateViewId();
        }
        return fallbackId;
    }

    public static boolean childHasFocus(View root) {
        return traverseHierarchy(root, new ViewVisitor() { // from class: io.flutter.util.ViewUtils$$ExternalSyntheticLambda0
            @Override // io.flutter.util.ViewUtils.ViewVisitor
            public final boolean run(View view) {
                boolean hasFocus;
                hasFocus = view.hasFocus();
                return hasFocus;
            }
        });
    }

    public static boolean hasChildViewOfType(View root, final Class<? extends View>[] viewTypes) {
        return traverseHierarchy(root, new ViewVisitor() { // from class: io.flutter.util.ViewUtils$$ExternalSyntheticLambda1
            @Override // io.flutter.util.ViewUtils.ViewVisitor
            public final boolean run(View view) {
                return ViewUtils.lambda$hasChildViewOfType$1(viewTypes, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$hasChildViewOfType$1(Class[] viewTypes, View view) {
        for (Class cls : viewTypes) {
            if (cls.isInstance(view)) {
                return true;
            }
        }
        return false;
    }

    public static boolean traverseHierarchy(View root, ViewVisitor visitor) {
        if (root == null) {
            return false;
        }
        if (visitor.run(root)) {
            return true;
        }
        if (root instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) root;
            for (int idx = 0; idx < viewGroup.getChildCount(); idx++) {
                if (traverseHierarchy(viewGroup.getChildAt(idx), visitor)) {
                    return true;
                }
            }
        }
        return false;
    }
}
