package androidx.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
@Deprecated
/* loaded from: classes.dex */
public interface LayoutInflaterFactory {
    View onCreateView(View parent, String name, Context context, AttributeSet attrs);
}
