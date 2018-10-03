package media.platform.amf.rtpcore.core.rtp.netty;

import org.squirrelframework.foundation.fsm.StateMachine;

public interface RtpInboundHandlerFsm extends StateMachine<RtpInboundHandlerFsm, RtpInboundHandlerState, RtpInboundHandlerEvent, RtpInboundHandlerTransactionContext> {

    void enterActivated(RtpInboundHandlerState from, RtpInboundHandlerState to, RtpInboundHandlerEvent event, RtpInboundHandlerTransactionContext context);

    void exitActivated(RtpInboundHandlerState from, RtpInboundHandlerState to, RtpInboundHandlerEvent event, RtpInboundHandlerTransactionContext context);

    void enterDeactivated(RtpInboundHandlerState from, RtpInboundHandlerState to, RtpInboundHandlerEvent event, RtpInboundHandlerTransactionContext context);

    void exitDeactivated(RtpInboundHandlerState from, RtpInboundHandlerState to, RtpInboundHandlerEvent event, RtpInboundHandlerTransactionContext context);

    void onPacketReceived(RtpInboundHandlerState from, RtpInboundHandlerState to, RtpInboundHandlerEvent event, RtpInboundHandlerTransactionContext context);

}
