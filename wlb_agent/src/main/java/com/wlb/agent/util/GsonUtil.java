package com.wlb.agent.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonUtil {

    private static GsonUtil mGsonUtil;
    private Gson mGson;

    public static GsonUtil getInstance() {
        if (null == mGsonUtil) {
            mGsonUtil = new GsonUtil();
        }
        return mGsonUtil;
    }

    private GsonUtil() {
        mGson = new Gson();
    }

    public <T> String toJson(T classType) {
        return mGson.toJson(classType);
    }

    public <T> String toJson(T classType, Type type) {
        return mGson.toJson(classType, type);
    }

    public <T> T toClass(String json, Class<T> classname) {
        return mGson.fromJson(json, classname);
    }

    public <T> T toClass(String json, Type type) {
        return mGson.fromJson(json, type);
    }
}
