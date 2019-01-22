package media.platform.amf;

import media.platform.amf.common.StringUtil;
import media.platform.amf.config.AmfConfig;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.service.ServiceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AmfMain
{
    private static final Logger logger = LoggerFactory.getLogger( AmfMain.class);

    private static final String DEFAULT_LOG_FILENAME = "amf%d.log";

    public static void main( String[] args )
    {
        int instanceId = 0;
        String configPath = null;

        if (args != null && args.length > 0) {
            if (StringUtil.isNumeric( args[0])) {
                instanceId = Integer.parseInt(args[0]);
            }

            if (args.length > 1) {
                configPath = args[1];
                AppInstance.getInstance().setConfigFile(configPath);
            }
        }

        String logFilename = String.format(DEFAULT_LOG_FILENAME, instanceId);
        System.setProperty("amf.log.filename", logFilename);

        logger.info("MRUD [{}] startScheduler", instanceId);

        AppInstance.getInstance().setInstanceId(instanceId);

        ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.loop();

        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();

        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory is bytes: " + memory);
    }

    private void updateLog4jConfiguration(String logFile) {
        Properties props = new Properties();
        try {
            InputStream configStream = getClass().getResourceAsStream( "/log4j.properties");
            props.load(configStream);
            configStream.close();
        } catch (IOException e) {
            System.out.println("Error: Cannot laod configuration file ");
        }

        props.setProperty("log4j.appender.FILE.file", logFile);
        PropertyConfigurator.configure(props);
    }
}
