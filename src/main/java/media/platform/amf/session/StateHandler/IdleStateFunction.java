package media.platform.amf.session.StateHandler;

import media.platform.amf.engine.EngineClient;
import media.platform.amf.engine.handler.EngineProcAudioCreateReq;
import media.platform.amf.engine.handler.EngineProcAudioDeleteReq;
import media.platform.amf.engine.messages.SysConnectReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.service.ServiceManager;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;

import java.util.UUID;

public class IdleStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(IdleStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo, Object arg) {
        if (sessionInfo == null) {
            return;
        }

        logger.debug("[{}] IdleStateFunction", sessionInfo.getSessionId());

//        BiUdpRelayManager.getInstance().close(sessionInfo.getSessionId());

        if (sessionInfo.getServiceState() != SessionState.IDLE) {
            sessionInfo.setServiceState(SessionState.IDLE);
        }

        String appId = UUID.randomUUID().toString();
        EngineProcAudioDeleteReq audioDeleteReq = new EngineProcAudioDeleteReq(appId);
        audioDeleteReq.setData(sessionInfo);

        if (audioDeleteReq.send()) {
            EngineClient.getInstance().pushSentQueue(appId, SysConnectReq.class, audioDeleteReq.getData());
        }

        ServiceManager.getInstance().releaseResource(sessionInfo.getSessionId());
    }
}
