/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqOutgoingMessage.java
 * @author Tony Lim
 *
 */

package media.platform.amf.engine.handler.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.engine.EngineClient;
import media.platform.amf.engine.types.EngineRequestHeader;
import media.platform.amf.engine.types.EngineRequestMessage;
import media.platform.amf.rmqif.module.RmqBuilder;
import media.platform.amf.rmqif.module.RmqClient;
import media.platform.amf.rmqif.types.RmqHeader;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class EngineOutgoingMessage implements EngineOutgoingMessageInterface {

    private static final Logger logger = LoggerFactory.getLogger(EngineOutgoingMessage.class);

    private EngineRequestHeader header;
    private JsonElement jsonElement = null;

    public EngineOutgoingMessage() {
    }

    public EngineOutgoingMessage(String type, String cmd, String appId) {
        this.header = new EngineRequestHeader(type, cmd, appId);
    }

    @Override
    public void setBody(Object obj, Type objType) {
        Gson gson = new GsonBuilder().create();
        jsonElement = gson.toJsonTree(obj, objType);
    }

    @Override
    public boolean sendTo() {
        boolean result = false;

        EngineRequestMessage msg = new EngineRequestMessage(header);
        if (jsonElement != null) {
            msg.setBody(jsonElement);
        }

        try {
            Gson gson = new Gson();
            String json = gson.toJson(msg);

            if (json != null) {
                EngineClient client = EngineClient.getInstance();
                if (client != null) {
                    result = client.sendMessage(json);
                }
            }
            else {
                logger.error("json error: type [{}] cmd [{}] appId [{}]", header.getType(), header.getCmd(), header.getAppId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Combines two functions: setReasonCode() and setReasonStr()
     * @param reasonCode
     * @param reasonStr
     */
    /*
    protected void setReason(int reasonCode, String reasonStr) {
        setReasonCode(reasonCode);
        setReasonStr(reasonStr);
    }

    */
}
