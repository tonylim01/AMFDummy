/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqParser.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.module;

import com.google.gson.Gson;
import media.platform.amf.rmqif.types.RmqMessage;

public class RmqParser {

    public static RmqMessage parse(String json) throws Exception {

        Gson gson = new Gson();
        RmqMessage msg = gson.fromJson(json, RmqMessage.class);

        return msg;
    }


}
