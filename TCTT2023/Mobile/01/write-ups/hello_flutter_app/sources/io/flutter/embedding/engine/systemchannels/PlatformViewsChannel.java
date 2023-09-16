package io.flutter.embedding.engine.systemchannels;

import io.flutter.Log;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.systemchannels.PlatformViewsChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMethodCodec;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class PlatformViewsChannel {
    private static final String TAG = "PlatformViewsChannel";
    private final MethodChannel channel;
    private PlatformViewsHandler handler;
    private final MethodChannel.MethodCallHandler parsingHandler;

    /* loaded from: classes.dex */
    public interface PlatformViewBufferResized {
        void run(PlatformViewBufferSize platformViewBufferSize);
    }

    /* loaded from: classes.dex */
    public interface PlatformViewsHandler {
        public static final long NON_TEXTURE_FALLBACK = -2;

        void clearFocus(int i);

        void createForPlatformViewLayer(PlatformViewCreationRequest platformViewCreationRequest);

        long createForTextureLayer(PlatformViewCreationRequest platformViewCreationRequest);

        void dispose(int i);

        void offset(int i, double d, double d2);

        void onTouch(PlatformViewTouch platformViewTouch);

        void resize(PlatformViewResizeRequest platformViewResizeRequest, PlatformViewBufferResized platformViewBufferResized);

        void setDirection(int i, int i2);

        void synchronizeToNativeViewHierarchy(boolean z);
    }

    public void invokeViewFocused(int viewId) {
        MethodChannel methodChannel = this.channel;
        if (methodChannel == null) {
            return;
        }
        methodChannel.invokeMethod("viewFocused", Integer.valueOf(viewId));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String detailedExceptionString(Exception exception) {
        return Log.getStackTraceString(exception);
    }

    /* renamed from: io.flutter.embedding.engine.systemchannels.PlatformViewsChannel$1  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass1 implements MethodChannel.MethodCallHandler {
        AnonymousClass1() {
        }

        @Override // io.flutter.plugin.common.MethodChannel.MethodCallHandler
        public void onMethodCall(MethodCall call, MethodChannel.Result result) {
            if (PlatformViewsChannel.this.handler == null) {
                return;
            }
            Log.v(PlatformViewsChannel.TAG, "Received '" + call.method + "' message.");
            String str = call.method;
            char c = 65535;
            switch (str.hashCode()) {
                case -1352294148:
                    if (str.equals("create")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1019779949:
                    if (str.equals("offset")) {
                        c = 3;
                        break;
                    }
                    break;
                case -934437708:
                    if (str.equals("resize")) {
                        c = 2;
                        break;
                    }
                    break;
                case -756050293:
                    if (str.equals("clearFocus")) {
                        c = 6;
                        break;
                    }
                    break;
                case -308988850:
                    if (str.equals("synchronizeToNativeViewHierarchy")) {
                        c = 7;
                        break;
                    }
                    break;
                case 110550847:
                    if (str.equals("touch")) {
                        c = 4;
                        break;
                    }
                    break;
                case 576796989:
                    if (str.equals("setDirection")) {
                        c = 5;
                        break;
                    }
                    break;
                case 1671767583:
                    if (str.equals("dispose")) {
                        c = 1;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    create(call, result);
                    return;
                case 1:
                    dispose(call, result);
                    return;
                case 2:
                    resize(call, result);
                    return;
                case 3:
                    offset(call, result);
                    return;
                case 4:
                    touch(call, result);
                    return;
                case 5:
                    setDirection(call, result);
                    return;
                case 6:
                    clearFocus(call, result);
                    return;
                case 7:
                    synchronizeToNativeViewHierarchy(call, result);
                    return;
                default:
                    result.notImplemented();
                    return;
            }
        }

        private void create(MethodCall call, MethodChannel.Result result) {
            ByteBuffer additionalParams;
            PlatformViewCreationRequest.RequestedDisplayMode displayMode;
            Map<String, Object> createArgs = (Map) call.arguments();
            boolean z = true;
            boolean usesPlatformViewLayer = createArgs.containsKey("hybrid") && ((Boolean) createArgs.get("hybrid")).booleanValue();
            if (createArgs.containsKey("params")) {
                additionalParams = ByteBuffer.wrap((byte[]) createArgs.get("params"));
            } else {
                additionalParams = null;
            }
            try {
                if (usesPlatformViewLayer) {
                    PlatformViewCreationRequest request = new PlatformViewCreationRequest(((Integer) createArgs.get("id")).intValue(), (String) createArgs.get("viewType"), 0.0d, 0.0d, 0.0d, 0.0d, ((Integer) createArgs.get("direction")).intValue(), PlatformViewCreationRequest.RequestedDisplayMode.HYBRID_ONLY, additionalParams);
                    PlatformViewsChannel.this.handler.createForPlatformViewLayer(request);
                    result.success(null);
                    return;
                }
                if (!createArgs.containsKey("hybridFallback") || !((Boolean) createArgs.get("hybridFallback")).booleanValue()) {
                    z = false;
                }
                boolean hybridFallback = z;
                if (hybridFallback) {
                    displayMode = PlatformViewCreationRequest.RequestedDisplayMode.TEXTURE_WITH_HYBRID_FALLBACK;
                } else {
                    displayMode = PlatformViewCreationRequest.RequestedDisplayMode.TEXTURE_WITH_VIRTUAL_FALLBACK;
                }
                PlatformViewCreationRequest request2 = new PlatformViewCreationRequest(((Integer) createArgs.get("id")).intValue(), (String) createArgs.get("viewType"), createArgs.containsKey("top") ? ((Double) createArgs.get("top")).doubleValue() : 0.0d, createArgs.containsKey("left") ? ((Double) createArgs.get("left")).doubleValue() : 0.0d, ((Double) createArgs.get("width")).doubleValue(), ((Double) createArgs.get("height")).doubleValue(), ((Integer) createArgs.get("direction")).intValue(), displayMode, additionalParams);
                long textureId = PlatformViewsChannel.this.handler.createForTextureLayer(request2);
                if (textureId == -2) {
                    if (!hybridFallback) {
                        throw new AssertionError("Platform view attempted to fall back to hybrid mode when not requested.");
                    }
                    result.success(null);
                    return;
                }
                result.success(Long.valueOf(textureId));
            } catch (IllegalStateException exception) {
                result.error("error", PlatformViewsChannel.detailedExceptionString(exception), null);
            }
        }

        private void dispose(MethodCall call, MethodChannel.Result result) {
            Map<String, Object> disposeArgs = (Map) call.arguments();
            int viewId = ((Integer) disposeArgs.get("id")).intValue();
            try {
                PlatformViewsChannel.this.handler.dispose(viewId);
                result.success(null);
            } catch (IllegalStateException exception) {
                result.error("error", PlatformViewsChannel.detailedExceptionString(exception), null);
            }
        }

        private void resize(MethodCall call, final MethodChannel.Result result) {
            Map<String, Object> resizeArgs = (Map) call.arguments();
            PlatformViewResizeRequest resizeRequest = new PlatformViewResizeRequest(((Integer) resizeArgs.get("id")).intValue(), ((Double) resizeArgs.get("width")).doubleValue(), ((Double) resizeArgs.get("height")).doubleValue());
            try {
                PlatformViewsChannel.this.handler.resize(resizeRequest, new PlatformViewBufferResized() { // from class: io.flutter.embedding.engine.systemchannels.PlatformViewsChannel$1$$ExternalSyntheticLambda0
                    @Override // io.flutter.embedding.engine.systemchannels.PlatformViewsChannel.PlatformViewBufferResized
                    public final void run(PlatformViewsChannel.PlatformViewBufferSize platformViewBufferSize) {
                        PlatformViewsChannel.AnonymousClass1.lambda$resize$0(MethodChannel.Result.this, platformViewBufferSize);
                    }
                });
            } catch (IllegalStateException exception) {
                result.error("error", PlatformViewsChannel.detailedExceptionString(exception), null);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$resize$0(MethodChannel.Result result, PlatformViewBufferSize bufferSize) {
            if (bufferSize == null) {
                result.error("error", "Failed to resize the platform view", null);
                return;
            }
            Map<String, Object> response = new HashMap<>();
            response.put("width", Double.valueOf(bufferSize.width));
            response.put("height", Double.valueOf(bufferSize.height));
            result.success(response);
        }

        private void offset(MethodCall call, MethodChannel.Result result) {
            Map<String, Object> offsetArgs = (Map) call.arguments();
            try {
                PlatformViewsChannel.this.handler.offset(((Integer) offsetArgs.get("id")).intValue(), ((Double) offsetArgs.get("top")).doubleValue(), ((Double) offsetArgs.get("left")).doubleValue());
                result.success(null);
            } catch (IllegalStateException exception) {
                result.error("error", PlatformViewsChannel.detailedExceptionString(exception), null);
            }
        }

        private void touch(MethodCall call, MethodChannel.Result result) {
            MethodChannel.Result result2;
            List<Object> args = (List) call.arguments();
            PlatformViewTouch touch = new PlatformViewTouch(((Integer) args.get(0)).intValue(), (Number) args.get(1), (Number) args.get(2), ((Integer) args.get(3)).intValue(), ((Integer) args.get(4)).intValue(), args.get(5), args.get(6), ((Integer) args.get(7)).intValue(), ((Integer) args.get(8)).intValue(), (float) ((Double) args.get(9)).doubleValue(), (float) ((Double) args.get(10)).doubleValue(), ((Integer) args.get(11)).intValue(), ((Integer) args.get(12)).intValue(), ((Integer) args.get(13)).intValue(), ((Integer) args.get(14)).intValue(), ((Number) args.get(15)).longValue());
            try {
                PlatformViewsChannel.this.handler.onTouch(touch);
                result2 = result;
                try {
                    result2.success(null);
                } catch (IllegalStateException e) {
                    exception = e;
                    result2.error("error", PlatformViewsChannel.detailedExceptionString(exception), null);
                }
            } catch (IllegalStateException e2) {
                exception = e2;
                result2 = result;
            }
        }

        private void setDirection(MethodCall call, MethodChannel.Result result) {
            Map<String, Object> setDirectionArgs = (Map) call.arguments();
            int newDirectionViewId = ((Integer) setDirectionArgs.get("id")).intValue();
            int direction = ((Integer) setDirectionArgs.get("direction")).intValue();
            try {
                PlatformViewsChannel.this.handler.setDirection(newDirectionViewId, direction);
                result.success(null);
            } catch (IllegalStateException exception) {
                result.error("error", PlatformViewsChannel.detailedExceptionString(exception), null);
            }
        }

        private void clearFocus(MethodCall call, MethodChannel.Result result) {
            int viewId = ((Integer) call.arguments()).intValue();
            try {
                PlatformViewsChannel.this.handler.clearFocus(viewId);
                result.success(null);
            } catch (IllegalStateException exception) {
                result.error("error", PlatformViewsChannel.detailedExceptionString(exception), null);
            }
        }

        private void synchronizeToNativeViewHierarchy(MethodCall call, MethodChannel.Result result) {
            boolean yes = ((Boolean) call.arguments()).booleanValue();
            try {
                PlatformViewsChannel.this.handler.synchronizeToNativeViewHierarchy(yes);
                result.success(null);
            } catch (IllegalStateException exception) {
                result.error("error", PlatformViewsChannel.detailedExceptionString(exception), null);
            }
        }
    }

    public PlatformViewsChannel(DartExecutor dartExecutor) {
        AnonymousClass1 anonymousClass1 = new AnonymousClass1();
        this.parsingHandler = anonymousClass1;
        MethodChannel methodChannel = new MethodChannel(dartExecutor, "flutter/platform_views", StandardMethodCodec.INSTANCE);
        this.channel = methodChannel;
        methodChannel.setMethodCallHandler(anonymousClass1);
    }

    public void setPlatformViewsHandler(PlatformViewsHandler handler) {
        this.handler = handler;
    }

    /* loaded from: classes.dex */
    public static class PlatformViewCreationRequest {
        public final int direction;
        public final RequestedDisplayMode displayMode;
        public final double logicalHeight;
        public final double logicalLeft;
        public final double logicalTop;
        public final double logicalWidth;
        public final ByteBuffer params;
        public final int viewId;
        public final String viewType;

        /* loaded from: classes.dex */
        public enum RequestedDisplayMode {
            TEXTURE_WITH_VIRTUAL_FALLBACK,
            TEXTURE_WITH_HYBRID_FALLBACK,
            HYBRID_ONLY
        }

        public PlatformViewCreationRequest(int viewId, String viewType, double logicalTop, double logicalLeft, double logicalWidth, double logicalHeight, int direction, ByteBuffer params) {
            this(viewId, viewType, logicalTop, logicalLeft, logicalWidth, logicalHeight, direction, RequestedDisplayMode.TEXTURE_WITH_VIRTUAL_FALLBACK, params);
        }

        public PlatformViewCreationRequest(int viewId, String viewType, double logicalTop, double logicalLeft, double logicalWidth, double logicalHeight, int direction, RequestedDisplayMode displayMode, ByteBuffer params) {
            this.viewId = viewId;
            this.viewType = viewType;
            this.logicalTop = logicalTop;
            this.logicalLeft = logicalLeft;
            this.logicalWidth = logicalWidth;
            this.logicalHeight = logicalHeight;
            this.direction = direction;
            this.displayMode = displayMode;
            this.params = params;
        }
    }

    /* loaded from: classes.dex */
    public static class PlatformViewResizeRequest {
        public final double newLogicalHeight;
        public final double newLogicalWidth;
        public final int viewId;

        public PlatformViewResizeRequest(int viewId, double newLogicalWidth, double newLogicalHeight) {
            this.viewId = viewId;
            this.newLogicalWidth = newLogicalWidth;
            this.newLogicalHeight = newLogicalHeight;
        }
    }

    /* loaded from: classes.dex */
    public static class PlatformViewBufferSize {
        public final int height;
        public final int width;

        public PlatformViewBufferSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    /* loaded from: classes.dex */
    public static class PlatformViewTouch {
        public final int action;
        public final int buttonState;
        public final int deviceId;
        public final Number downTime;
        public final int edgeFlags;
        public final Number eventTime;
        public final int flags;
        public final int metaState;
        public final long motionEventId;
        public final int pointerCount;
        public final Object rawPointerCoords;
        public final Object rawPointerPropertiesList;
        public final int source;
        public final int viewId;
        public final float xPrecision;
        public final float yPrecision;

        public PlatformViewTouch(int viewId, Number downTime, Number eventTime, int action, int pointerCount, Object rawPointerPropertiesList, Object rawPointerCoords, int metaState, int buttonState, float xPrecision, float yPrecision, int deviceId, int edgeFlags, int source, int flags, long motionEventId) {
            this.viewId = viewId;
            this.downTime = downTime;
            this.eventTime = eventTime;
            this.action = action;
            this.pointerCount = pointerCount;
            this.rawPointerPropertiesList = rawPointerPropertiesList;
            this.rawPointerCoords = rawPointerCoords;
            this.metaState = metaState;
            this.buttonState = buttonState;
            this.xPrecision = xPrecision;
            this.yPrecision = yPrecision;
            this.deviceId = deviceId;
            this.edgeFlags = edgeFlags;
            this.source = source;
            this.flags = flags;
            this.motionEventId = motionEventId;
        }
    }
}
