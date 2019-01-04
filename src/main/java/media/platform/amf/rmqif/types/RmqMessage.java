/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqMessage.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.types;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class RmqMessage {

    private RmqHeader header;
    private JsonElement body = null;

    public RmqMessage(String type, String sessionId, String transactionId, String msgFrom, boolean trxType, int reasonCode, String reasonStr) {
        this.header = new RmqHeader(type, sessionId, transactionId, msgFrom, trxType, reasonCode, reasonStr);
    }

    public RmqMessage(RmqHeader header) {
        this.header = new RmqHeader(header.getType(),
                header.getSessionId(),
                header.getTransactionId(),
                header.getMsgFrom(),
                header.getTrxType(),
                header.getReasonCode(),
                header.getReason());
    }

    public RmqHeader getHeader() {
        return header;
    }

    public void setHeader(RmqHeader header) {
        this.header = header;
    }

    public JsonElement getBody() {
        return body;
    }

    public void setBody(JsonElement body) {
        this.body = body;
    }

    public void setBody(String body) {
        Gson gson = new Gson();
        this.body = gson.toJsonTree(body);
    }

    public int getMessageType() {
        return RmqMessageType.getMessageType(header.getType());
    }

    public String getSessionId() {
        return (header != null) ? header.getSessionId() : null;
    }
}
