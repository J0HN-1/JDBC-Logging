package com.example.demoApp.util;

import java.util.HashMap;
import java.util.Map;

public final class MapBuilder {

    private boolean built;

    private HashMap<String, Object> map = new HashMap<>();

    public static MapBuilder init() {
        return new MapBuilder();
    }

    private MapBuilder() {
    }

    public MapBuilder put(String key, Object value) {
        if (built) {
            throw new IllegalStateException("Map Already Built");
        }
        map.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        built = true;
        return map;
    }

}
