package media.platform.amf.engine;

import media.platform.amf.engine.types.EngineToolInfo;
import media.platform.amf.engine.types.EngineToolState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class EngineManager {
    private static final Logger logger = LoggerFactory.getLogger(EngineManager.class);

    private static final int NUM_OF_TOOLIDS = 1024;

    private volatile static EngineManager engineManager = null;

    public static EngineManager getInstance() {
        if (engineManager == null) {
            engineManager = new EngineManager();
        }

        return engineManager;
    }

    //private Map<Integer, EngineToolInfo> toolInfoMap = null;
    private EngineToolInfo toolInfoRefs[] = null;
    private int lastToolId = 0;

    public EngineManager() {
        toolInfoRefs = new EngineToolInfo[NUM_OF_TOOLIDS];

        long timestamp = System.currentTimeMillis();

        for (int i = 0; i < toolInfoRefs.length; i++) {
            toolInfoRefs[i] = new EngineToolInfo();
            toolInfoRefs[i].setIdleTime(timestamp);
            //
            // TODO: TEST
            //
            toolInfoRefs[i].setState(EngineToolState.TOOL_IDLE);
        }
    }

    public synchronized int getIdleToolId() {

        int toolId = -1;
        int i;

        for (i = lastToolId; i < toolInfoRefs.length; i++) {
            if (toolInfoRefs[i].getState() == EngineToolState.TOOL_IDLE) {
                toolId = i;
                break;
            }
        }

        if (toolId < 0) {

            for (i = 0; i < lastToolId; i++) {
                if (toolInfoRefs[i].getState() == EngineToolState.TOOL_IDLE) {
                    toolId = i;
                    break;
                }
            }
        }

        if (toolId >= 0) {
            lastToolId += 1;

            if (lastToolId >= toolInfoRefs.length) {
                lastToolId = 0;
            }

            toolInfoRefs[toolId].setState(EngineToolState.TOOL_ALLOC);
        }

        return toolId;
    }

    public void freeTool(int toolId) {
        if (toolId < 0) {
            return;
        }

        synchronized (this) {

            toolInfoRefs[toolId].setState(EngineToolState.TOOL_IDLE);
            toolInfoRefs[toolId].setIdleTime(System.currentTimeMillis());
        }
    }

    private int resourceTotal = 0;
    private int resourceBusy = 0;
    private int resourceIdle = 0;

    public void setResourceCount(int total, int busy, int idle) {
        if (total >= 0) {
            resourceTotal = total;
        }

        if (busy >= 0) {
            resourceBusy = busy;
        }

        if (idle >= 0) {
            resourceIdle = idle;
        }
    }

    public boolean isResourceChanged(int total, int busy, int idle) {
        return (resourceTotal != total || resourceBusy != busy || resourceIdle != idle) ? true : false;
    }
}
