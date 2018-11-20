package media.platform.amf.redundant.handler;

import media.platform.amf.common.JsonMessage;
import media.platform.amf.redundant.RedundantInfoSimple;
import media.platform.amf.session.SessionState;
import media.platform.amf.session.SessionStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedunProcInboundGetAnswerReq implements RedunProcMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RedunProcInboundGetAnswerReq.class);

    @Override
    public boolean handle(String body) {
        if (body == null) {
            logger.error("Null body");
            return false;
        }

        RedundantInfoSimple redundantInfo = (RedundantInfoSimple)new JsonMessage(RedundantInfoSimple.class).parse(body);
        logger.debug("<- Redundant (InboundGetAnswerReq): sessionId [{}]", redundantInfo.getSessionId());

        if (redundantInfo.getSessionId() == null) {
            return false;
        }

        SessionStateManager.getInstance().setState(redundantInfo.getSessionId(), SessionState.ANSWER);

        return true;
    }
}
