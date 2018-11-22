package media.platform.amf.rtpcore.core.spi;

import java.io.Serializable;

public interface Component extends Serializable {

    /**
     * Gets the unique identifier of this component.
     * 
     * @return
     */
    public String getId();

    /**
     * Gets the name of the component. The component of same types can share same name.
     * 
     * @return name of this component;
     */
    public String getName();

    /**
     * Resets component to its original state. This methods cleans transmission statistics and any assigned formats
     */
    public void reset();

    /**
     * Activates component
     * 
     */
    public void activate();

    /**
     * Deactivates component
     * 
     */
    public void deactivate();

}
