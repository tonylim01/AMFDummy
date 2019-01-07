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
        String[] ret = null;
        if (list != null) {
            ret = new String[list.length];
            System.arraycopy(list, 0, ret, 0, list.length);
        }
        return ret;
    }

    public void setList(String[] list) {
        if (list != null) {
            this.list = new String[list.length];
            System.arraycopy(list, 0, this.list, 0, list.length);
        }
    }
}
