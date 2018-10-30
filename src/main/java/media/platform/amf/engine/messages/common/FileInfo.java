package media.platform.amf.engine.messages.common;

public class FileInfo {
    private Boolean container;
    public String filename;

    public Boolean getContainer() {
        return container;
    }

    public void setContainer(Boolean container) {
        this.container = container;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
