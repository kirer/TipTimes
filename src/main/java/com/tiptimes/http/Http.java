package com.tiptimes.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.tiptimes.http.cookie.SimpleCookieJar;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.tiptimes.http.builder.GetBuilder;
import com.tiptimes.http.builder.PostFileBuilder;
import com.tiptimes.http.builder.PostFormBuilder;
import com.tiptimes.http.builder.PostStringBuilder;
import com.tiptimes.http.callback.Callback;
import com.tiptimes.http.https.HttpsUtils;
import com.tiptimes.http.request.RequestCall;
import com.tiptimes.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by xinwenbo on 15/8/17.
 */
public class Http {
    public static final String TAG = "OkHttpUtils";
    public static final long DEFAULT_MILLISECONDS = 10000;
    private static Http mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;

    private Http() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        //cookie enabled
        okHttpClientBuilder.cookieJar(new SimpleCookieJar());
        mDelivery = new Handler(Looper.getMainLooper());

        if (true) {
            okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }

        mOkHttpClient = okHttpClientBuilder.build();
    }

    private boolean debug;
    private String tag;

    public Http debug(String tag) {
        debug = true;
        this.tag = tag;
        return this;
    }


    public static Http getInstance() {
        if (mInstance == null) {
            synchronized (Http.class) {
                if (mInstance == null) {
                    mInstance = new Http();
                }
            }
        }
        return mInstance;
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }


    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }


    public void execute(final RequestCall requestCall, Callback callback) {
        if (debug) {
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            L.d(tag + "{method:" + requestCall.getRequest().method() + ", detail:" + requestCall.getOkHttpRequest().toString() + "}");
        }

        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;
        final Callback finalCallback = callback;

        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                sendFailResultCallback(request, e, finalCallback);
            }

            @Override
            public void onResponse(final Response response) {
                try {
                    String result = response.body().string();
                    if (response.code() >= 400 && response.code() <= 599) {
                        sendFailResultCallback(requestCall.getRequest(), new RuntimeException(result), finalCallback);
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("status") == 1) {
                        Object o = finalCallback.parseNetworkResponse(result);
                        sendSuccessResultCallback(o, finalCallback);
                    } else {
                        sendFailResultCallback(response.request(), new Exception(jsonObject.getString("msg")), finalCallback);
                    }

                } catch (JSONException e) {
                    sendFailResultCallback(response.request(), e, finalCallback);
                    e.printStackTrace();
                } catch (IOException e) {
                    sendFailResultCallback(response.request(), e, finalCallback);
                    e.printStackTrace();
                } catch (Exception e) {
                    sendFailResultCallback(response.request(), e, finalCallback);
                    e.printStackTrace();
                }
            }
        });
    }


    public void sendFailResultCallback(final Request request, final Exception e, final Callback callback) {
        if (callback == null) return;

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(request, e);
                callback.onAfter();
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final Callback callback) {
        if (callback == null) return;
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(object);
                callback.onAfter();
            }
        });
    }

    public void cancelTag(Object tag) {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }


    public void setCertificates(InputStream... certificates) {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, null, null))
                .build();
    }


    public void setConnectTimeout(int timeout, TimeUnit units) {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .connectTimeout(timeout, units)
                .build();
    }
}

