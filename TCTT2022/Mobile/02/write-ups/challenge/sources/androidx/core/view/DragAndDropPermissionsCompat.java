package androidx.core.view;

import android.app.Activity;
import android.os.Build;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;
/* loaded from: classes.dex */
public final class DragAndDropPermissionsCompat {
    private final DragAndDropPermissions mDragAndDropPermissions;

    private DragAndDropPermissionsCompat(DragAndDropPermissions dragAndDropPermissions) {
        this.mDragAndDropPermissions = dragAndDropPermissions;
    }

    public static DragAndDropPermissionsCompat request(Activity activity, DragEvent dragEvent) {
        DragAndDropPermissions requestDragAndDropPermissions;
        if (Build.VERSION.SDK_INT < 24 || (requestDragAndDropPermissions = Api24Impl.requestDragAndDropPermissions(activity, dragEvent)) == null) {
            return null;
        }
        return new DragAndDropPermissionsCompat(requestDragAndDropPermissions);
    }

    public void release() {
        if (Build.VERSION.SDK_INT >= 24) {
            Api24Impl.release(this.mDragAndDropPermissions);
        }
    }

    /* loaded from: classes.dex */
    static class Api24Impl {
        private Api24Impl() {
        }

        static DragAndDropPermissions requestDragAndDropPermissions(Activity activity, DragEvent dragEvent) {
            return activity.requestDragAndDropPermissions(dragEvent);
        }

        static void release(DragAndDropPermissions dragAndDropPermissions) {
            dragAndDropPermissions.release();
        }
    }
}
