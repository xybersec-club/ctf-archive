package kotlin.internal.jdk7;
/* compiled from: D8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class JDK7PlatformImplementations$$ExternalSyntheticBackport0 {
    public static /* synthetic */ Throwable[] m(Throwable th) {
        try {
            return (Throwable[]) Throwable.class.getDeclaredMethod("getSuppressed", new Class[0]).invoke(th, new Object[0]);
        } catch (Exception e) {
            return new Throwable[0];
        }
    }
}
