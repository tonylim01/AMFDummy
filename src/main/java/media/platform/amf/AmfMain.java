package media.platform.amf;

import media.platform.amf.common.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.service.ServiceManager;

public class AmfMain
{
    private static final Logger logger = LoggerFactory.getLogger( AmfMain.class);

    public static void main( String[] args )
    {
        int instanceId = 0;
        String configPath = null;

        if (args != null && args.length > 0) {
            if (StringUtil.isNumeric( args[0])) {
                instanceId = Integer.valueOf(args[0]);
            }
        }

        logger.info("MRUD [{}] start", instanceId);

        AppInstance.getInstance().setInstanceId(instanceId);

        ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.loop();
    }
}
