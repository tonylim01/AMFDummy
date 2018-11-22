package media.platform.amf.rtpcore.core.component;

import media.platform.amf.rtpcore.core.spi.Component;
import media.platform.amf.rtpcore.core.spi.memory.Frame;

public abstract class BaseComponent implements Component {

	private static final long serialVersionUID = 7891529327834578393L;

	//unique identifier of the component
    private final String id;
    
    //the name of the component. 
    //name of the component might be same across many components of same types
    private final String name;
    
    /**
     * Creates new instance of the component.
     * 
     * @param name the name of component.
     */
    public BaseComponent(String name) {
        this.name = name;
        this.id = Long.toHexString(System.nanoTime());
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return name;
    }

    public abstract void perform(Frame frame);
}
