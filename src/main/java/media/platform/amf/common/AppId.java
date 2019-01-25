package media.platform.amf.common;

import media.platform.amf.AppInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AppId {
    private static final Logger logger = LoggerFactory.getLogger(AppId.class);

    private Map<String, String> idQueue;

    private static AppId appId = null;

    public static AppId getInstance() {
        if (appId == null) {
            appId = new AppId();
        }
        return appId;
    }

    public AppId() {
        idQueue = new HashMap<>();
    }

    public static String newId() {
        return UUID.randomUUID().toString();
    }


    public void push(String key, String value) {
        logger.debug("AppId push [{}] - [{}]", key, value);

        if (key == null || value == null) {
            return;
        }

        idQueue.put(key, value);
    }

    public String get(String key) {
        return idQueue.containsKey(key) ? idQueue.get(key) : null;
    }

    public void remove(String key) {
        if (idQueue.containsKey(key)) {
            logger.debug("AppId remove [{}]", key);
            idQueue.remove(key);
        }
        else {
            logger.debug("AppId remove [{}] but not found", key);
        }
    }
}
