package androidx.core.location;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public final class LocationCompat {
    private static final String EXTRA_IS_MOCK = "mockLocation";
    private static Method sSetIsFromMockProviderMethod;

    private LocationCompat() {
    }

    public static long getElapsedRealtimeNanos(Location location) {
        if (Build.VERSION.SDK_INT >= 17) {
            return Api17Impl.getElapsedRealtimeNanos(location);
        }
        return TimeUnit.MILLISECONDS.toNanos(getElapsedRealtimeMillis(location));
    }

    public static long getElapsedRealtimeMillis(Location location) {
        if (Build.VERSION.SDK_INT >= 17) {
            return TimeUnit.NANOSECONDS.toMillis(Api17Impl.getElapsedRealtimeNanos(location));
        }
        long timeDeltaMs = System.currentTimeMillis() - location.getTime();
        long elapsedRealtimeMs = SystemClock.elapsedRealtime();
        if (timeDeltaMs < 0) {
            return elapsedRealtimeMs;
        }
        if (timeDeltaMs > elapsedRealtimeMs) {
            return 0L;
        }
        return elapsedRealtimeMs - timeDeltaMs;
    }

    public static boolean isMock(Location location) {
        if (Build.VERSION.SDK_INT >= 18) {
            return Api18Impl.isMock(location);
        }
        Bundle extras = location.getExtras();
        if (extras == null) {
            return false;
        }
        return extras.getBoolean(EXTRA_IS_MOCK, false);
    }

    public static void setMock(Location location, boolean mock) {
        if (Build.VERSION.SDK_INT >= 18) {
            try {
                getSetIsFromMockProviderMethod().invoke(location, Boolean.valueOf(mock));
                return;
            } catch (IllegalAccessException e) {
                Error error = new IllegalAccessError();
                error.initCause(e);
                throw error;
            } catch (NoSuchMethodException e2) {
                Error error2 = new NoSuchMethodError();
                error2.initCause(e2);
                throw error2;
            } catch (InvocationTargetException e3) {
                throw new RuntimeException(e3);
            }
        }
        Bundle extras = location.getExtras();
        if (extras == null) {
            Bundle extras2 = new Bundle();
            extras2.putBoolean(EXTRA_IS_MOCK, true);
            location.setExtras(extras2);
            return;
        }
        extras.putBoolean(EXTRA_IS_MOCK, true);
    }

    /* loaded from: classes.dex */
    private static class Api18Impl {
        private Api18Impl() {
        }

        static boolean isMock(Location location) {
            return location.isFromMockProvider();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Api17Impl {
        private Api17Impl() {
        }

        static long getElapsedRealtimeNanos(Location location) {
            return location.getElapsedRealtimeNanos();
        }
    }

    private static Method getSetIsFromMockProviderMethod() throws NoSuchMethodException {
        if (sSetIsFromMockProviderMethod == null) {
            Method declaredMethod = Location.class.getDeclaredMethod("setIsFromMockProvider", Boolean.TYPE);
            sSetIsFromMockProviderMethod = declaredMethod;
            declaredMethod.setAccessible(true);
        }
        return sSetIsFromMockProviderMethod;
    }
}
