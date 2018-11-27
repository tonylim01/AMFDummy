/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqProcInboundGetAnswerReq.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.redundant.RedundantClient;
import media.platform.amf.redundant.RedundantMessage;
import media.platform.amf.rmqif.handler.base.RmqIncomingMessageHandler;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;
import media.platform.amf.session.SessionStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmqProcInboundGetAnswerReq extends RmqIncomingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RmqProcInboundGetAnswerReq.class);

    @Override
    public boolean handle(RmqMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.error("[{}] Invalid message");
            return false;
        }

        logger.info("[{}] InboundGetAnswerReq", msg.getSessionId());

        SessionInfo sessionInfo = validateSessionId(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());
        if (sessionInfo == null) {
            logger.error("[{}] Session not found", msg.getSessionId());
            return false;
        }

//        allocLocalResource(sessionInfo);

        SessionStateManager.getInstance().setState(msg.getSessionId(), SessionState.ANSWER);

        sendResponse(msg.getSessionId(), msg.getHeader().getTransactionId(), msg.getHeader().getMsgFrom());

        if (AppInstance.getInstance().getConfig().getRedundantConfig().isRun()) {
            RedundantClient.getInstance().sendMessageSimple(RedundantMessage.RMT_SN_INBOUND_GET_ANSER_REQ, msg.getSessionId());
        }

        return false;
    }

    @Override
    public void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr) {

        RmqProcInboundGetAnswerRes res = new RmqProcInboundGetAnswerRes(sessionId, transactionId);

        res.setReasonCode(reasonCode);
        res.setReasonStr(reasonStr);

        if (res.send(queueName) == false) {
            // TODO
        }
    }

    /**
     * Allocates a local media resource to receive RTP packets
     * @param sessionInfo
     * @return
     */
    private boolean allocLocalResource(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return false;
        }

        if (sessionInfo.getSdpInfo() != null) {
            // Inbound
            // Returns a caller's sdp

        }
        else {
            // Outbound
            // Returns a callee's sdp
        }

        /***
         * Local relay demo
         */
        /*
        SdpConfig config = AppInstance.getInstance().getConfig().getSdpConfig();
        sessionInfo.setLocalIpAddress(config.getLocalIpAddress());

        UdpRelayManager udpRelayManager = UdpRelayManager.getInstance();
        int srcLocalPort = udpRelayManager.getNextLocalPort();
        int dstLocalPort = udpRelayManager.getNextLocalPort();

        sessionInfo.setSrcLocalPort(srcLocalPort);
        sessionInfo.setDstLocalPort(dstLocalPort);

        logger.debug("[{}] Alloc local media: ip [{}] srcPort [{}] dstPort [{}]", sessionInfo.getSessionId(),
                sessionInfo.getLocalIpAddress(), sessionInfo.getSrcLocalPort(), sessionInfo.getDstLocalPort());
        */

        return true;
    }
}

