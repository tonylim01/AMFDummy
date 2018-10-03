package media.platform.amf.session;

/**
 * OOS      - Unavailable
 * IDLE     - Initial
 * OFFER    - Received OfferReq
 * ANSWER   - Received AnswerReq
 * PREPARE  - Received NegoDoneReq
 * READY    - After all channel opened
 * START    - Received ServiceStartReq
 * PLAY     - Received CommandReq(PlayReq)
 * UPDATE   - Update energy status
 * RELEASE  - Sent HangupReq
 */
public enum SessionState {

    OOS, IDLE, OFFER, ANSWER, PREPARE, READY, START, PLAY_START, PLAY_STOP, UPDATE, RELEASE
}
