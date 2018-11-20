package media.platform.amf.redundant.handler;

import media.platform.amf.common.JsonMessage;
import media.platform.amf.redundant.messages.RedundantInfoSimple;
import media.platform.amf.session.SessionState;
import media.platform.amf.session.SessionStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedunProcOutboundGetAnswerReq implements RedunProcMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RedunProcOutboundGetAnswerReq.class);

    @Override
    public boolean handle(String body) {
        if (body == null) {
            logger.error("Null body");
            return false;
        }

        RedundantInfoSimple redundantInfo = (RedundantInfoSimple)new JsonMessage(RedundantInfoSimple.class).parse(body);
        logger.debug("<- Redundant (OutboundGetAnswerReq): sessionId [{}]", redundantInfo.getSessionId());

        if (redundantInfo.getSessionId() == null) {
            return false;
        }

        SessionStateManager.getInstance().setState(redundantInfo.getSessionId(), SessionState.ANSWER);

        return true;
    }
}
