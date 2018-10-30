package media.platform.amf.engine.messages;

public class FileRecordRpt {

    private ReportHeader report;

    public ReportHeader getReport() {
        return report;
    }

    public void setReport(String type, String cmd, String appId, String event, int duration, String value) {
        this.report = new ReportHeader(type, cmd, appId, event, duration, value);
    }

    public void setReport(ReportHeader report) {
        this.report = report;
    }
}
