package media.platform.amf.core.sdp;

import com.google.common.collect.ImmutableMap;
import media.platform.amf.common.StringUtil;

import java.util.Map;

public class SdpCodec {
    public static final int CODEC_UNKNOWN = -1;
    public static final int CODEC_ALAW = 1;
    public static final int CODEC_MULAW = 2;
    public static final int CODEC_AMR = 3;
    public static final int CODEC_EVS = 5;

    private static final Map<String, Integer> codecMap = ImmutableMap.<String, Integer>builder()
            .put("alaw", CODEC_ALAW)
            .put("mulaw", CODEC_MULAW)
            .put("amr", CODEC_AMR)
            .put("evs", CODEC_EVS)
            .put("PCMA", CODEC_ALAW)
            .put("PCMU", CODEC_MULAW)
            .put("AMR", CODEC_AMR)
            .put("EVS", CODEC_EVS)
            .build();

    public static final int getCodecId(String codecStr) {
        int codecId = CODEC_UNKNOWN;

        if (codecStr != null && codecMap.containsKey(codecStr)) {
            codecId = codecMap.get(codecStr);
        }

        return codecId;
    }

    public static final int getPayloadId(int codecId) {
        if (codecId == CODEC_ALAW) {
            return 8;
        }
        else if (codecId == CODEC_MULAW) {
            return 0;
        }

        return 0;
    }

}
