package media.platform.amf.engine.types;

public class EngineMessageType {
    public static final String MSG_TYPE_REQUEST = "request";
    public static final String MSG_TYPE_RESPONSE = "response";
    public static final String MSG_TYPE_REPORT = "report";

    public static final String HDR_TYPE_SYS = "sys";
    public static final String HDR_TYPE_AUDIO = "audio";
    public static final String HDR_TYPE_MIXER = "mixer";
    public static final String HDR_TYPE_WAKEUP = "wakeup";
    public static final String HDR_TYPE_FILE = "file";

    public static final String MSG_CMD_CONNECT = "connect";
    public static final String MSG_CMD_HEARTBEAT = "heartbeat";
    public static final String MSG_CMD_BRANCH = "branch";
    public static final String MSG_CMD_CREATE = "create";
    public static final String MSG_CMD_DELETE = "delete";
    public static final String MSG_CMD_UPDATE = "update";
    public static final String MSG_CMD_START = "start";
    public static final String MSG_CMD_STOP = "stop";
    public static final String MSG_CMD_PLAY = "play";

    public static final String MSG_RESULT_OK = "ok";
    public static final String MSG_RESULT_SUCCESS = "success";
    public static final String MSG_RESULT_ERROR = "error";
    public static final String MSG_RESULT_TIIMEOUT = "timeout";

    public static final String MSG_EVENT_SUCCESS = "success";
    public static final String MSG_EVENT_DETECTED = "detected";
    public static final String MSG_EVENT_REJECTED = "rejected";
    public static final String MSG_EVENT_STOPPED = "stopped";
    public static final String MSG_EVENT_TIMEOUT = "timeout";
    public static final String MSG_EVENT_ERROR = "error";
    public static final String MSG_EVENT_DONE = "done";

}
