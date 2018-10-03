/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file JsonMessage.java
 * @author Tony Lim
 *
 */

package media.platform.amf.common;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class JsonMessage<T> {
    private Class<T> classType;

    public JsonMessage(Class<T> classType) {
        this.classType = classType;
    }

    public T parse(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, classType);
    }

    public T parse(JsonElement json) {
        Gson gson = new Gson();
        return gson.fromJson(json, classType);
    }

    public String build(T data) {
        Gson gson = new Gson();
        return gson.toJson(data);
    }
}
