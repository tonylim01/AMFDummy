/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcHeartbeatReq.java
 * @author Tony Lim
 *
 */


package media.platform.amf.rmqif.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import media.platform.amf.rmqif.messages.HeartbeatReq;
import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.room.RoomManager;

public class RmqProcHeartbeatReq extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcIncomingHangupRes.class);

    public RmqProcHeartbeatReq(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_HEARTBEAT);
    }

    public boolean send(String queueName) {
        AmfConfig config = AppInstance.getInstance().getConfig();

        HeartbeatReq req = new HeartbeatReq();

        SessionManager sessionManager = SessionManager.getInstance();

        req.setAmfId(config.getAmfId());

        req.setSessionTotal(sessionManager.getTotalCount());
        req.setSessionIdle(sessionManager.getIdleCount());

        RoomManager roomManager = RoomManager.getInstance();

        req.setConferenceChannelTotal(roomManager.getTotalRoomCount());
        req.setConferenceChannelIdle(roomManager.getIdleRoomCount());

        setBody(req, HeartbeatReq.class);

        return sendTo(queueName);
    }
}
