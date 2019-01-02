package media.platform.amf.engine.handler;

public class DefaultEngineMessageHandler {
    protected boolean compareString(String src, String dst) {
        return (src != null && dst != null && src.equals(dst)) ? true : false;
    }
}
