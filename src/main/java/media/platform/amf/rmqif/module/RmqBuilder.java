/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqBuilder.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.module;

import com.google.gson.Gson;
import media.platform.amf.rmqif.types.RmqMessage;

public class RmqBuilder {

    public static String build(RmqMessage msg) throws Exception {

        Gson gson = new Gson();
        String json = gson.toJson(msg);

        return json;
    }

}
