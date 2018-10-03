/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqData.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.module;

import com.google.gson.Gson;
import media.platform.amf.rmqif.types.RmqMessage;

public class RmqData<T> {

    private Class<T> classType;

    public RmqData(Class<T> classType) {
        this.classType = classType;
    }

    public T parse(RmqMessage rmq) {
        Gson gson = new Gson();
        return gson.fromJson(rmq.getBody(), classType);
    }

    public String build(T data) {
        Gson gson = new Gson();
        return gson.toJson(data);
    }
}
