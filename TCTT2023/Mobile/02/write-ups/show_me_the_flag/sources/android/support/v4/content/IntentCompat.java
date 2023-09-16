package android.support.v4.content;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
/* loaded from: classes.dex */
public final class IntentCompat {
    @Deprecated
    public static final String ACTION_EXTERNAL_APPLICATIONS_AVAILABLE = "android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE";
    @Deprecated
    public static final String ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE = "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE";
    public static final String CATEGORY_LEANBACK_LAUNCHER = "android.intent.category.LEANBACK_LAUNCHER";
    @Deprecated
    public static final String EXTRA_CHANGED_PACKAGE_LIST = "android.intent.extra.changed_package_list";
    @Deprecated
    public static final String EXTRA_CHANGED_UID_LIST = "android.intent.extra.changed_uid_list";
    public static final String EXTRA_HTML_TEXT = "android.intent.extra.HTML_TEXT";
    public static final String EXTRA_START_PLAYBACK = "android.intent.extra.START_PLAYBACK";
    @Deprecated
    public static final int FLAG_ACTIVITY_CLEAR_TASK = 32768;
    @Deprecated
    public static final int FLAG_ACTIVITY_TASK_ON_HOME = 16384;
    private static final IntentCompatBaseImpl IMPL;

    /* loaded from: classes.dex */
    static class IntentCompatBaseImpl {
        IntentCompatBaseImpl() {
        }

        public Intent makeMainSelectorActivity(String str, String str2) {
            Intent intent = new Intent(str);
            intent.addCategory(str2);
            return intent;
        }
    }

    /* loaded from: classes.dex */
    static class IntentCompatApi15Impl extends IntentCompatBaseImpl {
        IntentCompatApi15Impl() {
        }

        @Override // android.support.v4.content.IntentCompat.IntentCompatBaseImpl
        public Intent makeMainSelectorActivity(String str, String str2) {
            return Intent.makeMainSelectorActivity(str, str2);
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 15) {
            IMPL = new IntentCompatApi15Impl();
        } else {
            IMPL = new IntentCompatBaseImpl();
        }
    }

    private IntentCompat() {
    }

    @Deprecated
    public static Intent makeMainActivity(ComponentName componentName) {
        return Intent.makeMainActivity(componentName);
    }

    public static Intent makeMainSelectorActivity(String str, String str2) {
        return IMPL.makeMainSelectorActivity(str, str2);
    }

    @Deprecated
    public static Intent makeRestartActivityTask(ComponentName componentName) {
        return Intent.makeRestartActivityTask(componentName);
    }
}
