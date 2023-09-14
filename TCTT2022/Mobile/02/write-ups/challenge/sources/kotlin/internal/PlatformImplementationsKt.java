package kotlin.internal;

import kotlin.KotlinVersion;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
/* compiled from: PlatformImplementations.kt */
@Metadata(d1 = {"\u0000\u001e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0002\b\u0004\u001a \u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u0005H\u0001\u001a\"\u0010\b\u001a\u0002H\t\"\n\b\u0000\u0010\t\u0018\u0001*\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0083\b¢\u0006\u0002\u0010\f\u001a\b\u0010\r\u001a\u00020\u0005H\u0002\"\u0010\u0010\u0000\u001a\u00020\u00018\u0000X\u0081\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"IMPLEMENTATIONS", "Lkotlin/internal/PlatformImplementations;", "apiVersionIsAtLeast", "", "major", "", "minor", "patch", "castToBaseType", "T", "", "instance", "(Ljava/lang/Object;)Ljava/lang/Object;", "getJavaVersion", "kotlin-stdlib"}, k = 2, mv = {1, 7, 1}, xi = 48)
/* loaded from: classes.dex */
public final class PlatformImplementationsKt {
    public static final PlatformImplementations IMPLEMENTATIONS;

    static {
        PlatformImplementations platformImplementations;
        Object newInstance;
        Object newInstance2;
        int javaVersion = getJavaVersion();
        if (javaVersion >= 65544 || javaVersion < 65536) {
            try {
                try {
                    newInstance = Class.forName("kotlin.internal.jdk8.JDK8PlatformImplementations").newInstance();
                    Intrinsics.checkNotNullExpressionValue(newInstance, "forName(\"kotlin.internal…entations\").newInstance()");
                } catch (ClassNotFoundException unused) {
                    Object newInstance3 = Class.forName("kotlin.internal.JRE8PlatformImplementations").newInstance();
                    Intrinsics.checkNotNullExpressionValue(newInstance3, "forName(\"kotlin.internal…entations\").newInstance()");
                    try {
                        if (newInstance3 == null) {
                            throw new NullPointerException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
                        }
                        platformImplementations = (PlatformImplementations) newInstance3;
                    } catch (ClassCastException e) {
                        ClassLoader classLoader = newInstance3.getClass().getClassLoader();
                        ClassLoader classLoader2 = PlatformImplementations.class.getClassLoader();
                        if (!Intrinsics.areEqual(classLoader, classLoader2)) {
                            throw new ClassNotFoundException("Instance class was loaded from a different classloader: " + classLoader + ", base type classloader: " + classLoader2, e);
                        }
                        throw e;
                    }
                }
            } catch (ClassNotFoundException unused2) {
            }
            try {
                if (newInstance == null) {
                    throw new NullPointerException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
                }
                platformImplementations = (PlatformImplementations) newInstance;
                IMPLEMENTATIONS = platformImplementations;
            } catch (ClassCastException e2) {
                ClassLoader classLoader3 = newInstance.getClass().getClassLoader();
                ClassLoader classLoader4 = PlatformImplementations.class.getClassLoader();
                if (!Intrinsics.areEqual(classLoader3, classLoader4)) {
                    throw new ClassNotFoundException("Instance class was loaded from a different classloader: " + classLoader3 + ", base type classloader: " + classLoader4, e2);
                }
                throw e2;
            }
        }
        if (javaVersion >= 65543 || javaVersion < 65536) {
            try {
                newInstance2 = Class.forName("kotlin.internal.jdk7.JDK7PlatformImplementations").newInstance();
                Intrinsics.checkNotNullExpressionValue(newInstance2, "forName(\"kotlin.internal…entations\").newInstance()");
                try {
                    try {
                    } catch (ClassCastException e3) {
                        ClassLoader classLoader5 = newInstance2.getClass().getClassLoader();
                        ClassLoader classLoader6 = PlatformImplementations.class.getClassLoader();
                        if (!Intrinsics.areEqual(classLoader5, classLoader6)) {
                            throw new ClassNotFoundException("Instance class was loaded from a different classloader: " + classLoader5 + ", base type classloader: " + classLoader6, e3);
                        }
                        throw e3;
                    }
                } catch (ClassNotFoundException unused3) {
                }
            } catch (ClassNotFoundException unused4) {
                Object newInstance4 = Class.forName("kotlin.internal.JRE7PlatformImplementations").newInstance();
                Intrinsics.checkNotNullExpressionValue(newInstance4, "forName(\"kotlin.internal…entations\").newInstance()");
                try {
                    if (newInstance4 == null) {
                        throw new NullPointerException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
                    }
                    platformImplementations = (PlatformImplementations) newInstance4;
                } catch (ClassCastException e4) {
                    ClassLoader classLoader7 = newInstance4.getClass().getClassLoader();
                    ClassLoader classLoader8 = PlatformImplementations.class.getClassLoader();
                    if (!Intrinsics.areEqual(classLoader7, classLoader8)) {
                        throw new ClassNotFoundException("Instance class was loaded from a different classloader: " + classLoader7 + ", base type classloader: " + classLoader8, e4);
                    }
                    throw e4;
                }
            }
            if (newInstance2 == null) {
                throw new NullPointerException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
            }
            platformImplementations = (PlatformImplementations) newInstance2;
            IMPLEMENTATIONS = platformImplementations;
        }
        platformImplementations = new PlatformImplementations();
        IMPLEMENTATIONS = platformImplementations;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static final /* synthetic */ <T> T castToBaseType(Object obj) {
        try {
            Intrinsics.reifiedOperationMarker(1, "T");
            Object obj2 = obj;
            return obj;
        } catch (ClassCastException e) {
            ClassLoader classLoader = obj.getClass().getClassLoader();
            Intrinsics.reifiedOperationMarker(4, "T");
            ClassLoader classLoader2 = Object.class.getClassLoader();
            if (!Intrinsics.areEqual(classLoader, classLoader2)) {
                throw new ClassNotFoundException("Instance class was loaded from a different classloader: " + classLoader + ", base type classloader: " + classLoader2, e);
            }
            throw e;
        }
    }

    private static final int getJavaVersion() {
        String property = System.getProperty("java.specification.version");
        if (property == null) {
            return 65542;
        }
        String str = property;
        int indexOf$default = StringsKt.indexOf$default((CharSequence) str, '.', 0, false, 6, (Object) null);
        if (indexOf$default < 0) {
            try {
                return Integer.parseInt(property) * 65536;
            } catch (NumberFormatException unused) {
                return 65542;
            }
        }
        int i = indexOf$default + 1;
        int indexOf$default2 = StringsKt.indexOf$default((CharSequence) str, '.', i, false, 4, (Object) null);
        if (indexOf$default2 < 0) {
            indexOf$default2 = property.length();
        }
        String substring = property.substring(0, indexOf$default);
        Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
        String substring2 = property.substring(i, indexOf$default2);
        Intrinsics.checkNotNullExpressionValue(substring2, "this as java.lang.String…ing(startIndex, endIndex)");
        try {
            return (Integer.parseInt(substring) * 65536) + Integer.parseInt(substring2);
        } catch (NumberFormatException unused2) {
            return 65542;
        }
    }

    public static final boolean apiVersionIsAtLeast(int i, int i2, int i3) {
        return KotlinVersion.CURRENT.isAtLeast(i, i2, i3);
    }
}
