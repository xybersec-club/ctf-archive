package io.flutter.embedding.engine.mutatorsstack;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class FlutterMutatorsStack {
    private List<FlutterMutator> mutators = new ArrayList();
    private Matrix finalMatrix = new Matrix();
    private List<Path> finalClippingPaths = new ArrayList();

    /* loaded from: classes.dex */
    public enum FlutterMutatorType {
        CLIP_RECT,
        CLIP_RRECT,
        CLIP_PATH,
        TRANSFORM,
        OPACITY
    }

    /* loaded from: classes.dex */
    public class FlutterMutator {
        private Matrix matrix;
        private Path path;
        private float[] radiis;
        private Rect rect;
        private FlutterMutatorType type;

        public FlutterMutator(Rect rect) {
            this.type = FlutterMutatorType.CLIP_RECT;
            this.rect = rect;
        }

        public FlutterMutator(Rect rect, float[] radiis) {
            this.type = FlutterMutatorType.CLIP_RRECT;
            this.rect = rect;
            this.radiis = radiis;
        }

        public FlutterMutator(Path path) {
            this.type = FlutterMutatorType.CLIP_PATH;
            this.path = path;
        }

        public FlutterMutator(Matrix matrix) {
            this.type = FlutterMutatorType.TRANSFORM;
            this.matrix = matrix;
        }

        public FlutterMutatorType getType() {
            return this.type;
        }

        public Rect getRect() {
            return this.rect;
        }

        public Path getPath() {
            return this.path;
        }

        public Matrix getMatrix() {
            return this.matrix;
        }
    }

    public void pushTransform(float[] values) {
        Matrix matrix = new Matrix();
        matrix.setValues(values);
        FlutterMutator mutator = new FlutterMutator(matrix);
        this.mutators.add(mutator);
        this.finalMatrix.preConcat(mutator.getMatrix());
    }

    public void pushClipRect(int left, int top, int right, int bottom) {
        Rect rect = new Rect(left, top, right, bottom);
        FlutterMutator mutator = new FlutterMutator(rect);
        this.mutators.add(mutator);
        Path path = new Path();
        path.addRect(new RectF(rect), Path.Direction.CCW);
        path.transform(this.finalMatrix);
        this.finalClippingPaths.add(path);
    }

    public void pushClipRRect(int left, int top, int right, int bottom, float[] radiis) {
        Rect rect = new Rect(left, top, right, bottom);
        FlutterMutator mutator = new FlutterMutator(rect, radiis);
        this.mutators.add(mutator);
        Path path = new Path();
        path.addRoundRect(new RectF(rect), radiis, Path.Direction.CCW);
        path.transform(this.finalMatrix);
        this.finalClippingPaths.add(path);
    }

    public List<FlutterMutator> getMutators() {
        return this.mutators;
    }

    public List<Path> getFinalClippingPaths() {
        return this.finalClippingPaths;
    }

    public Matrix getFinalMatrix() {
        return this.finalMatrix;
    }
}
