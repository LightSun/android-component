package com.heaven7.android.component.network;

import java.util.HashMap;
import java.util.Map;

/**
 * the map builder used to build map.
 * @author heaven7
 * @since 1.1.6
 */
public class MapBuilder {

    private final Map<String, Object> mMap;

    public MapBuilder(Map<String, Object> mMap) {
        this.mMap = mMap;
    }
    public MapBuilder() {
        this.mMap = new HashMap<>();
    }

    public static MapBuilder of(Map<String, Object> map){
        return new MapBuilder(map);
    }

    public MapBuilder pair(String key, Object val){
        mMap.put(key, val);
        return this;
    }
    public MapBuilder pair(String key, MapBuilder builder){
        mMap.put(key, builder.toMap());
        return this;
    }
    public Map<String, Object> toMap(){
        return mMap;
    }
}
