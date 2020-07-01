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

    /**
     * convert map to url parameters
     * @return the url parameter. like "a=1&b=2"
     */
    public String toUrlParameters(){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> en : mMap.entrySet()){
            sb.append("&").append(en.getKey()).append("=").append(en.getValue());
        }
        String str = sb.toString();
        return str.substring(1);
    }
}
