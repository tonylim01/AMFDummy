package media.platform.amf.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AppId {
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
        if (key == null || value == null) {
            return;
        }

        idQueue.put(key, value);
    }

    public String get(String key) {
        return idQueue.containsKey(key) ? idQueue.get(key) : null;
    }
}
