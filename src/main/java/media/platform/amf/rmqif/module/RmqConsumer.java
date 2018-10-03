/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqConsumer.java
 * @author Tony Lim
 *
 */


package media.platform.amf.rmqif.module;

import media.platform.amf.rmqif.handler.*;
import media.platform.amf.rmqif.types.RmqMessage;
import media.platform.amf.rmqif.types.RmqMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

public class RmqConsumer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RmqConsumer.class);

    private BlockingQueue<String> queue;
    private boolean isQuit = false;

    public RmqConsumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        logger.debug("RmqConsumer start");

        while (!isQuit) {
            try {
                String msg = queue.take();
                logger.debug("RmqConsumer msg={}", msg);

                parseRmqMesage(msg);

            } catch (Exception e) {
                logger.warn("Exception [{}] [{}]", e.getClass(), e.getMessage());
                if (e.getClass() == InterruptedException.class || e.getClass() == SocketException.class) {
                    isQuit = true;
                }
            }
        }

        logger.debug("RmqConsumer end");
    }

    private void parseRmqMesage(String json) {

        RmqMessage msg = null;

        try {
            msg = RmqParser.parse(json);

            if (msg.getHeader() != null) {
                logger.debug("Received message: header {}", msg.getHeader().toString());
            }

            if (msg.getBody() != null) {
                logger.debug("Received message: body {}", msg.getBody().toString());
            }

            logger.info("[{}] <- ({}) {}", msg.getSessionId(), msg.getHeader().getMsgFrom(),
                    RmqMessageType.getMessageTypeStr(msg.getMessageType()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (msg == null) {
            return;
        }

        switch (msg.getMessageType()) {
            case RmqMessageType.RMQ_MSG_TYPE_INBOUND_SET_OFFER_REQ:
                RmqProcInboundSetOfferReq inboudSetOfferReq = new RmqProcInboundSetOfferReq();
                inboudSetOfferReq.handle(msg);
                break;
            case RmqMessageType.RMQ_MSG_TYPE_INBOUND_SET_OFFER_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_INBOUND_GET_ANSWER_REQ:
                RmqProcInboundGetAnswerReq inboundGetAnswerReq= new RmqProcInboundGetAnswerReq();
                inboundGetAnswerReq.handle(msg);
                break;
            case RmqMessageType.RMQ_MSG_TYPE_INBOUND_GET_ANSWER_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_OUTBOUND_SET_OFFER_REQ:
                RmqProcOutboundSetOfferReq outboudSetOfferReq = new RmqProcOutboundSetOfferReq();
                outboudSetOfferReq.handle(msg);
                break;
            case RmqMessageType.RMQ_MSG_TYPE_OUTBOUND_SET_OFFER_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_OUTBOUND_GET_ANSWER_REQ:
                RmqProcOutboundGetAnswerReq outboundGetAnswerReq= new RmqProcOutboundGetAnswerReq();
                outboundGetAnswerReq.handle(msg);
                break;
            case RmqMessageType.RMQ_MSG_TYPE_OUTBOUND_GET_ANSWER_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_HANGUP_REQ:
                RmqProcIncomingHangupReq hangupReq= new RmqProcIncomingHangupReq();
                hangupReq.handle(msg);
                break;
            case RmqMessageType.RMQ_MSG_TYPE_HANGUP_RES:
                RmqProcOutgoingHangupRes hangupRes = new RmqProcOutgoingHangupRes();
                hangupRes.handle(msg);
                break;
            case RmqMessageType.RMQ_MSG_TYPE_NEGO_DONE_REQ:
                RmqProcNegoDoneReq negoDoneReq= new RmqProcNegoDoneReq();
                negoDoneReq.handle(msg);
                break;
            case RmqMessageType.RMQ_MSG_TYPE_NEGO_DONE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_COMMAND_REQ:
                RmqProcIncomingCommandReq commandStartReq = new RmqProcIncomingCommandReq();
                commandStartReq.handle(msg);
                break;
            case RmqMessageType.RMQ_MSG_TYPE_COMMAND_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_LONGCALL_CHECK_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_LONGCALL_CHECK_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_PROMPT_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_PROMPT_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_PROMPT_ACK:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_COLLECT_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_COLLECT_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_COLLECT_ACK:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_RECORD_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_RECORD_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_RECORD_ACK:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STOP_PLAY_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STOP_PLAY_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STOP_RECORD_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STOP_RECORD_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_CONTROL_FILE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_CONTROL_FILE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_ASR_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_ASR_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_ASR_ACK:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_SERVICE_START_REQ:
                RmqProcServiceStartReq startServiceReq = new RmqProcServiceStartReq();
                startServiceReq.handle(msg);
                break;
            case RmqMessageType.RMQ_MSG_TYPE_SERVICE_START_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_COMMAND_STOP_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_COMMAND_STOP_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_COMMAND_DONE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_COMMAND_DONE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_COMMAND_END_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_COMMAND_END_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_CREATE_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_CREATE_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_DELETE_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_DELETE_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_JOIN_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_JOIN_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_WITHDRAW_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_WITHDRAW_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_UPDATE_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_UPDATE_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STARTRECORD_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STARTRECORD_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STOPRECORD_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_STOPRECORD_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_RECORD_CONFERENCE_RPT:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_RECORD_CONFERENCE_ACK:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_CHANGE_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_CHANGE_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_DTMF_CONFERENCE_RPT:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_DTMF_CONFERENCE_ACK:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_CONFERENCE_REQ:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_CONFERENCE_RES:
                break;
            case RmqMessageType.RMQ_MSG_TYPE_PLAY_CONFERENCE_ACK:
                break;
        }
    }

}
