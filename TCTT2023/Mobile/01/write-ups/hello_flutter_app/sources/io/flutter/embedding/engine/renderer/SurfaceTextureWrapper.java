package io.flutter.embedding.engine.renderer;

import android.graphics.SurfaceTexture;
/* loaded from: classes.dex */
public class SurfaceTextureWrapper {
    private boolean attached;
    private Runnable onFrameConsumed;
    private boolean released;
    private SurfaceTexture surfaceTexture;

    public SurfaceTextureWrapper(SurfaceTexture surfaceTexture) {
        this(surfaceTexture, null);
    }

    public SurfaceTextureWrapper(SurfaceTexture surfaceTexture, Runnable onFrameConsumed) {
        this.surfaceTexture = surfaceTexture;
        this.released = false;
        this.onFrameConsumed = onFrameConsumed;
    }

    public SurfaceTexture surfaceTexture() {
        return this.surfaceTexture;
    }

    public void updateTexImage() {
        synchronized (this) {
            if (!this.released) {
                this.surfaceTexture.updateTexImage();
                Runnable runnable = this.onFrameConsumed;
                if (runnable != null) {
                    runnable.run();
                }
            }
        }
    }

    public void release() {
        synchronized (this) {
            if (!this.released) {
                this.surfaceTexture.release();
                this.released = true;
                this.attached = false;
            }
        }
    }

    public void attachToGLContext(int texName) {
        synchronized (this) {
            if (this.released) {
                return;
            }
            if (this.attached) {
                this.surfaceTexture.detachFromGLContext();
            }
            this.surfaceTexture.attachToGLContext(texName);
            this.attached = true;
        }
    }

    public void detachFromGLContext() {
        synchronized (this) {
            if (this.attached && !this.released) {
                this.surfaceTexture.detachFromGLContext();
                this.attached = false;
            }
        }
    }

    public void getTransformMatrix(float[] mtx) {
        this.surfaceTexture.getTransformMatrix(mtx);
    }
}
