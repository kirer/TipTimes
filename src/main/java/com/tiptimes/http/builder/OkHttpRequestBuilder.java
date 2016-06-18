package com.tiptimes.http.builder;

import com.tiptimes.http.request.RequestCall;

import java.util.Map;

/**
 * Created by xinwenbo on 16/1/17.
 */
public abstract class OkHttpRequestBuilder {
    protected String url;
    protected Object tag;
    protected Map<String, String> headers;
    protected Map<String, String> params;

    public abstract OkHttpRequestBuilder url(String url);

    public abstract OkHttpRequestBuilder tag(Object tag);

    public abstract OkHttpRequestBuilder params(Map<String, String> params);

    public abstract OkHttpRequestBuilder addParams(String key, String val);

    public abstract OkHttpRequestBuilder headers(Map<String, String> headers);

    public abstract OkHttpRequestBuilder addHeader(String key, String val);

    public abstract RequestCall build();


}
