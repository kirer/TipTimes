package com.tiptimes.http.callback;

import com.google.gson.internal.$Gson$Types;
import com.tiptimes.TipTimes;
import com.tiptimes.utils.L;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by xinwenbo on 16/1/17.
 */

public  abstract class TTCallBack<T> extends Callback<T> {

    private Type mType;

    public TTCallBack() {
        mType = getSuperclassTypeParameter(getClass());
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        return $Gson$Types.canonicalize(((ParameterizedType) superclass).getActualTypeArguments()[0]);
    }

    @Override
    public T parseNetworkResponse(String result) throws Exception {
        L.e("result --> " + result);
        JSONObject jsonObject = new JSONObject(result);
        return TipTimes.getGson().fromJson(jsonObject.getString("data"), mType);
    }
}
