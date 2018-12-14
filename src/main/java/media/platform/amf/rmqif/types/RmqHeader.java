/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqHeader.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.types;

public class RmqHeader {

    private String type;
    private String callId;
    private String sessionId;
    private String transactionId;
    private String msgFrom;
    private int trxType;
    private int reasonCode;
    private String reason;

    public RmqHeader() {

    }

    public RmqHeader(String type, String sessionId, String transactionId, String msgFrom, int trxType, int reasonCode, String reason) {
        this.type = type;
        this.sessionId = sessionId;
        this.transactionId = transactionId;
        this.msgFrom = msgFrom;
        this.trxType = trxType;
        this.reasonCode = reasonCode;
        this.reason = reason;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSessionId() {
        if (sessionId == null && callId != null) {
            sessionId = callId;
        }
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public void setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
    }

    public int getTrxType() {
        return trxType;
    }

    public void setTrxType(int trxType) {
        this.trxType = trxType;
    }

    public int getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(int reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    @Override
    public String toString() {
        return "RmqHeader{" +
                "types='" + type + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", transactionId=" + transactionId +
                ", msgFrom='" + msgFrom + '\'' +
                ", trxType=" + trxType +
                ", reasonCode=" + reasonCode +
                ", reason='" + reason + '\'' +
                '}';
    }
}
