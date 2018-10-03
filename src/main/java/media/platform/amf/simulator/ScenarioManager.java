package media.platform.amf.simulator;

import media.platform.amf.core.rabbitmq.transport.RmqSender;
import media.platform.amf.AppInstance;
import media.platform.amf.config.AmfConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;

public class ScenarioManager {
    private static final Logger logger = LoggerFactory.getLogger(ScenarioManager.class);

    private RmqSender rmqSender = null;

    public ScenarioManager() {
        AmfConfig config = new AmfConfig( 0, AppInstance.getInstance().getConfigFile());

        rmqSender = new RmqSender(config.getRmqHost(), config.getRmqUser(), config.getRmqPass(), config.getLocalName());
        rmqSender.connectClient();
    }

    public boolean startScenario(String filename) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line;
            StringBuilder sb = null;

            while ((line = in.readLine()) != null) {

                if (line.equals("SEND")) {
                    sb = new StringBuilder();
                }
                else if (line.equals("END")) {
                    if (sb != null) {
                        String msg = sb.toString();
                        sendMessage(msg);
                        sb = null;
                    }
                }
                else if (line.startsWith("SLEEP")) {
                    logger.debug("SLEEP line: {}", line);
                    sleepCommand(line);
                }
                else if (sb != null) {
                    sb.append(line);
                }
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        rmqSender.close();

        return false;
    }

    private void sendMessage(String msg) {
        logger.debug("Send message: {}", msg);
        rmqSender.send(msg);
    }

    private void sleepCommand(String line) {
        String [] args = line.split(" ");
        if (args.length >= 2) {
            int sleepValue = Integer.valueOf(args[1]);
            logger.info("Sleep value [{}]", sleepValue);

            try {
                Thread.sleep(sleepValue);
            } catch (Exception e) {}
        }
    }
}
