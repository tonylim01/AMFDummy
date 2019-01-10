package media.platform.amf.config;

import media.platform.amf.core.socket.packets.Vocoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.core.config.DefaultConfig;
import media.platform.amf.common.StringUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PromptConfig extends DefaultConfig {

    private static final Logger logger = LoggerFactory.getLogger(PromptConfig.class);

    private static final float DEFAULT_VOLUME = 0.1f;

    private float mentVolume;
    private float bgmVolume;

    private String promptDir;
    private Map<Integer, String> waitingPrompts;

    public PromptConfig(String configPath) {

        super(configPath);

        boolean result = load();
        logger.info("Load config [{}] ... [{}]", configPath, StringUtil.getOkFail(result));

        if (result == true) {
            loadConfig();
        }
    }

    @Override
    public String getStrValue(String session, String key, String defaultValue) {
        String value = super.getStrValue(session, key, defaultValue);

        logger.info("\tConfig key [{}] value [{}]", key, value);
        return value;
    }

    private void loadConfig() {
        try {
            mentVolume = getFloatValue("VOLUME", "MENT_VOLUME", DEFAULT_VOLUME);
            bgmVolume = getFloatValue("VOLUME", "BGM_VOLUME", DEFAULT_VOLUME);

            promptDir = getStrValue("GENERAL", "PROMPT_DIR", null);
            if (promptDir == null) {
                String path = (this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
                promptDir = path.substring(0, path.lastIndexOf('/'));
            }

            if (promptDir.charAt(promptDir.length() - 1) != '/') {
                promptDir = promptDir + "/";
            }

            String prompt;
            waitingPrompts = new HashMap<>();

            prompt = getStrValue("RESOURCE", "WAITING_PROMPT_ALAW", null);
            if (prompt != null) {
                waitingPrompts.put(Vocoder.VOCODER_ALAW, !prompt.startsWith("/") ? promptDir + prompt : prompt);
            }
            prompt = getStrValue("RESOURCE", "WAITING_PROMPT_ULAW", null);
            if (prompt != null) {
                waitingPrompts.put(Vocoder.VOCODER_ULAW, !prompt.startsWith("/") ? promptDir + prompt : prompt);
            }
            prompt = getStrValue("RESOURCE", "WAITING_PROMPT_AMR_NB", null);
            if (prompt != null) {
                waitingPrompts.put(Vocoder.VOCODER_AMR_NB, !prompt.startsWith("/") ? promptDir + prompt : prompt);
            }
            prompt = getStrValue("RESOURCE", "WAITING_PROMPT_AMR_WB", null);
            if (prompt != null) {
                waitingPrompts.put(Vocoder.VOCODER_AMR_WB, !prompt.startsWith("/") ? promptDir + prompt : prompt);
            }

            for (Map.Entry<Integer, String> entry: waitingPrompts.entrySet()) {
                logger.debug("Waiting Prompt [{}] - [{}]", entry.getKey(), entry.getValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getMentVolume() {
        logger.debug("Ment volume [{}]", mentVolume);
        return mentVolume;
    }

    public float getBgmVolume() {
        logger.debug("BGM volume [{}]", bgmVolume);
        return bgmVolume;
    }

    public String getWaitingPrompt(int vocoder) {
        String prompt = null;
        if (waitingPrompts != null && waitingPrompts.containsKey(vocoder)) {
            prompt = waitingPrompts.get(vocoder);
        }

        return prompt;
    }

    public String getPromptDir() {
        return promptDir;
    }
}
