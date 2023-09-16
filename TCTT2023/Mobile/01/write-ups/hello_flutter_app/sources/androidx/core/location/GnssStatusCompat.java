package androidx.core.location;

import android.location.GnssStatus;
import android.location.GpsStatus;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public abstract class GnssStatusCompat {
    public static final int CONSTELLATION_BEIDOU = 5;
    public static final int CONSTELLATION_GALILEO = 6;
    public static final int CONSTELLATION_GLONASS = 3;
    public static final int CONSTELLATION_GPS = 1;
    public static final int CONSTELLATION_IRNSS = 7;
    public static final int CONSTELLATION_QZSS = 4;
    public static final int CONSTELLATION_SBAS = 2;
    public static final int CONSTELLATION_UNKNOWN = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ConstellationType {
    }

    public abstract float getAzimuthDegrees(int satelliteIndex);

    public abstract float getBasebandCn0DbHz(int satelliteIndex);

    public abstract float getCarrierFrequencyHz(int satelliteIndex);

    public abstract float getCn0DbHz(int satelliteIndex);

    public abstract int getConstellationType(int satelliteIndex);

    public abstract float getElevationDegrees(int satelliteIndex);

    public abstract int getSatelliteCount();

    public abstract int getSvid(int satelliteIndex);

    public abstract boolean hasAlmanacData(int satelliteIndex);

    public abstract boolean hasBasebandCn0DbHz(int satelliteIndex);

    public abstract boolean hasCarrierFrequencyHz(int satelliteIndex);

    public abstract boolean hasEphemerisData(int satelliteIndex);

    public abstract boolean usedInFix(int satelliteIndex);

    /* loaded from: classes.dex */
    public static abstract class Callback {
        public void onStarted() {
        }

        public void onStopped() {
        }

        public void onFirstFix(int ttffMillis) {
        }

        public void onSatelliteStatusChanged(GnssStatusCompat status) {
        }
    }

    public static GnssStatusCompat wrap(GnssStatus gnssStatus) {
        return new GnssStatusWrapper(gnssStatus);
    }

    public static GnssStatusCompat wrap(GpsStatus gpsStatus) {
        return new GpsStatusWrapper(gpsStatus);
    }
}
