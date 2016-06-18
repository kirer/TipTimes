package com.tiptimes.http.request;

import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Map;

/**
 * Created by xinwenbo on 16/1/17.
 */
public class GetRequest extends OkHttpRequest {
    public GetRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers) {
        super(url, tag, params, headers);
    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.get().build();
    }


}
