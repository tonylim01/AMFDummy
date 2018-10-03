/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqOutgoingMessageInterface.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler.base;

import java.lang.reflect.Type;

public interface RmqOutgoingMessageInterface {
    void setType(String type);

    void setTransactionId(String transactionId);

    void setSessionId(String sessionId);

    void setMessageFrom(String messageFrom);

    void setTrxType(int trxType);

    void setReasonCode(int reasonCode);

    void setReasonStr(String reasonStr);

    String getSessionId();

    void setBody(Object obj, Type objType);

    boolean sendTo(String target);
}
