package io.flutter.embedding.engine;

import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class FlutterEngineCache {
    private static FlutterEngineCache instance;
    private final Map<String, FlutterEngine> cachedEngines = new HashMap();

    public static FlutterEngineCache getInstance() {
        if (instance == null) {
            instance = new FlutterEngineCache();
        }
        return instance;
    }

    FlutterEngineCache() {
    }

    public boolean contains(String engineId) {
        return this.cachedEngines.containsKey(engineId);
    }

    public FlutterEngine get(String engineId) {
        return this.cachedEngines.get(engineId);
    }

    public void put(String engineId, FlutterEngine engine) {
        if (engine != null) {
            this.cachedEngines.put(engineId, engine);
        } else {
            this.cachedEngines.remove(engineId);
        }
    }

    public void remove(String engineId) {
        put(engineId, null);
    }

    public void clear() {
        this.cachedEngines.clear();
    }
}
