package io.flutter.embedding.engine;

import android.view.Surface;
/* loaded from: classes.dex */
public class FlutterOverlaySurface {
    private final int id;
    private final Surface surface;

    public FlutterOverlaySurface(int id, Surface surface) {
        this.id = id;
        this.surface = surface;
    }

    public int getId() {
        return this.id;
    }

    public Surface getSurface() {
        return this.surface;
    }
}
