/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file DefaultConfig.java
 * @author Tony Lim
 *
 */

package media.platform.amf.core.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DefaultConfig {

    private static final Logger logger = LoggerFactory.getLogger( DefaultConfig.class);

    private static final long CONFIG_RELOAD_DELAY_MILLI = 1000L;

    private ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> builder;
    private PeriodicReloadingTrigger trigger;
    private Configuration config;
    private String fileName;

    private ConfigChangedListener configChangedListener = null;

    public DefaultConfig(String fileName) {
        this.fileName = fileName;
    }

    protected boolean load() {

        if (fileName == null) {
            return false;
        }

        Parameters params = new Parameters();
        File file;

        if (!fileName.startsWith("/")) {
            file = new File("src/main/resources/" + fileName);

        }
        else {
            file = new File(fileName);
        }

        builder = new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(INIConfiguration.class)
                .configure(params.fileBased()
                        .setFile(file)
                        .setReloadingRefreshDelay(0l));

        try {
            config = builder.getConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
        }

        trigger = new PeriodicReloadingTrigger(builder.getReloadingController(), null, 1, TimeUnit.SECONDS);
        trigger.start();

        builder.addEventListener(ConfigurationBuilderEvent.RESET, new CustomConfigurationEvent());

        logger.debug( "Loading Config :" + config.toString());

        return true;
    }

    public void setConfigChangedListener(ConfigChangedListener listener) {
        configChangedListener = listener;
    }

    private class CustomConfigurationEvent implements EventListener<Event> {

        @Override
        public void onEvent(Event event) {
            logger.warn("onEvent");
            try {
                Configuration newConfig = builder.getConfiguration();

                ConfigurationComparator comparator = new StrictConfigurationComparator();
                if (!comparator.compare(config, newConfig)) {
                    diffConfig(config, newConfig);
                }

                newConfig.clear();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void diffConfig(Configuration config1, Configuration config2) {
        boolean changed = false;
        for (Iterator<String> keys = config1.getKeys(); keys.hasNext(); ) {
            String key = keys.next();
            Object v1 = config1.getProperty(key);
            Object v2 = config2.getProperty(key);

            if (!Objects.equals(v1, v2)) {
                config.setProperty(key, v2);
                if (!changed) {
                    changed = true;
                }
            }
        }

        for (Iterator<String> keys = config2.getKeys(); keys.hasNext(); ) {
            String key = keys.next();
            Object v2 = config2.getProperty(key);

            if (!config1.containsKey(key)) {
                config.setProperty(key, v2);
                if (!changed) {
                    changed = true;
                }
            }
        }
        if (changed && configChangedListener != null) {
           configChangedListener.configChanged(true);
        }
    }

    public void close() {
        trigger.start();

        config.clear();
        config = null;
    }

    public String getStrValue(String section, String key, String defaultValue) {

        String mkey = section + "." + key;

        if (section == null) {
            return defaultValue;
        }

        return config.getString(mkey, defaultValue);
    }

    public int getIntValue(String section, String key, int defaultValue) {

        String mkey = section + "." + key;

        if (section == null) {
            return defaultValue;
        }

        return config.getInt(mkey, defaultValue);
    }

    public float getFloatValue(String section, String key, float defaultValue) {

        String mkey = section + "." + key;

        if (section == null) {
            return defaultValue;
        }

        return config.getFloat(mkey, defaultValue);
    }

    public boolean getBooleanValue(String section, String key, boolean defaultValue) {

        String mkey = section + "." + key;

        if (section == null) {
            return defaultValue;
        }

        return config.getBoolean(mkey, defaultValue);
    }
}
