package media.platform.amf.redundant;

import media.platform.amf.AppInstance;
import media.platform.amf.common.JsonMessage;
import media.platform.amf.config.RedundantConfig;
import media.platform.amf.redundant.messages.RedundantInfoSimple;
import media.platform.amf.rtpcore.Process.UdpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class RedundantClient {

    private static final Logger logger = LoggerFactory.getLogger(RedundantClient.class);

    private static final byte STX = (byte)0x2;

    private UdpClient udpClient;
    private String ip;
    private int port;

    private static RedundantClient redundantClient = null;

    public static RedundantClient getInstance() {
        if (redundantClient == null) {
            RedundantConfig config = AppInstance.getInstance().getConfig().getRedundantConfig();
            redundantClient = new RedundantClient(config.getRemoteIp(), config.getRemotePort());
        }
        return redundantClient;
    }

    public RedundantClient(String ip, int port) {
        this.ip = ip;
        this.port = port;

        initClient();
    }

    private void initClient() {
        try {
            InetAddress addr = InetAddress.getByName(ip);
            udpClient = new UdpClient(addr, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean sendMessage(byte[] msg) {
        if (msg == null) {
            return false;
        }

        boolean result = false;

        boolean isRepeat;
        int repeatCount = 0;

        do {
            isRepeat = false;

            if (udpClient == null) {
                initClient();
            }

            if (udpClient != null) {
                try {
                    udpClient.send(msg);
                    result = true;

                } catch (Exception e) {
                    e.printStackTrace();

                    udpClient.close();
                    udpClient = null;

                    if (repeatCount < 1) {
                        isRepeat = true;
                        repeatCount++;
                    }
                }

            }
        } while (isRepeat);

        return result;
    }

    public boolean sendMessage(int msgType, String body) {

        int bufSize = body.length() + 4;

        ByteBuffer buf = ByteBuffer.allocate(bufSize);
        buf.put(STX);
        buf.put((byte)msgType);
        buf.putShort((short)body.length());
        buf.put(body.getBytes());

        byte[] msg = new byte[buf.position()];

        buf.rewind();
        buf.get(msg);

        boolean result = sendMessage(msg);
        if (msgType != RedundantMessage.RMT_SN_UPDATE_JITTER_SENDER_REQ) {
            logger.debug("-> Redundant: type [{}] size [{}] result [{}]", msgType, body.length(), result);
        }

        buf.clear();
        buf = null;

        return result;
    }

    public boolean sendMessageSimple(int msgType, String sessionId) {

        RedundantInfoSimple ris = new RedundantInfoSimple(sessionId);
        String json = new JsonMessage(RedundantInfoSimple.class).build(ris);

        return sendMessage(msgType, json);
    }


}
