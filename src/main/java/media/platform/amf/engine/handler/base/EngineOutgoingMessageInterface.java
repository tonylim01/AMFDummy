package media.platform.amf.engine.handler.base;

import java.lang.reflect.Type;

public interface EngineOutgoingMessageInterface {

    void setBody(Object obj, Type objType);
    boolean sendTo();
}
