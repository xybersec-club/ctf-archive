package io.flutter.plugin.common;

import java.util.Map;
import org.json.JSONObject;
/* loaded from: classes.dex */
public final class MethodCall {
    public final Object arguments;
    public final String method;

    public MethodCall(String method, Object arguments) {
        if (method == null) {
            throw new AssertionError("Parameter method must not be null.");
        }
        this.method = method;
        this.arguments = arguments;
    }

    public <T> T arguments() {
        return (T) this.arguments;
    }

    public <T> T argument(String key) {
        Object obj = this.arguments;
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map) {
            return (T) ((Map) obj).get(key);
        }
        if (obj instanceof JSONObject) {
            return (T) ((JSONObject) obj).opt(key);
        }
        throw new ClassCastException();
    }

    public boolean hasArgument(String key) {
        Object obj = this.arguments;
        if (obj == null) {
            return false;
        }
        if (obj instanceof Map) {
            return ((Map) obj).containsKey(key);
        }
        if (obj instanceof JSONObject) {
            return ((JSONObject) obj).has(key);
        }
        throw new ClassCastException();
    }
}
