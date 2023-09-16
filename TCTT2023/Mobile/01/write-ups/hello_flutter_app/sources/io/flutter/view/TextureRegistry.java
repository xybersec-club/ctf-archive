package io.flutter.view;

import android.graphics.SurfaceTexture;
import com.android.tools.r8.annotations.SynthesizedClassV2;
/* loaded from: classes.dex */
public interface TextureRegistry {

    /* loaded from: classes.dex */
    public interface OnFrameConsumedListener {
        void onFrameConsumed();
    }

    /* loaded from: classes.dex */
    public interface OnTrimMemoryListener {
        void onTrimMemory(int i);
    }

    SurfaceTextureEntry createSurfaceTexture();

    void onTrimMemory(int i);

    SurfaceTextureEntry registerSurfaceTexture(SurfaceTexture surfaceTexture);

    @SynthesizedClassV2(kind = 7, versionHash = "15f1483824cf4085ddca5a8529d873fc59a8ced2cbce67fb2b3dd9033ea03442")
    /* renamed from: io.flutter.view.TextureRegistry$-CC  reason: invalid class name */
    /* loaded from: classes.dex */
    public final /* synthetic */ class CC {
        public static void $default$onTrimMemory(TextureRegistry _this, int level) {
        }
    }

    /* loaded from: classes.dex */
    public interface SurfaceTextureEntry {
        long id();

        void release();

        void setOnFrameConsumedListener(OnFrameConsumedListener onFrameConsumedListener);

        void setOnTrimMemoryListener(OnTrimMemoryListener onTrimMemoryListener);

        SurfaceTexture surfaceTexture();

        @SynthesizedClassV2(kind = 7, versionHash = "15f1483824cf4085ddca5a8529d873fc59a8ced2cbce67fb2b3dd9033ea03442")
        /* renamed from: io.flutter.view.TextureRegistry$SurfaceTextureEntry$-CC  reason: invalid class name */
        /* loaded from: classes.dex */
        public final /* synthetic */ class CC {
            public static void $default$setOnFrameConsumedListener(SurfaceTextureEntry _this, OnFrameConsumedListener listener) {
            }

            public static void $default$setOnTrimMemoryListener(SurfaceTextureEntry _this, OnTrimMemoryListener listener) {
            }
        }
    }
}
