package media.platform.amf.engine.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import media.platform.amf.engine.EngineClient;
import media.platform.amf.engine.EngineManager;
import media.platform.amf.engine.messages.SysHeartbeatRes;
import media.platform.amf.engine.types.EngineResponseMessage;
import media.platform.amf.engine.types.EngineResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineMessageHandlerSys extends DefaultEngineMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(EngineMessageHandlerSys.class);

    private EngineClient engineClient;

    public EngineMessageHandlerSys() {
        engineClient = EngineClient.getInstance();
    }

    public void handle(EngineResponseMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            return;
        }

        if (compareString(msg.getHeader().getCmd(), "connect")) {

            procSysConnectRes(msg);
        }
        else if (compareString(msg.getHeader().getCmd(), "heartbeat")) {

            procSysHeartbeatRes(msg);
        }

    }

    private void procSysConnectRes(EngineResponseMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        if (compareString(msg.getHeader().getResult(), EngineResponseResult.RESULT_OK) ||
            compareString(msg.getHeader().getResult(),EngineResponseResult.RESULT_SUCCESS)) {
            // Ok
            engineClient.setConnected(true);
        }
        else {
            // Error
            engineClient.setConnected(false);
        }

    }

    private void procSysHeartbeatRes(EngineResponseMessage msg) {
        if (msg == null || msg.getHeader() == null) {
            logger.warn("Null response message");
            return;
        }

        engineClient.checkHeartbeat(msg.getHeader().appId);

        Gson gson = new GsonBuilder().setLenient().create();

        SysHeartbeatRes heartbeatRes = gson.fromJson(msg.getBody(), SysHeartbeatRes.class);
        if (heartbeatRes != null && EngineManager.getInstance().isResourceChanged(heartbeatRes.getTotal(), heartbeatRes.getBusy(), heartbeatRes.getIdle())) {
            logger.debug("Heart resource: total [{}] busy [{}] idle [{}]", heartbeatRes.getTotal(), heartbeatRes.getBusy(), heartbeatRes.getIdle());
            EngineManager.getInstance().setResourceCount(heartbeatRes.getTotal(), heartbeatRes.getBusy(), heartbeatRes.getIdle());
        }
    }
}
