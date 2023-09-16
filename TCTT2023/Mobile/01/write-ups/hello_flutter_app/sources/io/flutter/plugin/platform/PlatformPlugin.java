package io.flutter.plugin.platform;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.view.Window;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.core.view.WindowInsetsControllerCompat;
import io.flutter.Log;
import io.flutter.embedding.engine.systemchannels.PlatformChannel;
import io.flutter.plugin.platform.PlatformPlugin;
import java.io.FileNotFoundException;
import java.util.List;
/* loaded from: classes.dex */
public class PlatformPlugin {
    public static final int DEFAULT_SYSTEM_UI = 1280;
    private static final String TAG = "PlatformPlugin";
    private final Activity activity;
    private PlatformChannel.SystemChromeStyle currentTheme;
    private int mEnabledOverlays;
    final PlatformChannel.PlatformMessageHandler mPlatformMessageHandler;
    private final PlatformChannel platformChannel;
    private final PlatformPluginDelegate platformPluginDelegate;

    /* loaded from: classes.dex */
    public interface PlatformPluginDelegate {
        boolean popSystemNavigator();
    }

    public PlatformPlugin(Activity activity, PlatformChannel platformChannel) {
        this(activity, platformChannel, null);
    }

    public PlatformPlugin(Activity activity, PlatformChannel platformChannel, PlatformPluginDelegate delegate) {
        PlatformChannel.PlatformMessageHandler platformMessageHandler = new PlatformChannel.PlatformMessageHandler() { // from class: io.flutter.plugin.platform.PlatformPlugin.1
            @Override // io.flutter.embedding.engine.systemchannels.PlatformChannel.PlatformMessageHandler
            public void playSystemSound(PlatformChannel.SoundType soundType) {
                PlatformPlugin.this.playSystemSound(soundType);
            }

            @Override // io.flutter.embedding.engine.systemchannels.PlatformChannel.PlatformMessageHandler
            public void vibrateHapticFeedback(PlatformChannel.HapticFeedbackType feedbackType) {
                PlatformPlugin.this.vibrateHapticFeedback(feedbackType);
            }

            @Override // io.flutter.embedding.engine.systemchannels.PlatformChannel.PlatformMessageHandler
            public void setPreferredOrientations(int androidOrientation) {
                PlatformPlugin.this.setSystemChromePreferredOrientations(androidOrientation);
            }

            @Override // io.flutter.embedding.engine.systemchannels.PlatformChannel.PlatformMessageHandler
            public void setApplicationSwitcherDescription(PlatformChannel.AppSwitcherDescription description) {
                PlatformPlugin.this.setSystemChromeApplicationSwitcherDescription(description);
            }

            @Override // io.flutter.embedding.engine.systemchannels.PlatformChannel.PlatformMessageHandler
            public void showSystemOverlays(List<PlatformChannel.SystemUiOverlay> overlays) {
                PlatformPlugin.this.setSystemChromeEnabledSystemUIOverlays(overlays);
            }

            @Override // io.flutter.embedding.engine.systemchannels.PlatformChannel.PlatformMessageHandler
            public void showSystemUiMode(PlatformChannel.SystemUiMode mode) {
                PlatformPlugin.this.setSystemChromeEnabledSystemUIMode(mode);
            }

            @Override // io.flutter.embedding.engine.systemchannels.PlatformChannel.PlatformMessageHandler
            public void setSystemUiChangeListener() {
                PlatformPlugin.this.setSystemChromeChangeListener();
            }

            @Override // io.flutter.embedding.engine.systemchannels.PlatformChannel.PlatformMessageHandler
            public void restoreSystemUiOverlays() {
                PlatformPlugin.this.restoreSystemChromeSystemUIOverlays();
            }

            @Override // io.flutter.embedding.engine.systemchannels.PlatformChannel.PlatformMessageHandler
            public void setSystemUiOverlayStyle(PlatformChannel.SystemChromeStyle systemUiOverlayStyle) {
                PlatformPlugin.this.setSystemChromeSystemUIOverlayStyle(systemUiOverlayStyle);
            }

            @Override // io.flutter.embedding.engine.systemchannels.PlatformChannel.PlatformMessageHandler
            public void popSystemNavigator() {
                PlatformPlugin.this.popSystemNavigator();
            }

            @Override // io.flutter.embedding.engine.systemchannels.PlatformChannel.PlatformMessageHandler
            public CharSequence getClipboardData(PlatformChannel.ClipboardContentFormat format) {
                return PlatformPlugin.this.getClipboardData(format);
            }

            @Override // io.flutter.embedding.engine.systemchannels.PlatformChannel.PlatformMessageHandler
            public void setClipboardData(String text) {
                PlatformPlugin.this.setClipboardData(text);
            }

            @Override // io.flutter.embedding.engine.systemchannels.PlatformChannel.PlatformMessageHandler
            public boolean clipboardHasStrings() {
                return PlatformPlugin.this.clipboardHasStrings();
            }
        };
        this.mPlatformMessageHandler = platformMessageHandler;
        this.activity = activity;
        this.platformChannel = platformChannel;
        platformChannel.setPlatformMessageHandler(platformMessageHandler);
        this.platformPluginDelegate = delegate;
        this.mEnabledOverlays = DEFAULT_SYSTEM_UI;
    }

    public void destroy() {
        this.platformChannel.setPlatformMessageHandler(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playSystemSound(PlatformChannel.SoundType soundType) {
        if (soundType == PlatformChannel.SoundType.CLICK) {
            View view = this.activity.getWindow().getDecorView();
            view.playSoundEffect(0);
        }
    }

    void vibrateHapticFeedback(PlatformChannel.HapticFeedbackType feedbackType) {
        View view = this.activity.getWindow().getDecorView();
        switch (AnonymousClass3.$SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$HapticFeedbackType[feedbackType.ordinal()]) {
            case 1:
                view.performHapticFeedback(0);
                return;
            case 2:
                view.performHapticFeedback(1);
                return;
            case 3:
                view.performHapticFeedback(3);
                return;
            case 4:
                if (Build.VERSION.SDK_INT >= 23) {
                    view.performHapticFeedback(6);
                    return;
                }
                return;
            case 5:
                if (Build.VERSION.SDK_INT >= 21) {
                    view.performHapticFeedback(4);
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSystemChromePreferredOrientations(int androidOrientation) {
        this.activity.setRequestedOrientation(androidOrientation);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSystemChromeApplicationSwitcherDescription(PlatformChannel.AppSwitcherDescription description) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        if (Build.VERSION.SDK_INT < 28 && Build.VERSION.SDK_INT > 21) {
            this.activity.setTaskDescription(new ActivityManager.TaskDescription(description.label, (Bitmap) null, description.color));
        }
        if (Build.VERSION.SDK_INT >= 28) {
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(description.label, 0, description.color);
            this.activity.setTaskDescription(taskDescription);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSystemChromeChangeListener() {
        View decorView = this.activity.getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new AnonymousClass2(decorView));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: io.flutter.plugin.platform.PlatformPlugin$2  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass2 implements View.OnSystemUiVisibilityChangeListener {
        final /* synthetic */ View val$decorView;

        AnonymousClass2(View view) {
            this.val$decorView = view;
        }

        @Override // android.view.View.OnSystemUiVisibilityChangeListener
        public void onSystemUiVisibilityChange(final int visibility) {
            this.val$decorView.post(new Runnable() { // from class: io.flutter.plugin.platform.PlatformPlugin$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PlatformPlugin.AnonymousClass2.this.m28x970b83d5(visibility);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$onSystemUiVisibilityChange$0$io-flutter-plugin-platform-PlatformPlugin$2  reason: not valid java name */
        public /* synthetic */ void m28x970b83d5(int visibility) {
            if ((visibility & 4) == 0) {
                PlatformPlugin.this.platformChannel.systemChromeChanged(true);
            } else {
                PlatformPlugin.this.platformChannel.systemChromeChanged(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSystemChromeEnabledSystemUIMode(PlatformChannel.SystemUiMode systemUiMode) {
        int enabledOverlays;
        if (systemUiMode == PlatformChannel.SystemUiMode.LEAN_BACK) {
            enabledOverlays = 1798;
        } else if (systemUiMode == PlatformChannel.SystemUiMode.IMMERSIVE && Build.VERSION.SDK_INT >= 19) {
            enabledOverlays = 3846;
        } else if (systemUiMode == PlatformChannel.SystemUiMode.IMMERSIVE_STICKY && Build.VERSION.SDK_INT >= 19) {
            enabledOverlays = 5894;
        } else if (systemUiMode == PlatformChannel.SystemUiMode.EDGE_TO_EDGE && Build.VERSION.SDK_INT >= 29) {
            enabledOverlays = 1792;
        } else {
            return;
        }
        this.mEnabledOverlays = enabledOverlays;
        updateSystemUiOverlays();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSystemChromeEnabledSystemUIOverlays(List<PlatformChannel.SystemUiOverlay> overlaysToShow) {
        int enabledOverlays = 1798;
        if (overlaysToShow.size() == 0 && Build.VERSION.SDK_INT >= 19) {
            enabledOverlays = 1798 | 4096;
        }
        for (int i = 0; i < overlaysToShow.size(); i++) {
            PlatformChannel.SystemUiOverlay overlayToShow = overlaysToShow.get(i);
            switch (AnonymousClass3.$SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$SystemUiOverlay[overlayToShow.ordinal()]) {
                case 1:
                    enabledOverlays &= -5;
                    break;
                case 2:
                    enabledOverlays = enabledOverlays & (-513) & (-3);
                    break;
            }
        }
        this.mEnabledOverlays = enabledOverlays;
        updateSystemUiOverlays();
    }

    public void updateSystemUiOverlays() {
        this.activity.getWindow().getDecorView().setSystemUiVisibility(this.mEnabledOverlays);
        PlatformChannel.SystemChromeStyle systemChromeStyle = this.currentTheme;
        if (systemChromeStyle != null) {
            setSystemChromeSystemUIOverlayStyle(systemChromeStyle);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreSystemChromeSystemUIOverlays() {
        updateSystemUiOverlays();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSystemChromeSystemUIOverlayStyle(PlatformChannel.SystemChromeStyle systemChromeStyle) {
        Window window = this.activity.getWindow();
        View view = window.getDecorView();
        WindowInsetsControllerCompat windowInsetsControllerCompat = new WindowInsetsControllerCompat(window, view);
        if (Build.VERSION.SDK_INT < 30) {
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(201326592);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (systemChromeStyle.statusBarIconBrightness != null) {
                switch (AnonymousClass3.$SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$Brightness[systemChromeStyle.statusBarIconBrightness.ordinal()]) {
                    case 1:
                        windowInsetsControllerCompat.setAppearanceLightStatusBars(true);
                        break;
                    case 2:
                        windowInsetsControllerCompat.setAppearanceLightStatusBars(false);
                        break;
                }
            }
            if (systemChromeStyle.statusBarColor != null) {
                window.setStatusBarColor(systemChromeStyle.statusBarColor.intValue());
            }
        }
        if (systemChromeStyle.systemStatusBarContrastEnforced != null && Build.VERSION.SDK_INT >= 29) {
            window.setStatusBarContrastEnforced(systemChromeStyle.systemStatusBarContrastEnforced.booleanValue());
        }
        if (Build.VERSION.SDK_INT >= 26) {
            if (systemChromeStyle.systemNavigationBarIconBrightness != null) {
                switch (AnonymousClass3.$SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$Brightness[systemChromeStyle.systemNavigationBarIconBrightness.ordinal()]) {
                    case 1:
                        windowInsetsControllerCompat.setAppearanceLightNavigationBars(true);
                        break;
                    case 2:
                        windowInsetsControllerCompat.setAppearanceLightNavigationBars(false);
                        break;
                }
            }
            if (systemChromeStyle.systemNavigationBarColor != null) {
                window.setNavigationBarColor(systemChromeStyle.systemNavigationBarColor.intValue());
            }
        }
        if (systemChromeStyle.systemNavigationBarDividerColor != null && Build.VERSION.SDK_INT >= 28) {
            window.setNavigationBarDividerColor(systemChromeStyle.systemNavigationBarDividerColor.intValue());
        }
        if (systemChromeStyle.systemNavigationBarContrastEnforced != null && Build.VERSION.SDK_INT >= 29) {
            window.setNavigationBarContrastEnforced(systemChromeStyle.systemNavigationBarContrastEnforced.booleanValue());
        }
        this.currentTheme = systemChromeStyle;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: io.flutter.plugin.platform.PlatformPlugin$3  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$Brightness;
        static final /* synthetic */ int[] $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$HapticFeedbackType;
        static final /* synthetic */ int[] $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$SystemUiOverlay;

        static {
            int[] iArr = new int[PlatformChannel.Brightness.values().length];
            $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$Brightness = iArr;
            try {
                iArr[PlatformChannel.Brightness.DARK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$Brightness[PlatformChannel.Brightness.LIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            int[] iArr2 = new int[PlatformChannel.SystemUiOverlay.values().length];
            $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$SystemUiOverlay = iArr2;
            try {
                iArr2[PlatformChannel.SystemUiOverlay.TOP_OVERLAYS.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$SystemUiOverlay[PlatformChannel.SystemUiOverlay.BOTTOM_OVERLAYS.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            int[] iArr3 = new int[PlatformChannel.HapticFeedbackType.values().length];
            $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$HapticFeedbackType = iArr3;
            try {
                iArr3[PlatformChannel.HapticFeedbackType.STANDARD.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$HapticFeedbackType[PlatformChannel.HapticFeedbackType.LIGHT_IMPACT.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$HapticFeedbackType[PlatformChannel.HapticFeedbackType.MEDIUM_IMPACT.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$HapticFeedbackType[PlatformChannel.HapticFeedbackType.HEAVY_IMPACT.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$HapticFeedbackType[PlatformChannel.HapticFeedbackType.SELECTION_CLICK.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void popSystemNavigator() {
        PlatformPluginDelegate platformPluginDelegate = this.platformPluginDelegate;
        if (platformPluginDelegate != null && platformPluginDelegate.popSystemNavigator()) {
            return;
        }
        Activity activity = this.activity;
        if (activity instanceof OnBackPressedDispatcherOwner) {
            ((OnBackPressedDispatcherOwner) activity).getOnBackPressedDispatcher().onBackPressed();
        } else {
            activity.finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CharSequence getClipboardData(PlatformChannel.ClipboardContentFormat format) {
        ClipboardManager clipboard = (ClipboardManager) this.activity.getSystemService("clipboard");
        if (clipboard.hasPrimaryClip()) {
            try {
                ClipData clip = clipboard.getPrimaryClip();
                if (clip == null) {
                    return null;
                }
                if (format != null && format != PlatformChannel.ClipboardContentFormat.PLAIN_TEXT) {
                    return null;
                }
                ClipData.Item item = clip.getItemAt(0);
                if (item.getUri() != null) {
                    this.activity.getContentResolver().openTypedAssetFileDescriptor(item.getUri(), "text/*", null);
                }
                return item.coerceToText(this.activity);
            } catch (FileNotFoundException e) {
                return null;
            } catch (SecurityException e2) {
                Log.w(TAG, "Attempted to get clipboard data that requires additional permission(s).\nSee the exception details for which permission(s) are required, and consider adding them to your Android Manifest as described in:\nhttps://developer.android.com/guide/topics/permissions/overview", e2);
                return null;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setClipboardData(String text) {
        ClipboardManager clipboard = (ClipboardManager) this.activity.getSystemService("clipboard");
        ClipData clip = ClipData.newPlainText("text label?", text);
        clipboard.setPrimaryClip(clip);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean clipboardHasStrings() {
        ClipDescription description;
        ClipboardManager clipboard = (ClipboardManager) this.activity.getSystemService("clipboard");
        if (clipboard.hasPrimaryClip() && (description = clipboard.getPrimaryClipDescription()) != null) {
            return description.hasMimeType("text/*");
        }
        return false;
    }
}
