package media.platform.amf.rtpcore.core.network.deprecated;

public enum IPAddressType {

    IPV4(0),IPV6(1),INVALID(2);
    
    private int value;
    
    IPAddressType(int value)
    {
    	this.value=value;
    }
    
    public int getValue()
    {
    	return value;
    }
}
