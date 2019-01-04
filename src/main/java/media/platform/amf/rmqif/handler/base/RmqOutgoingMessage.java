/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqOutgoingMessage.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.AppInstance;
import media.platform.amf.config.UserConfig;
import media.platform.amf.rmqif.module.RmqBuilder;
import media.platform.amf.rmqif.module.RmqClient;
import media.platform.amf.rmqif.types.RmqHeader;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.rmqif.types.RmqMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;

import java.lang.reflect.Type;
import java.util.UUID;

public class RmqOutgoingMessage implements RmqOutgoingMessageInterface {

    private static final Logger logger = LoggerFactory.getLogger(RmqOutgoingMessage.class);

    public static final int RMQ_TARGET_ID_MCUD  = 1;

    private RmqHeader header;
    private JsonElement jsonElement = null;

    public RmqOutgoingMessage() {
        this.header = new RmqHeader();
    }

    public RmqOutgoingMessage(String sessionId, String transactionId) {
        this.header = new RmqHeader();

        setSessionId(sessionId);

        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
        }
        setTransactionId(transactionId);
    }

    @Override
    public void setType(String type) {
        header.setType(type);
    }

    @Override
    public void setTransactionId(String transactionId) {
        header.setTransactionId(transactionId);
    }

    @Override
    public void setSessionId(String sessionId) {
        header.setSessionId(sessionId);
    }

    @Override
    public void setMessageFrom(String messageFrom) {
        header.setMsgFrom(messageFrom);
    }

    @Override
    public void setTrxType(boolean trxType) {
        header.setTrxType(trxType);
    }

    @Override
    public void setReasonCode(int reasonCode) {
        header.setReasonCode(reasonCode);
    }

    @Override
    public void setReasonStr(String reasonStr) {
        header.setReason(reasonStr);
    }

    @Override
    public String getSessionId() {
        return header.getSessionId();
    }

    @Override
    public void setBody(Object obj, Type objType) {
        Gson gson = new GsonBuilder().create();
        jsonElement = gson.toJsonTree(obj, objType);
    }

    @Override
    public boolean sendTo(String target) {
        boolean result = false;

        UserConfig config = AppInstance.getInstance().getUserConfig();
        if (config == null) {
            return false;
        }

        if (target == null) {
            target = config.getMcudName();
        }

        if (config.getLocalName() != null) {
            header.setMsgFrom(config.getLocalName());
        }

        RmqMessage msg = new RmqMessage(header);
        if (jsonElement != null) {
            msg.setBody(jsonElement);
        }

        try {
            String json = RmqBuilder.build(msg);

            if (json != null) {
                if (msg.getMessageType() != RmqMessageType.RMQ_MSG_TYPE_HEARTBEAT) {
                    logger.debug("[{}] json=[{}]", msg.getSessionId(), json);
                }

                RmqClient client = RmqClient.getInstance(target);
                if (client != null) {
                    result = client.send(json);

                    if (result) {
                        if (msg.getHeader().getReason() == null) {
                            if (msg.getMessageType() != RmqMessageType.RMQ_MSG_TYPE_HEARTBEAT) {
                                logger.info("[{}] -> ({}) {}", msg.getSessionId(), target,
                                        RmqMessageType.getMessageTypeStr(msg.getMessageType()));
                            }
                        }
                        else {
                            logger.info("[{}] -> ({}) {}: code=[{}] reason=[{}]", msg.getSessionId(), target,
                                    RmqMessageType.getMessageTypeStr(msg.getMessageType()), msg.getHeader().getReasonCode(), msg.getHeader().getReason());
                        }
                    }
                    else {
                        logger.error("[{}] -> ({}) {} failed", msg.getSessionId(), target,
                                RmqMessageType.getMessageTypeStr(msg.getMessageType()));
                    }

                    return result;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public RmqHeader getHeader() {
        return header;
    }

    /**
     * Combines two functions: setReasonCode() and setReasonStr()
     * @param reasonCode
     * @param reasonStr
     */
    protected void setReason(int reasonCode, String reasonStr) {
        setReasonCode(reasonCode);
        setReasonStr(reasonStr);
    }

    protected SessionInfo checkAndGetSession(String sessionId) {
        SessionInfo sessionInfo = SessionManager.findSession(getSessionId());
        if (sessionInfo == null) {
            logger.error("[{}] No session found", getSessionId());
            SessionManager.getInstance().printSessionList();

            if (getHeader().getReasonCode() == RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_SUCCESS) {
                setReason(RmqMessageType.RMQ_MSG_COMMON_REASON_CODE_WRONG_PARAM, "NO SESSION FOUND");
            }
        }
        return sessionInfo;
    }

}
