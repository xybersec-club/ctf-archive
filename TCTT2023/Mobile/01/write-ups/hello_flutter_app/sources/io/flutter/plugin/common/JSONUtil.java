package io.flutter.plugin.common;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class JSONUtil {
    private JSONUtil() {
    }

    public static Object unwrap(Object o) {
        if (JSONObject.NULL.equals(o) || o == null) {
            return null;
        }
        if ((o instanceof Boolean) || (o instanceof Byte) || (o instanceof Character) || (o instanceof Double) || (o instanceof Float) || (o instanceof Integer) || (o instanceof Long) || (o instanceof Short) || (o instanceof String)) {
            return o;
        }
        if (o instanceof JSONArray) {
            List<Object> list = new ArrayList<>();
            JSONArray array = (JSONArray) o;
            for (int i = 0; i < array.length(); i++) {
                list.add(unwrap(array.get(i)));
            }
            return list;
        }
        if (o instanceof JSONObject) {
            Map<String, Object> map = new HashMap<>();
            JSONObject jsonObject = (JSONObject) o;
            Iterator<String> keyIterator = jsonObject.keys();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                map.put(key, unwrap(jsonObject.get(key)));
            }
            return map;
        }
        return null;
    }

    public static Object wrap(Object o) {
        if (o == null) {
            return JSONObject.NULL;
        }
        if ((o instanceof JSONArray) || (o instanceof JSONObject)) {
            return o;
        }
        if (o.equals(JSONObject.NULL)) {
            return o;
        }
        try {
            if (o instanceof Collection) {
                JSONArray result = new JSONArray();
                for (Object e : (Collection) o) {
                    result.put(wrap(e));
                }
                return result;
            } else if (o.getClass().isArray()) {
                JSONArray result2 = new JSONArray();
                int length = Array.getLength(o);
                for (int i = 0; i < length; i++) {
                    result2.put(wrap(Array.get(o, i)));
                }
                return result2;
            } else if (o instanceof Map) {
                JSONObject result3 = new JSONObject();
                for (Map.Entry<?, ?> entry : ((Map) o).entrySet()) {
                    result3.put((String) entry.getKey(), wrap(entry.getValue()));
                }
                return result3;
            } else {
                if (!(o instanceof Boolean) && !(o instanceof Byte) && !(o instanceof Character) && !(o instanceof Double) && !(o instanceof Float) && !(o instanceof Integer) && !(o instanceof Long) && !(o instanceof Short) && !(o instanceof String)) {
                    if (o.getClass().getPackage().getName().startsWith("java.")) {
                        return o.toString();
                    }
                    return null;
                }
                return o;
            }
        } catch (Exception e2) {
            return null;
        }
    }
}
