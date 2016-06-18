package com.tiptimes.http.callback;

import okhttp3.Response;

import java.io.IOException;

/**
 * Created by xinwenbo on 16/1/17.
 */
public abstract class StringCallback extends Callback<String> {
    @Override
    public String parseNetworkResponse(Response response) throws IOException {
        return response.body().string();
    }

}
