package media.platform.amf.rtpcore.core.network.deprecated;

import media.platform.amf.rtpcore.core.IPAddressUtil;

public class IPAddressCompare
{

	/**
	*  Checks whether ipAddress is in IPV4 network with specified subnet 
	*/
    public static boolean isInRangeV4(byte[] network,byte[] subnet,byte[] ipAddress)
    {
    	if(network.length!=4 || subnet.length!=4 || ipAddress.length!=4)
    		return false;    	
    	
    	return compareByteValues(network,subnet,ipAddress);
    }
    
    /**
	*  Checks whether ipAddress is in IPV6 network with specified subnet 
	*/
    public static boolean isInRangeV6(byte[] network,byte[] subnet,byte[] ipAddress)
    {
    	if(network.length!=16 || subnet.length!=16 || ipAddress.length!=16)
    		return false;
    	
    	return compareByteValues(network,subnet,ipAddress);
    }
    
    /**
	*  Checks whether ipAddress is in network with specified subnet by comparing byte logical end values 
	*/
    private static boolean compareByteValues(byte[] network,byte[] subnet,byte[] ipAddress)
    {    	
    	for(int i=0;i<network.length;i++)
    		if((network[i] & subnet[i]) != (ipAddress[i] & subnet[i]))
    			return false;
    	
    	return true;
    }

    /**
	*  Gets IP address types
	*/
    public static IPAddressType getAddressType(String ipAddress)
    {
    	if(IPAddressUtil.isIPv4LiteralAddress(ipAddress))
    		return IPAddressType.IPV4;
    	
    	if(IPAddressUtil.isIPv6LiteralAddress(ipAddress))
    		return IPAddressType.IPV6;
    	
    	return IPAddressType.INVALID;
    }
    
    /**
	*  Converts String to byte array for IPV4 , returns null if ip address is not legal  
	*/
    public static byte[] addressToByteArrayV4(String ipAddress)
    {
    	return IPAddressUtil.textToNumericFormatV4(ipAddress);
    }
    
    /**
	*  Converts String to byte array for IPV6 , returns null if ip address is not legal 
	*/
    public static byte[] addressToByteArrayV6(String ipAddress)
    {
    	return IPAddressUtil.textToNumericFormatV6(ipAddress);
    }
}
