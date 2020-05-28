package com.heaven7.android.component.network;

import java.lang.reflect.Type;

/**
 * the request config
 * @since 1.1.6
 */
public class RequestConfig {

    public final String url;
    public final boolean get;
    public final Type type;

    public RequestConfig(String url, boolean get, Type type) {
        this.url = url;
        this.get = get;
        this.type = type;
    }

    public RequestConfig(String url, Type type) {
       this(url, false, type);
    }
}