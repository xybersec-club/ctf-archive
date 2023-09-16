package io.flutter.embedding.engine.systemchannels;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import io.flutter.Log;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.JSONMethodCodec;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class PlatformChannel {
    private static final String TAG = "PlatformChannel";
    public final MethodChannel channel;
    final MethodChannel.MethodCallHandler parsingMethodCallHandler;
    private PlatformMessageHandler platformMessageHandler;

    /* loaded from: classes.dex */
    public interface PlatformMessageHandler {
        boolean clipboardHasStrings();

        CharSequence getClipboardData(ClipboardContentFormat clipboardContentFormat);

        void playSystemSound(SoundType soundType);

        void popSystemNavigator();

        void restoreSystemUiOverlays();

        void setApplicationSwitcherDescription(AppSwitcherDescription appSwitcherDescription);

        void setClipboardData(String str);

        void setPreferredOrientations(int i);

        void setSystemUiChangeListener();

        void setSystemUiOverlayStyle(SystemChromeStyle systemChromeStyle);

        void showSystemOverlays(List<SystemUiOverlay> list);

        void showSystemUiMode(SystemUiMode systemUiMode);

        void vibrateHapticFeedback(HapticFeedbackType hapticFeedbackType);
    }

    public PlatformChannel(DartExecutor dartExecutor) {
        MethodChannel.MethodCallHandler methodCallHandler = new MethodChannel.MethodCallHandler() { // from class: io.flutter.embedding.engine.systemchannels.PlatformChannel.1
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // io.flutter.plugin.common.MethodChannel.MethodCallHandler
            public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                if (PlatformChannel.this.platformMessageHandler == null) {
                    return;
                }
                String method = call.method;
                Object arguments = call.arguments;
                Log.v(PlatformChannel.TAG, "Received '" + method + "' message.");
                char c = 65535;
                try {
                    switch (method.hashCode()) {
                        case -766342101:
                            if (method.equals("SystemNavigator.pop")) {
                                c = '\t';
                                break;
                            }
                            break;
                        case -720677196:
                            if (method.equals("Clipboard.setData")) {
                                c = 11;
                                break;
                            }
                            break;
                        case -577225884:
                            if (method.equals("SystemChrome.setSystemUIChangeListener")) {
                                c = 6;
                                break;
                            }
                            break;
                        case -548468504:
                            if (method.equals("SystemChrome.setApplicationSwitcherDescription")) {
                                c = 3;
                                break;
                            }
                            break;
                        case -247230243:
                            if (method.equals("HapticFeedback.vibrate")) {
                                c = 1;
                                break;
                            }
                            break;
                        case -215273374:
                            if (method.equals("SystemSound.play")) {
                                c = 0;
                                break;
                            }
                            break;
                        case 241845679:
                            if (method.equals("SystemChrome.restoreSystemUIOverlays")) {
                                c = 7;
                                break;
                            }
                            break;
                        case 875995648:
                            if (method.equals("Clipboard.hasStrings")) {
                                c = '\f';
                                break;
                            }
                            break;
                        case 1128339786:
                            if (method.equals("SystemChrome.setEnabledSystemUIMode")) {
                                c = 5;
                                break;
                            }
                            break;
                        case 1390477857:
                            if (method.equals("SystemChrome.setSystemUIOverlayStyle")) {
                                c = '\b';
                                break;
                            }
                            break;
                        case 1514180520:
                            if (method.equals("Clipboard.getData")) {
                                c = '\n';
                                break;
                            }
                            break;
                        case 1674312266:
                            if (method.equals("SystemChrome.setEnabledSystemUIOverlays")) {
                                c = 4;
                                break;
                            }
                            break;
                        case 2119655719:
                            if (method.equals("SystemChrome.setPreferredOrientations")) {
                                c = 2;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            try {
                                SoundType soundType = SoundType.fromValue((String) arguments);
                                PlatformChannel.this.platformMessageHandler.playSystemSound(soundType);
                                result.success(null);
                                return;
                            } catch (NoSuchFieldException exception) {
                                result.error("error", exception.getMessage(), null);
                                return;
                            }
                        case 1:
                            try {
                                HapticFeedbackType feedbackType = HapticFeedbackType.fromValue((String) arguments);
                                PlatformChannel.this.platformMessageHandler.vibrateHapticFeedback(feedbackType);
                                result.success(null);
                                return;
                            } catch (NoSuchFieldException exception2) {
                                result.error("error", exception2.getMessage(), null);
                                return;
                            }
                        case 2:
                            try {
                                int androidOrientation = PlatformChannel.this.decodeOrientations((JSONArray) arguments);
                                PlatformChannel.this.platformMessageHandler.setPreferredOrientations(androidOrientation);
                                result.success(null);
                                return;
                            } catch (NoSuchFieldException | JSONException exception3) {
                                result.error("error", exception3.getMessage(), null);
                                return;
                            }
                        case 3:
                            try {
                                AppSwitcherDescription description = PlatformChannel.this.decodeAppSwitcherDescription((JSONObject) arguments);
                                PlatformChannel.this.platformMessageHandler.setApplicationSwitcherDescription(description);
                                result.success(null);
                                return;
                            } catch (JSONException exception4) {
                                result.error("error", exception4.getMessage(), null);
                                return;
                            }
                        case 4:
                            try {
                                List<SystemUiOverlay> overlays = PlatformChannel.this.decodeSystemUiOverlays((JSONArray) arguments);
                                PlatformChannel.this.platformMessageHandler.showSystemOverlays(overlays);
                                result.success(null);
                                return;
                            } catch (NoSuchFieldException | JSONException exception5) {
                                result.error("error", exception5.getMessage(), null);
                                return;
                            }
                        case 5:
                            try {
                                SystemUiMode mode = PlatformChannel.this.decodeSystemUiMode((String) arguments);
                                PlatformChannel.this.platformMessageHandler.showSystemUiMode(mode);
                                result.success(null);
                                return;
                            } catch (NoSuchFieldException | JSONException exception6) {
                                result.error("error", exception6.getMessage(), null);
                                return;
                            }
                        case 6:
                            PlatformChannel.this.platformMessageHandler.setSystemUiChangeListener();
                            result.success(null);
                            return;
                        case 7:
                            PlatformChannel.this.platformMessageHandler.restoreSystemUiOverlays();
                            result.success(null);
                            return;
                        case '\b':
                            try {
                                SystemChromeStyle systemChromeStyle = PlatformChannel.this.decodeSystemChromeStyle((JSONObject) arguments);
                                PlatformChannel.this.platformMessageHandler.setSystemUiOverlayStyle(systemChromeStyle);
                                result.success(null);
                                return;
                            } catch (NoSuchFieldException | JSONException exception7) {
                                result.error("error", exception7.getMessage(), null);
                                return;
                            }
                        case '\t':
                            PlatformChannel.this.platformMessageHandler.popSystemNavigator();
                            result.success(null);
                            return;
                        case '\n':
                            String contentFormatName = (String) arguments;
                            ClipboardContentFormat clipboardFormat = null;
                            if (contentFormatName != null) {
                                try {
                                    clipboardFormat = ClipboardContentFormat.fromValue(contentFormatName);
                                } catch (NoSuchFieldException e) {
                                    result.error("error", "No such clipboard content format: " + contentFormatName, null);
                                }
                            }
                            CharSequence clipboardContent = PlatformChannel.this.platformMessageHandler.getClipboardData(clipboardFormat);
                            if (clipboardContent != null) {
                                JSONObject response = new JSONObject();
                                response.put("text", clipboardContent);
                                result.success(response);
                                return;
                            }
                            result.success(null);
                            return;
                        case 11:
                            PlatformChannel.this.platformMessageHandler.setClipboardData(((JSONObject) arguments).getString("text"));
                            result.success(null);
                            return;
                        case MotionEventCompat.AXIS_RX /* 12 */:
                            boolean hasStrings = PlatformChannel.this.platformMessageHandler.clipboardHasStrings();
                            JSONObject response2 = new JSONObject();
                            response2.put("value", hasStrings);
                            result.success(response2);
                            return;
                        default:
                            result.notImplemented();
                            return;
                    }
                } catch (JSONException e2) {
                    result.error("error", "JSON error: " + e2.getMessage(), null);
                }
                result.error("error", "JSON error: " + e2.getMessage(), null);
            }
        };
        this.parsingMethodCallHandler = methodCallHandler;
        MethodChannel methodChannel = new MethodChannel(dartExecutor, "flutter/platform", JSONMethodCodec.INSTANCE);
        this.channel = methodChannel;
        methodChannel.setMethodCallHandler(methodCallHandler);
    }

    public void setPlatformMessageHandler(PlatformMessageHandler platformMessageHandler) {
        this.platformMessageHandler = platformMessageHandler;
    }

    public void systemChromeChanged(boolean overlaysAreVisible) {
        Log.v(TAG, "Sending 'systemUIChange' message.");
        this.channel.invokeMethod("SystemChrome.systemUIChange", Arrays.asList(Boolean.valueOf(overlaysAreVisible)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public int decodeOrientations(JSONArray encodedOrientations) throws JSONException, NoSuchFieldException {
        int requestedOrientation = 0;
        int firstRequestedOrientation = 0;
        for (int index = 0; index < encodedOrientations.length(); index++) {
            String encodedOrientation = encodedOrientations.getString(index);
            DeviceOrientation orientation = DeviceOrientation.fromValue(encodedOrientation);
            switch (AnonymousClass2.$SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$DeviceOrientation[orientation.ordinal()]) {
                case 1:
                    requestedOrientation |= 1;
                    break;
                case 2:
                    requestedOrientation |= 4;
                    break;
                case 3:
                    requestedOrientation |= 2;
                    break;
                case 4:
                    requestedOrientation |= 8;
                    break;
            }
            if (firstRequestedOrientation == 0) {
                firstRequestedOrientation = requestedOrientation;
            }
        }
        switch (requestedOrientation) {
            case 0:
                return -1;
            case 1:
                return 1;
            case 2:
                return 0;
            case 3:
            case 6:
            case 7:
            case 9:
            case MotionEventCompat.AXIS_RX /* 12 */:
            case 13:
            case MotionEventCompat.AXIS_RZ /* 14 */:
                switch (firstRequestedOrientation) {
                    case 1:
                        return 1;
                    case 2:
                        return 0;
                    case 4:
                        return 9;
                    case 8:
                        return 8;
                }
            case 4:
                return 9;
            case 5:
                return 12;
            case 8:
                return 8;
            case 10:
                return 11;
            case 11:
                return 2;
            case 15:
                return 13;
        }
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AppSwitcherDescription decodeAppSwitcherDescription(JSONObject encodedDescription) throws JSONException {
        int color = encodedDescription.getInt("primaryColor");
        if (color != 0) {
            color |= ViewCompat.MEASURED_STATE_MASK;
        }
        String label = encodedDescription.getString("label");
        return new AppSwitcherDescription(color, label);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<SystemUiOverlay> decodeSystemUiOverlays(JSONArray encodedSystemUiOverlay) throws JSONException, NoSuchFieldException {
        List<SystemUiOverlay> overlays = new ArrayList<>();
        for (int i = 0; i < encodedSystemUiOverlay.length(); i++) {
            String encodedOverlay = encodedSystemUiOverlay.getString(i);
            SystemUiOverlay overlay = SystemUiOverlay.fromValue(encodedOverlay);
            switch (AnonymousClass2.$SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$SystemUiOverlay[overlay.ordinal()]) {
                case 1:
                    overlays.add(SystemUiOverlay.TOP_OVERLAYS);
                    break;
                case 2:
                    overlays.add(SystemUiOverlay.BOTTOM_OVERLAYS);
                    break;
            }
        }
        return overlays;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: io.flutter.embedding.engine.systemchannels.PlatformChannel$2  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$DeviceOrientation;
        static final /* synthetic */ int[] $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$SystemUiMode;
        static final /* synthetic */ int[] $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$SystemUiOverlay;

        static {
            int[] iArr = new int[SystemUiMode.values().length];
            $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$SystemUiMode = iArr;
            try {
                iArr[SystemUiMode.LEAN_BACK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$SystemUiMode[SystemUiMode.IMMERSIVE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$SystemUiMode[SystemUiMode.IMMERSIVE_STICKY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$SystemUiMode[SystemUiMode.EDGE_TO_EDGE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            int[] iArr2 = new int[SystemUiOverlay.values().length];
            $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$SystemUiOverlay = iArr2;
            try {
                iArr2[SystemUiOverlay.TOP_OVERLAYS.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$SystemUiOverlay[SystemUiOverlay.BOTTOM_OVERLAYS.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            int[] iArr3 = new int[DeviceOrientation.values().length];
            $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$DeviceOrientation = iArr3;
            try {
                iArr3[DeviceOrientation.PORTRAIT_UP.ordinal()] = 1;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$DeviceOrientation[DeviceOrientation.PORTRAIT_DOWN.ordinal()] = 2;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$DeviceOrientation[DeviceOrientation.LANDSCAPE_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$DeviceOrientation[DeviceOrientation.LANDSCAPE_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SystemUiMode decodeSystemUiMode(String encodedSystemUiMode) throws JSONException, NoSuchFieldException {
        SystemUiMode mode = SystemUiMode.fromValue(encodedSystemUiMode);
        switch (AnonymousClass2.$SwitchMap$io$flutter$embedding$engine$systemchannels$PlatformChannel$SystemUiMode[mode.ordinal()]) {
            case 1:
                return SystemUiMode.LEAN_BACK;
            case 2:
                return SystemUiMode.IMMERSIVE;
            case 3:
                return SystemUiMode.IMMERSIVE_STICKY;
            case 4:
                return SystemUiMode.EDGE_TO_EDGE;
            default:
                return SystemUiMode.EDGE_TO_EDGE;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SystemChromeStyle decodeSystemChromeStyle(JSONObject encodedStyle) throws JSONException, NoSuchFieldException {
        Brightness statusBarIconBrightness = null;
        Boolean systemStatusBarContrastEnforced = null;
        Integer systemNavigationBarColor = null;
        Brightness systemNavigationBarIconBrightness = null;
        Integer systemNavigationBarDividerColor = null;
        Boolean systemNavigationBarContrastEnforced = null;
        Integer statusBarColor = encodedStyle.isNull("statusBarColor") ? null : Integer.valueOf(encodedStyle.getInt("statusBarColor"));
        if (!encodedStyle.isNull("statusBarIconBrightness")) {
            statusBarIconBrightness = Brightness.fromValue(encodedStyle.getString("statusBarIconBrightness"));
        }
        if (!encodedStyle.isNull("systemStatusBarContrastEnforced")) {
            systemStatusBarContrastEnforced = Boolean.valueOf(encodedStyle.getBoolean("systemStatusBarContrastEnforced"));
        }
        if (!encodedStyle.isNull("systemNavigationBarColor")) {
            systemNavigationBarColor = Integer.valueOf(encodedStyle.getInt("systemNavigationBarColor"));
        }
        if (!encodedStyle.isNull("systemNavigationBarIconBrightness")) {
            systemNavigationBarIconBrightness = Brightness.fromValue(encodedStyle.getString("systemNavigationBarIconBrightness"));
        }
        if (!encodedStyle.isNull("systemNavigationBarDividerColor")) {
            systemNavigationBarDividerColor = Integer.valueOf(encodedStyle.getInt("systemNavigationBarDividerColor"));
        }
        if (!encodedStyle.isNull("systemNavigationBarContrastEnforced")) {
            systemNavigationBarContrastEnforced = Boolean.valueOf(encodedStyle.getBoolean("systemNavigationBarContrastEnforced"));
        }
        return new SystemChromeStyle(statusBarColor, statusBarIconBrightness, systemStatusBarContrastEnforced, systemNavigationBarColor, systemNavigationBarIconBrightness, systemNavigationBarDividerColor, systemNavigationBarContrastEnforced);
    }

    /* loaded from: classes.dex */
    public enum SoundType {
        CLICK("SystemSoundType.click"),
        ALERT("SystemSoundType.alert");
        
        private final String encodedName;

        static SoundType fromValue(String encodedName) throws NoSuchFieldException {
            SoundType[] values;
            for (SoundType soundType : values()) {
                if (soundType.encodedName.equals(encodedName)) {
                    return soundType;
                }
            }
            throw new NoSuchFieldException("No such SoundType: " + encodedName);
        }

        SoundType(String encodedName) {
            this.encodedName = encodedName;
        }
    }

    /* loaded from: classes.dex */
    public enum HapticFeedbackType {
        STANDARD(null),
        LIGHT_IMPACT("HapticFeedbackType.lightImpact"),
        MEDIUM_IMPACT("HapticFeedbackType.mediumImpact"),
        HEAVY_IMPACT("HapticFeedbackType.heavyImpact"),
        SELECTION_CLICK("HapticFeedbackType.selectionClick");
        
        private final String encodedName;

        static HapticFeedbackType fromValue(String encodedName) throws NoSuchFieldException {
            HapticFeedbackType[] values;
            for (HapticFeedbackType feedbackType : values()) {
                String str = feedbackType.encodedName;
                if ((str == null && encodedName == null) || (str != null && str.equals(encodedName))) {
                    return feedbackType;
                }
            }
            throw new NoSuchFieldException("No such HapticFeedbackType: " + encodedName);
        }

        HapticFeedbackType(String encodedName) {
            this.encodedName = encodedName;
        }
    }

    /* loaded from: classes.dex */
    public enum DeviceOrientation {
        PORTRAIT_UP("DeviceOrientation.portraitUp"),
        PORTRAIT_DOWN("DeviceOrientation.portraitDown"),
        LANDSCAPE_LEFT("DeviceOrientation.landscapeLeft"),
        LANDSCAPE_RIGHT("DeviceOrientation.landscapeRight");
        
        private String encodedName;

        static DeviceOrientation fromValue(String encodedName) throws NoSuchFieldException {
            DeviceOrientation[] values;
            for (DeviceOrientation orientation : values()) {
                if (orientation.encodedName.equals(encodedName)) {
                    return orientation;
                }
            }
            throw new NoSuchFieldException("No such DeviceOrientation: " + encodedName);
        }

        DeviceOrientation(String encodedName) {
            this.encodedName = encodedName;
        }
    }

    /* loaded from: classes.dex */
    public enum SystemUiOverlay {
        TOP_OVERLAYS("SystemUiOverlay.top"),
        BOTTOM_OVERLAYS("SystemUiOverlay.bottom");
        
        private String encodedName;

        static SystemUiOverlay fromValue(String encodedName) throws NoSuchFieldException {
            SystemUiOverlay[] values;
            for (SystemUiOverlay overlay : values()) {
                if (overlay.encodedName.equals(encodedName)) {
                    return overlay;
                }
            }
            throw new NoSuchFieldException("No such SystemUiOverlay: " + encodedName);
        }

        SystemUiOverlay(String encodedName) {
            this.encodedName = encodedName;
        }
    }

    /* loaded from: classes.dex */
    public enum SystemUiMode {
        LEAN_BACK("SystemUiMode.leanBack"),
        IMMERSIVE("SystemUiMode.immersive"),
        IMMERSIVE_STICKY("SystemUiMode.immersiveSticky"),
        EDGE_TO_EDGE("SystemUiMode.edgeToEdge");
        
        private String encodedName;

        static SystemUiMode fromValue(String encodedName) throws NoSuchFieldException {
            SystemUiMode[] values;
            for (SystemUiMode mode : values()) {
                if (mode.encodedName.equals(encodedName)) {
                    return mode;
                }
            }
            throw new NoSuchFieldException("No such SystemUiMode: " + encodedName);
        }

        SystemUiMode(String encodedName) {
            this.encodedName = encodedName;
        }
    }

    /* loaded from: classes.dex */
    public static class AppSwitcherDescription {
        public final int color;
        public final String label;

        public AppSwitcherDescription(int color, String label) {
            this.color = color;
            this.label = label;
        }
    }

    /* loaded from: classes.dex */
    public static class SystemChromeStyle {
        public final Integer statusBarColor;
        public final Brightness statusBarIconBrightness;
        public final Integer systemNavigationBarColor;
        public final Boolean systemNavigationBarContrastEnforced;
        public final Integer systemNavigationBarDividerColor;
        public final Brightness systemNavigationBarIconBrightness;
        public final Boolean systemStatusBarContrastEnforced;

        public SystemChromeStyle(Integer statusBarColor, Brightness statusBarIconBrightness, Boolean systemStatusBarContrastEnforced, Integer systemNavigationBarColor, Brightness systemNavigationBarIconBrightness, Integer systemNavigationBarDividerColor, Boolean systemNavigationBarContrastEnforced) {
            this.statusBarColor = statusBarColor;
            this.statusBarIconBrightness = statusBarIconBrightness;
            this.systemStatusBarContrastEnforced = systemStatusBarContrastEnforced;
            this.systemNavigationBarColor = systemNavigationBarColor;
            this.systemNavigationBarIconBrightness = systemNavigationBarIconBrightness;
            this.systemNavigationBarDividerColor = systemNavigationBarDividerColor;
            this.systemNavigationBarContrastEnforced = systemNavigationBarContrastEnforced;
        }
    }

    /* loaded from: classes.dex */
    public enum Brightness {
        LIGHT("Brightness.light"),
        DARK("Brightness.dark");
        
        private String encodedName;

        static Brightness fromValue(String encodedName) throws NoSuchFieldException {
            Brightness[] values;
            for (Brightness brightness : values()) {
                if (brightness.encodedName.equals(encodedName)) {
                    return brightness;
                }
            }
            throw new NoSuchFieldException("No such Brightness: " + encodedName);
        }

        Brightness(String encodedName) {
            this.encodedName = encodedName;
        }
    }

    /* loaded from: classes.dex */
    public enum ClipboardContentFormat {
        PLAIN_TEXT("text/plain");
        
        private String encodedName;

        static ClipboardContentFormat fromValue(String encodedName) throws NoSuchFieldException {
            ClipboardContentFormat[] values;
            for (ClipboardContentFormat format : values()) {
                if (format.encodedName.equals(encodedName)) {
                    return format;
                }
            }
            throw new NoSuchFieldException("No such ClipboardContentFormat: " + encodedName);
        }

        ClipboardContentFormat(String encodedName) {
            this.encodedName = encodedName;
        }
    }
}
