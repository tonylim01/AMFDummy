package media.platform.amf.rtpcore.core.rtp.netty;

import org.squirrelframework.foundation.fsm.StateMachineBuilder;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;

public class RtpInboundHandlerFsmBuilder {

    public static final RtpInboundHandlerFsmBuilder INSTANCE = new RtpInboundHandlerFsmBuilder();

    private final StateMachineBuilder<RtpInboundHandlerFsm, RtpInboundHandlerState, RtpInboundHandlerEvent, RtpInboundHandlerTransactionContext> builder;

    private RtpInboundHandlerFsmBuilder() {
        this.builder = StateMachineBuilderFactory.<RtpInboundHandlerFsm, RtpInboundHandlerState, RtpInboundHandlerEvent, RtpInboundHandlerTransactionContext> create( RtpInboundHandlerFsmImpl.class, RtpInboundHandlerState.class, RtpInboundHandlerEvent.class, RtpInboundHandlerTransactionContext.class, RtpInboundHandlerGlobalContext.class);

        this.builder.onEntry(RtpInboundHandlerState.ACTIVATED).callMethod("enterActivated");
        this.builder.internalTransition().within(RtpInboundHandlerState.ACTIVATED).on(RtpInboundHandlerEvent.PACKET_RECEIVED).callMethod("onPacketReceived");
        this.builder.externalTransition().from(RtpInboundHandlerState.ACTIVATED).toFinal(RtpInboundHandlerState.DEACTIVATED).on(RtpInboundHandlerEvent.DEACTIVATE);
        this.builder.onExit(RtpInboundHandlerState.ACTIVATED).callMethod("exitActivated");

        this.builder.onEntry(RtpInboundHandlerState.DEACTIVATED).callMethod("enterDeactivated");
        this.builder.onExit(RtpInboundHandlerState.DEACTIVATED).callMethod("exitDeactivated");
    }

    public RtpInboundHandlerFsm build(RtpInboundHandlerGlobalContext context) {
        return this.builder.newStateMachine(RtpInboundHandlerState.ACTIVATED, context);
    }

}
