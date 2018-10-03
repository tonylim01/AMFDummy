/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcOutgoingCommandReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import media.platform.amf.rmqif.messages.CommandStartReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.SessionInfo;

public class RmqProcOutgoingCommandReq extends RmqOutgoingMessage {
    private static final Logger logger = LoggerFactory.getLogger(RmqProcOutgoingCommandReq.class);

    public RmqProcOutgoingCommandReq(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_COMMAND_REQ);
    }

    public void setPlayDone(int channel) {
        SessionInfo sessionInfo = checkAndGetSession(getSessionId());
        if (sessionInfo == null) {
            return;
        }

        CommandStartReq req = new CommandStartReq();
        req.setType(CommandStartReq.CMD_TYPE_MEDIA_DONE);
        req.setChannel(channel);

        setBody(req, CommandStartReq.class);

    }
    /**
     * Sends a CommandReq to the given queue
     * @param queueName
     * @return
     */
    public boolean send(String queueName) {
        return sendTo(queueName);
    }
}
