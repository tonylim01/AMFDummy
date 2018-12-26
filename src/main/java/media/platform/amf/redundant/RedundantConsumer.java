package media.platform.amf.redundant;

import media.platform.amf.redundant.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;

public class RedundantConsumer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RedundantConsumer.class);

    private BlockingQueue<byte[]> queue;
    private boolean isQuit = false;

    public RedundantConsumer(BlockingQueue<byte[]> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        logger.debug("RedundantConsumer startScheduler");

        while (!isQuit) {
            try {
                byte[] buf = queue.take();

                handleMessage(buf);

            } catch (Exception e) {
                logger.warn("Exception [{}] [{}]", e.getClass(), e.getMessage());
                if (e.getClass() == InterruptedException.class || e.getClass() == SocketException.class) {
                    isQuit = true;
                }
            }
        }

        logger.debug("RedundantConsumer end");
    }

    private void handleMessage(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }

        ByteBuffer buf = ByteBuffer.wrap(data);
        byte stx = buf.get();
        byte msgType = buf.get();
        short length = buf.getShort();
        String body = null;

        if (length > 0) {
            byte[] bodyBuf = new byte[length];
            buf.get(bodyBuf, 0, length);
            body = new String(bodyBuf, Charset.defaultCharset());
        }

        if (msgType != RedundantMessage.RMT_SN_UPDATE_JITTER_SENDER_REQ) {
            logger.debug("<- Redundant: stx [{}] types [{}] length [{}] body [{}]", stx, msgType, length, body);
        }

        switch ((int)msgType) {
            case RedundantMessage.RMT_SN_INBOUND_SET_OFFER_REQ:
                RedunProcInboundSetOfferReq inboundSetOfferReq = new RedunProcInboundSetOfferReq();
                inboundSetOfferReq.handle(body);
                break;
            case RedundantMessage.RMT_SN_INBOUND_GET_ANSER_REQ:
                RedunProcInboundGetAnswerReq inboundGetAnswerReq = new RedunProcInboundGetAnswerReq();
                inboundGetAnswerReq.handle(body);
                break;
            case RedundantMessage.RMT_SN_OUTBOUND_SET_OFFER_REQ:
                RedunProcOutboundSetOfferReq outboundSetOfferReq = new RedunProcOutboundSetOfferReq();
                outboundSetOfferReq.handle(body);
                break;
            case RedundantMessage.RMT_SN_OUTBOUND_GET_ANSER_REQ:
                RedunProcOutboundGetAnswerReq outboundGetAnswerReq = new RedunProcOutboundGetAnswerReq();
                outboundGetAnswerReq.handle(body);
                break;
            case RedundantMessage.RMT_SN_NEGO_DONE_REQ:
                RedunProcNegoDoneReq negoDoneReq = new RedunProcNegoDoneReq();
                negoDoneReq.handle(body);
                break;
            case RedundantMessage.RMT_SN_HANGUP_REQ:
                RedunProcHangupReq hangupReq = new RedunProcHangupReq();
                hangupReq.handle(body);
                break;
            case RedundantMessage.RMT_SN_UPDATE_JITTER_SENDER_REQ:
                //RedunProcUpdateJitterSenderReq updateJitterSenderReq = new RedunProcUpdateJitterSenderReq();
                //updateJitterSenderReq.handle(body);
                break;
            default:
                break;
        }
    }
}
