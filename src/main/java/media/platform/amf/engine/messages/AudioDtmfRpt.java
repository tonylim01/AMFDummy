package media.platform.amf.engine.messages;

public class AudioDtmfRpt {

    private ReportHeader report;

    public ReportHeader getReport() {
        return report;
    }

    public void setReport(String type, String cmd, String appId, String event, String value) {
        this.report = new ReportHeader(type, cmd, appId, event, 0, value);
    }

    public void setReport(ReportHeader report) {
        this.report = report;
    }
}
