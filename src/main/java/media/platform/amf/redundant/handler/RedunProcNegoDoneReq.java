package media.platform.amf.redundant.handler;

import media.platform.amf.AppInstance;
import media.platform.amf.common.JsonMessage;
import media.platform.amf.config.SdpConfig;
import media.platform.amf.core.sdp.SdpInfo;
import media.platform.amf.core.sdp.SdpParser;
import media.platform.amf.redundant.RedundantClient;
import media.platform.amf.redundant.RedundantMessage;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionManager;
import media.platform.amf.session.SessionState;
import media.platform.amf.session.SessionStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedunProcNegoDoneReq implements RedunProcMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RedunProcNegoDoneReq.class);

    @Override
    public boolean handle(String body) {
        if (body == null) {
            logger.error("Null body");
            return false;
        }

        SessionInfo fromSessionInfo = (SessionInfo)new JsonMessage(SessionInfo.class).parse(body);
        logger.debug("<- Redundant (NegoDoneReq): sessionId [{}]", fromSessionInfo.getSessionId());

        SessionInfo sessionInfo = SessionManager.getInstance().getSession(fromSessionInfo.getSessionId());
        if (sessionInfo == null) {
            logger.warn("[{}] Session not found", fromSessionInfo.getSessionId());
            return false;
        }

        sessionInfo.setSdpInfo(fromSessionInfo.getSdpInfo());
        sessionInfo.setSdpDeviceInfo(fromSessionInfo.getSdpDeviceInfo());

        SessionStateManager.getInstance().setState(sessionInfo.getSessionId(), SessionState.PREPARE);

        return true;
    }
}
