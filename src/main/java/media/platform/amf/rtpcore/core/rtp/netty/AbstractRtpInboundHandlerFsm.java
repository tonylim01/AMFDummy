package media.platform.amf.rtpcore.core.rtp.netty;

import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

public class AbstractRtpInboundHandlerFsm extends AbstractStateMachine<RtpInboundHandlerFsm, RtpInboundHandlerState, RtpInboundHandlerEvent, RtpInboundHandlerTransactionContext> implements RtpInboundHandlerFsm {

    @Override
    public void enterActivated(RtpInboundHandlerState from, RtpInboundHandlerState to, RtpInboundHandlerEvent event,
            RtpInboundHandlerTransactionContext context) {
    }

    @Override
    public void exitActivated(RtpInboundHandlerState from, RtpInboundHandlerState to, RtpInboundHandlerEvent event,
            RtpInboundHandlerTransactionContext context) {
    }

    @Override
    public void enterDeactivated(RtpInboundHandlerState from, RtpInboundHandlerState to, RtpInboundHandlerEvent event,
            RtpInboundHandlerTransactionContext context) {
    }

    @Override
    public void exitDeactivated(RtpInboundHandlerState from, RtpInboundHandlerState to, RtpInboundHandlerEvent event,
            RtpInboundHandlerTransactionContext context) {
    }

    @Override
    public void onPacketReceived(RtpInboundHandlerState from, RtpInboundHandlerState to, RtpInboundHandlerEvent event,
            RtpInboundHandlerTransactionContext context) {
    }

}
