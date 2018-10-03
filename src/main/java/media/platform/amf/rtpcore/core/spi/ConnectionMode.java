package media.platform.amf.rtpcore.core.spi;

public enum ConnectionMode {
    
    INACTIVE("inactive"), 
    SEND_ONLY("sendonly"),
    RECV_ONLY("recvonly"),
    SEND_RECV("sendrecv"),
    CONFERENCE("confrnce"),
    NETWORK_LOOPBACK("netwloop"),
    LOOPBACK("loopback"),
    CONTINUITY_TEST("conttest"),
    NETWORK_CONTINUITY_TEST("netwtest");
    
    private final String description;
    
    private ConnectionMode(String description) {
        this.description = description;
    }
    
    public String description() {
        return this.description;
    }
    
    public static final ConnectionMode fromDescription(String description) {
        if(description != null && !description.isEmpty()) {
            for (ConnectionMode mode : values()) {
                if(mode.description.equalsIgnoreCase(description)) {
                    return mode;
                }
            }
        }
        throw new IllegalArgumentException("Unknown connection mode: " + description);
    }
}
