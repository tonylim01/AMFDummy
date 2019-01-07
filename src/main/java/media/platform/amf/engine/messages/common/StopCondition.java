package media.platform.amf.engine.messages.common;

import com.google.gson.annotations.SerializedName;

public class StopCondition {
    @SerializedName("silence")
    private int silenceDuration;
    private int timeout;

    public int getSilenceDuration() {
        return silenceDuration;
    }

    public void setSilenceDuration(int silenceDuration) {
        this.silenceDuration = silenceDuration;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
