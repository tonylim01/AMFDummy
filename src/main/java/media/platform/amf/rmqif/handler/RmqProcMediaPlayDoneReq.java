/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcInboundGetAnswerRes.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.rmqif.handler.base.RmqOutgoingMessage;
import media.platform.amf.rmqif.messages.AiServiceReq;
import media.platform.amf.rmqif.messages.MediaPlayDoneReq;
import media.platform.amf.rmqif.types.RmqMessageType;
import media.platform.amf.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcMediaPlayDoneReq extends RmqOutgoingMessage {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcMediaPlayDoneReq.class);

    public RmqProcMediaPlayDoneReq(String sessionId, String transactionId) {
        super(sessionId, transactionId);
        setType(RmqMessageType.RMQ_MSG_STR_MEDIA_PLAY_DONE);
    }

    /**
     * Makes a response body and sends the message to AWF
     * @return
     */
    public boolean send(String queueName, int dir, int mentOfMusic) {

        SessionInfo sessionInfo = checkAndGetSession(getSessionId());
        if (sessionInfo == null) {
            return sendTo(queueName);
        }

        MediaPlayDoneReq req = new MediaPlayDoneReq();
        req.setDir(dir);
        req.setChannelId(mentOfMusic);

        setBody(req, MediaPlayDoneReq.class);

        return sendTo(queueName);
    }

}
