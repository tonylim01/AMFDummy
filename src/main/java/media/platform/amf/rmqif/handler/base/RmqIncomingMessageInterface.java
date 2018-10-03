/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqIncomingMessageInterface.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.handler.base;

import media.platform.amf.rmqif.types.RmqMessage;

public interface RmqIncomingMessageInterface {

    boolean handle(RmqMessage msg);

    /**
     * Sends a response with reason = success
     * @param sessionId
     * @param transactionId
     */
    void sendResponse(String sessionId, String transactionId);

    /**
     * Sends a response to the given queue
     * @param sessionId
     * @param transactionId
     * @param queueName
     */
    void sendResponse(String sessionId, String transactionId, String queueName);

    /**
     * Sends a resopnse with a specified reasonCode and reasonStr
     * @param sessionId
     * @param transactionId
     * @param reasonCode
     * @param reasonStr
     */
    void sendResponse(String sessionId, String transactionId, int reasonCode, String reasonStr);

    /**
     * Sends a response to the given queue
     * @param sessionId
     * @param transactionId
     * @param queueName
     * @param reasonCode
     * @param reasonStr
     */
    void sendResponse(String sessionId, String transactionId, String queueName, int reasonCode, String reasonStr);
}
