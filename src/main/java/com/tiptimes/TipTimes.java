package com.tiptimes;

import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tiptimes.http.Http;

import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

/**
 * Created by xinwenbo on 16/1/17.
 */
public class TipTimes {

    private static TipTimes tipTimes;
    private static Gson mGson;

    public static void initializer() {
        if (Build.VERSION.SDK_INT >= 23) {
            GsonBuilder gsonBuilder = new GsonBuilder().excludeFieldsWithModifiers(
                    Modifier.FINAL,
                    Modifier.TRANSIENT,
                    Modifier.STATIC);
            mGson = gsonBuilder.create();
        } else {
            mGson = new Gson();
        }

        Http.getInstance().setConnectTimeout(5000, TimeUnit.MILLISECONDS);
        Http.getInstance().debug("TestFrame");

    }

    public static Gson getGson() {
        return mGson;
    }
}
