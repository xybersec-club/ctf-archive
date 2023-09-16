package android.support.transition;

import android.animation.PropertyValuesHolder;
import android.animation.TypeConverter;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Property;
/* loaded from: classes.dex */
class PropertyValuesHolderUtilsApi21 implements PropertyValuesHolderUtilsImpl {
    @Override // android.support.transition.PropertyValuesHolderUtilsImpl
    public PropertyValuesHolder ofPointF(Property<?, PointF> property, Path path) {
        return PropertyValuesHolder.ofObject(property, (TypeConverter) null, path);
    }
}
