package com.heaven7.android.component.network;

import java.lang.reflect.Type;

/**
 * the request config
 * @since 1.1.6
 */
public class RequestConfig {

    public static final byte TYPE_GET = 1;
    public static final byte TYPE_POST_BODY = 2;
    public static final byte TYPE_POST_FORM = 3;

    public final String url;
    public final byte method;
    public final Type dataType;

    public RequestConfig(String url, byte method, Type dataType) {
        this.url = url;
        this.method = method;
        this.dataType = dataType;
    }
}