package media.platform.amf.redundant.handler;

import media.platform.amf.common.JsonMessage;
import media.platform.amf.redundant.messages.JitterSenderInfo;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedunProcUpdateJitterSenderReq implements RedunProcMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RedunProcUpdateJitterSenderReq.class);

    @Override
    public boolean handle(String body) {
        if (body == null) {
            logger.error("Null body");
            return false;
        }

        JitterSenderInfo jitterSenderInfo = (JitterSenderInfo)new JsonMessage(JitterSenderInfo.class).parse(body);
        //logger.debug("<- Redundant (UpdateJitterSenderReq): sessionId [{}]", jitterSenderInfo.getSessionId());

        if (jitterSenderInfo.getSessionId() == null) {
            return false;
        }

        SessionInfo sessionInfo = SessionManager.getInstance().getSession(jitterSenderInfo.getSessionId());

        if (sessionInfo == null) {
            logger.debug("[{}] Session not found", jitterSenderInfo.getSessionId());
            return false;
        }

        sessionInfo.getUdpSender().updateRtpInfo(jitterSenderInfo.getSeq(),
                jitterSenderInfo.getSsrc(), jitterSenderInfo.getTimestamp());

        return true;
    }
}
