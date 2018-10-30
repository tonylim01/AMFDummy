package media.platform.amf.engine.messages.common;

public class FileInfos {
    private Boolean container;
    public String[] list;

    public Boolean getContainer() {
        return container;
    }

    public void setContainer(Boolean container) {
        this.container = container;
    }

    public String[] getList() {
        return list;
    }

    public void setList(String[] list) {
        this.list = list;
    }
}
