package media.platform.amf.engine.messages.common;

import com.google.gson.annotations.SerializedName;

public class VolumeInfo {
    @SerializedName("volume")
    private int def;
    private int low;

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }
}
