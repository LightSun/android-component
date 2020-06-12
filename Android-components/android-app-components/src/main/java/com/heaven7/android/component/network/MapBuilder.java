package com.heaven7.android.component.network;

import java.util.HashMap;
import java.util.Map;

/**
 * the map builder used to build map.
 * @author heaven7
 * @since 1.1.6
 */
public class MapBuilder {

    private final Map<String, Object> mMap = new HashMap<>();

    public MapBuilder pair(String key, Object val){
        mMap.put(key, val);
        return this;
    }
    public MapBuilder subMap(String key, MapBuilder builder){
        mMap.put(key, builder.toMap());
        return this;
    }

    public Map<String, Object > toMap(){
        return mMap;
    }


}
