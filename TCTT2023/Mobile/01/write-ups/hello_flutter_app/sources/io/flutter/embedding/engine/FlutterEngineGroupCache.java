package io.flutter.embedding.engine;

import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class FlutterEngineGroupCache {
    private static volatile FlutterEngineGroupCache instance;
    private final Map<String, FlutterEngineGroup> cachedEngineGroups = new HashMap();

    public static FlutterEngineGroupCache getInstance() {
        if (instance == null) {
            synchronized (FlutterEngineGroupCache.class) {
                if (instance == null) {
                    instance = new FlutterEngineGroupCache();
                }
            }
        }
        return instance;
    }

    FlutterEngineGroupCache() {
    }

    public boolean contains(String engineGroupId) {
        return this.cachedEngineGroups.containsKey(engineGroupId);
    }

    public FlutterEngineGroup get(String engineGroupId) {
        return this.cachedEngineGroups.get(engineGroupId);
    }

    public void put(String engineGroupId, FlutterEngineGroup engineGroup) {
        if (engineGroup != null) {
            this.cachedEngineGroups.put(engineGroupId, engineGroup);
        } else {
            this.cachedEngineGroups.remove(engineGroupId);
        }
    }

    public void remove(String engineGroupId) {
        put(engineGroupId, null);
    }

    public void clear() {
        this.cachedEngineGroups.clear();
    }
}
