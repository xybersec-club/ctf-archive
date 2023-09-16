package io.flutter.embedding.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorSpace;
import android.graphics.Paint;
import android.hardware.HardwareBuffer;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import io.flutter.Log;
import io.flutter.embedding.engine.renderer.FlutterRenderer;
import io.flutter.embedding.engine.renderer.RenderSurface;
import java.nio.ByteBuffer;
import java.util.Locale;
/* loaded from: classes.dex */
public class FlutterImageView extends View implements RenderSurface {
    private static final String TAG = "FlutterImageView";
    private Bitmap currentBitmap;
    private Image currentImage;
    private FlutterRenderer flutterRenderer;
    private ImageReader imageReader;
    private boolean isAttachedToFlutterRenderer;
    private SurfaceKind kind;

    /* loaded from: classes.dex */
    public enum SurfaceKind {
        background,
        overlay
    }

    public ImageReader getImageReader() {
        return this.imageReader;
    }

    public FlutterImageView(Context context, int width, int height, SurfaceKind kind) {
        this(context, createImageReader(width, height), kind);
    }

    public FlutterImageView(Context context) {
        this(context, 1, 1, SurfaceKind.background);
    }

    public FlutterImageView(Context context, AttributeSet attrs) {
        this(context, 1, 1, SurfaceKind.background);
    }

    FlutterImageView(Context context, ImageReader imageReader, SurfaceKind kind) {
        super(context, null);
        this.isAttachedToFlutterRenderer = false;
        this.imageReader = imageReader;
        this.kind = kind;
        init();
    }

    private void init() {
        setAlpha(0.0f);
    }

    private static void logW(String format, Object... args) {
        Log.w(TAG, String.format(Locale.US, format, args));
    }

    private static ImageReader createImageReader(int width, int height) {
        if (width <= 0) {
            logW("ImageReader width must be greater than 0, but given width=%d, set width=1", Integer.valueOf(width));
            width = 1;
        }
        if (height <= 0) {
            logW("ImageReader height must be greater than 0, but given height=%d, set height=1", Integer.valueOf(height));
            height = 1;
        }
        if (Build.VERSION.SDK_INT >= 29) {
            return ImageReader.newInstance(width, height, 1, 3, 768L);
        }
        return ImageReader.newInstance(width, height, 1, 3);
    }

    public Surface getSurface() {
        return this.imageReader.getSurface();
    }

    @Override // io.flutter.embedding.engine.renderer.RenderSurface
    public FlutterRenderer getAttachedRenderer() {
        return this.flutterRenderer;
    }

    /* renamed from: io.flutter.embedding.android.FlutterImageView$1  reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$io$flutter$embedding$android$FlutterImageView$SurfaceKind;

        static {
            int[] iArr = new int[SurfaceKind.values().length];
            $SwitchMap$io$flutter$embedding$android$FlutterImageView$SurfaceKind = iArr;
            try {
                iArr[SurfaceKind.background.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$io$flutter$embedding$android$FlutterImageView$SurfaceKind[SurfaceKind.overlay.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    @Override // io.flutter.embedding.engine.renderer.RenderSurface
    public void attachToRenderer(FlutterRenderer flutterRenderer) {
        switch (AnonymousClass1.$SwitchMap$io$flutter$embedding$android$FlutterImageView$SurfaceKind[this.kind.ordinal()]) {
            case 1:
                flutterRenderer.swapSurface(this.imageReader.getSurface());
                break;
        }
        setAlpha(1.0f);
        this.flutterRenderer = flutterRenderer;
        this.isAttachedToFlutterRenderer = true;
    }

    @Override // io.flutter.embedding.engine.renderer.RenderSurface
    public void detachFromRenderer() {
        if (!this.isAttachedToFlutterRenderer) {
            return;
        }
        setAlpha(0.0f);
        acquireLatestImage();
        this.currentBitmap = null;
        closeCurrentImage();
        invalidate();
        this.isAttachedToFlutterRenderer = false;
    }

    @Override // io.flutter.embedding.engine.renderer.RenderSurface
    public void pause() {
    }

    public boolean acquireLatestImage() {
        if (this.isAttachedToFlutterRenderer) {
            Image newImage = this.imageReader.acquireLatestImage();
            if (newImage != null) {
                closeCurrentImage();
                this.currentImage = newImage;
                invalidate();
            }
            return newImage != null;
        }
        return false;
    }

    public void resizeIfNeeded(int width, int height) {
        if (this.flutterRenderer == null) {
            return;
        }
        if (width == this.imageReader.getWidth() && height == this.imageReader.getHeight()) {
            return;
        }
        closeCurrentImage();
        closeImageReader();
        this.imageReader = createImageReader(width, height);
    }

    public void closeImageReader() {
        this.imageReader.close();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.currentImage != null) {
            updateCurrentBitmap();
        }
        Bitmap bitmap = this.currentBitmap;
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
        }
    }

    private void closeCurrentImage() {
        Image image = this.currentImage;
        if (image != null) {
            image.close();
            this.currentImage = null;
        }
    }

    private void updateCurrentBitmap() {
        if (Build.VERSION.SDK_INT >= 29) {
            HardwareBuffer buffer = this.currentImage.getHardwareBuffer();
            this.currentBitmap = Bitmap.wrapHardwareBuffer(buffer, ColorSpace.get(ColorSpace.Named.SRGB));
            buffer.close();
            return;
        }
        Image.Plane[] imagePlanes = this.currentImage.getPlanes();
        if (imagePlanes.length != 1) {
            return;
        }
        Image.Plane imagePlane = imagePlanes[0];
        int desiredWidth = imagePlane.getRowStride() / imagePlane.getPixelStride();
        int desiredHeight = this.currentImage.getHeight();
        Bitmap bitmap = this.currentBitmap;
        if (bitmap == null || bitmap.getWidth() != desiredWidth || this.currentBitmap.getHeight() != desiredHeight) {
            this.currentBitmap = Bitmap.createBitmap(desiredWidth, desiredHeight, Bitmap.Config.ARGB_8888);
        }
        ByteBuffer buffer2 = imagePlane.getBuffer();
        buffer2.rewind();
        this.currentBitmap.copyPixelsFromBuffer(buffer2);
    }

    @Override // android.view.View
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        if ((width != this.imageReader.getWidth() || height != this.imageReader.getHeight()) && this.kind == SurfaceKind.background && this.isAttachedToFlutterRenderer) {
            resizeIfNeeded(width, height);
            this.flutterRenderer.swapSurface(this.imageReader.getSurface());
        }
    }
}
