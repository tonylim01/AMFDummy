package media.platform.amf.rtpcore.core.spi.format;

import media.platform.amf.rtpcore.core.spi.utils.Text;

public class EncodingName extends Text implements Cloneable {
    public EncodingName() {
        super();
    }

    public EncodingName(Text text) {
    	byte[] newArray=new byte[text.length()];
    	this.strain(newArray,0, this.length());
    	text.duplicate(this);
    }

    public EncodingName(String s) {
        super(s);
    }
    
    @Override
    protected EncodingName clone() {
    	byte[] newArray=new byte[this.length()];
        Text t = new Text();
        t.strain(newArray,0, this.length());
        this.duplicate(t);
        return new EncodingName(t);
    }
}
