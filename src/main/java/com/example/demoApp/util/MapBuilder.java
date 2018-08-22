package com.example.demoApp.util;

import java.util.HashMap;
import java.util.Map;

public final class MapBuilder<T, U> {

    private boolean built;

    private HashMap<T, U> map = new HashMap<>();

    public static <T, U> MapBuilder<T, U> from(T key, U value) {
        MapBuilder<T, U> builder = new MapBuilder<>();
        builder.map.put(key, value);
        return builder;
    }

    private MapBuilder() {
    }

    public MapBuilder<T, U> and(T key, U value) {
        if (built) {
            throw new IllegalStateException("Map Already Built");
        }
        map.put(key, value);
        return this;
    }

    public Map<T, U> build() {
        built = true;
        return map;
    }

}
