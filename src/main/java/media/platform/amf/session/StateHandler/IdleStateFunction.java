package media.platform.amf.session.StateHandler;

import media.platform.amf.AppInstance;
import media.platform.amf.common.AppId;
import media.platform.amf.engine.EngineClient;
import media.platform.amf.engine.EngineManager;
import media.platform.amf.engine.handler.EngineProcAudioCreateReq;
import media.platform.amf.engine.handler.EngineProcAudioDeleteReq;
import media.platform.amf.engine.handler.EngineProcMixerDeleteReq;
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

        if (!AppInstance.getInstance().getConfig().isRelayMode()) {

            if (sessionInfo.getEngineToolId() >= 0) {
                sendAudioDeleteReq(sessionInfo);
                EngineManager.getInstance().freeTool(sessionInfo.getEngineToolId());
            }


            if (sessionInfo.isCaller()) {
                sendMixerDeleteReq(sessionInfo);
            }
        }

        ServiceManager.getInstance().releaseResource(sessionInfo.getSessionId());

        sessionInfo.setEndOfState(SessionState.IDLE);

    }

    private void sendAudioDeleteReq(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return;
        }

        String appId = AppId.newId();
        EngineProcAudioDeleteReq audioDeleteReq = new EngineProcAudioDeleteReq(appId);
        audioDeleteReq.setData(sessionInfo);

        if (audioDeleteReq.send()) {
            EngineClient.getInstance().pushSentQueue(appId, SysConnectReq.class, audioDeleteReq.getData());
        }
    }

    private void sendMixerDeleteReq(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return;
        }

        String appId = AppId.newId();
        EngineProcMixerDeleteReq mixerDeleteReq = new EngineProcMixerDeleteReq(appId);
        mixerDeleteReq.setData(sessionInfo);

        if (mixerDeleteReq.send()) {
            EngineClient.getInstance().pushSentQueue(appId, SysConnectReq.class, mixerDeleteReq.getData());
        }
    }
}
