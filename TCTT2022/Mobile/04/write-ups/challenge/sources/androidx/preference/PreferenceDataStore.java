package androidx.preference;

import java.util.Set;
/* loaded from: classes.dex */
public abstract class PreferenceDataStore {
    public boolean getBoolean(String str, boolean z) {
        return z;
    }

    public float getFloat(String str, float f) {
        return f;
    }

    public int getInt(String str, int i) {
        return i;
    }

    public long getLong(String str, long j) {
        return j;
    }

    public String getString(String str, String str2) {
        return str2;
    }

    public Set<String> getStringSet(String str, Set<String> set) {
        return set;
    }

    public void putString(String str, String str2) {
        throw new UnsupportedOperationException("Not implemented on this data store");
    }

    public void putStringSet(String str, Set<String> set) {
        throw new UnsupportedOperationException("Not implemented on this data store");
    }

    public void putInt(String str, int i) {
        throw new UnsupportedOperationException("Not implemented on this data store");
    }

    public void putLong(String str, long j) {
        throw new UnsupportedOperationException("Not implemented on this data store");
    }

    public void putFloat(String str, float f) {
        throw new UnsupportedOperationException("Not implemented on this data store");
    }

    public void putBoolean(String str, boolean z) {
        throw new UnsupportedOperationException("Not implemented on this data store");
    }
}
