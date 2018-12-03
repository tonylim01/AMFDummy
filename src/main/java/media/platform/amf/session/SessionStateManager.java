package media.platform.amf.session;

import media.platform.amf.session.StateHandler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.StateHandler.*;
import sun.nio.ch.ThreadPool;

import java.net.SocketException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(SessionStateManager.class);

    private static final int THREAD_POOL_SIZE = 600;
    private static final int QUEUE_SIZE = 128;

    private static SessionStateManager sessionStateManager = null;

    public static SessionStateManager getInstance() {
        if (sessionStateManager == null) {
            sessionStateManager = new SessionStateManager();
        }
        return sessionStateManager;
    }

    private ThreadPoolExecutor statePoolExcutor;
    private BlockingQueue<SessionStateMessage> stateQueue;
    private Thread stateMachineThread;

    public SessionStateManager() {
        statePoolExcutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        stateQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
        stateMachineThread = new Thread(new SessionStateMachine(stateQueue));
        stateMachineThread.start();

        logger.info("SessionStateManager started");
    }

    public void stop() {

        stateMachineThread.interrupt();
        stateMachineThread = null;
        statePoolExcutor.shutdown();
    }

    /**
     * Adds SessionState into the state queue without an argument
     * @param sessionId
     * @param state
     */
    public void setState(String sessionId, SessionState state) {
        setState(sessionId, state, null);
    }

    /**
     * Adds SessionState into the state queue with an argument
     * @param sessionId
     * @param state
     * @param data
     */
    public void setState(String sessionId, SessionState state, Object data) {
        if (sessionId == null) {
            return;
        }

        SessionStateMessage msg = new SessionStateMessage(sessionId, state, data);
        stateQueue.add(msg);
    }

    /**
     * Session state machine module
     */
    class SessionStateMachine implements Runnable {

        private BlockingQueue<SessionStateMessage> queue;
        private boolean isQuit = false;

        private Map<SessionState, StateFunction> stateFunctions() {
            return Collections.unmodifiableMap(Stream.of(
                    new AbstractMap.SimpleEntry<>(SessionState.IDLE, new IdleStateFunction()),
                    new AbstractMap.SimpleEntry<>(SessionState.OFFER, new OfferStateFunction()),
                    new AbstractMap.SimpleEntry<>(SessionState.ANSWER, new AnswerStateFunction()),
                    new AbstractMap.SimpleEntry<>(SessionState.PREPARE, new PrepareStateFunction()),
                    new AbstractMap.SimpleEntry<>(SessionState.START, new StartStateFunction()),
                    new AbstractMap.SimpleEntry<>(SessionState.READY, new ReadyStateFunction()),
                    new AbstractMap.SimpleEntry<>(SessionState.PLAY_START, new PlayStartStateFunction()),
                    new AbstractMap.SimpleEntry<>(SessionState.PLAY_STOP, new PlayStopStateFunction()),
                    new AbstractMap.SimpleEntry<>(SessionState.UPDATE, new UpdateStateFunction()),
                    new AbstractMap.SimpleEntry<>(SessionState.RELEASE, new ReleaseStateFunction())
            ).collect(Collectors.toMap((e) -> e.getKey(), (e) ->e.getValue())));
        }

        public SessionStateMachine(BlockingQueue<SessionStateMessage> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            logger.info("SessionStateMachine started");

            while(!isQuit) {
                try {
                    SessionStateMessage msg = queue.take();
                    handleMessage(msg);
                } catch (Exception e) {
                    logger.warn("Exception [{}] [{}]", e.getClass(), e.getMessage());
                    logger.error("Exception desc: {}", e);

                    if (e.getClass() == InterruptedException.class || e.getClass() == SocketException.class) {
                        isQuit = true;
                    }
                    else {
                        e.printStackTrace();
                    }
                }
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

            logger.warn("SessionStateMachine end");
        }

        private void handleMessage(SessionStateMessage msg) {
            if (msg == null) {
                return;
            }

            logger.debug("[{}] State message [{}]", msg.getSessionId(), msg.getState().name());

            SessionInfo sessionInfo = SessionManager.findSession(msg.getSessionId());
            if (sessionInfo == null) {
                return;
            }

            StateFunction stateFunction = stateFunctions().get(msg.getState());
            if (stateFunction != null) {
                statePoolExcutor.execute(new StateFunctionRunnable(stateFunction, sessionInfo, msg.getData()));
            }
        }
    }
}
