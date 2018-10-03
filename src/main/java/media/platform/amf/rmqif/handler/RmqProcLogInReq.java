/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcLogInReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import media.platform.amf.rmqif.messages.HeartbeatReq;
import media.platform.amf.rmqif.messages.LogInReq;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class RmqProcLogInReq extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcLogInReq.class);

    public RmqProcLogInReq(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_LOGIN_REQ);
    }

    public boolean send(String queueName) {
        AmfConfig config = AppInstance.getInstance().getConfig();
        LogInReq req = new LogInReq();

        logger.error( "AMF {}  Login Message Send " ,config.getAmfId());

        req.setAmfid(config.getAmfId());

        setBody(req, LogInReq.class);

        return sendTo(queueName);
    }


}
