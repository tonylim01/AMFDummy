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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DefaultConfig {

    private static final Logger logger = LoggerFactory.getLogger( DefaultConfig.class);

    private Config conf;
    private String fileName;

    public DefaultConfig(String fileName) {
        this.fileName = fileName;
    }

    protected boolean load() {

        if (fileName == null) {
            return false;
        }

        //conf = ConfigFactory.load();
        if (!fileName.startsWith("/")) {
            conf = ConfigFactory.parseResources(fileName);
        }
        else {
            conf = ConfigFactory.parseFile(new File(fileName));
        }

        logger.debug( "Loading Config :" + conf.toString());

        return true;
    }

    public void close() {
        conf = null;
    }

    public String getStrValue(String section, String key, String defaultValue) {
        String mkey = section+"."+key;
        String value = null;

        if (section == null) {
            return defaultValue;
        }
        try
        {
            value = conf.getString( mkey);
        }
        catch (Exception e) {
            value = defaultValue;
        }


//        value.getString(rvalue);

        return value;
    }

    public int getIntValue(String section, String key, int defaultValue) {

        String mkey = section+"."+key;
        int rvalue = 0;

        if (section == null) {
            return defaultValue;
        }

        try
        {
            rvalue = conf.getInt( mkey);
        }
        catch (Exception e) {
            rvalue = defaultValue;
        }

        return rvalue;
    }

    public float getFloatValue(String section, String key, float defaultValue) {

        float result;
        String mkey = section+"."+key;
        String value = null;

        if (section == null) {
            return defaultValue;
        }
        try
        {
            value = conf.getString( mkey);
            result = Float.valueOf(value);
        }
        catch (Exception e) {
            result = defaultValue;
        }

        return result;
    }

    public boolean getBooleanValue(String section, String key, boolean defaultValue) {

        boolean result;
        String mkey = section+"."+key;
        String value = null;

        if (section == null) {
            return defaultValue;
        }
        try
        {
            value = conf.getString( mkey);
            result = Boolean.valueOf(value);
        }
        catch (Exception e) {
            result = defaultValue;
        }

        return result;
    }
}
