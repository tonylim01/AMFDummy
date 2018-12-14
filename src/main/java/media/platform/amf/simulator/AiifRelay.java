package media.platform.amf.simulator;

import media.platform.amf.core.codec.AMRSlience;
import media.platform.amf.AppInstance;
import media.platform.amf.common.ShellUtil;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.rmqif.module.RmqClient;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import media.platform.amf.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;
import media.platform.amf.session.SessionStateManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class AiifRelay {

    private static final Logger logger = LoggerFactory.getLogger(AiifRelay.class);

    private static final int RTP_HEADER_SIZE = 12;

    private boolean isQuit;

    private RmqClient rmqClient = null;
    private FileOutputStream fileStream = null;
    private RandomAccessFile inputPipeFile = null;
    private RandomAccessFile outputPipeFile = null;
    private boolean pipeOpened = false;
    private Thread ffmpegThread = null;
    private Thread rmqThread = null;
    private String inputPipeName;
    private String outputPipeName;

    private long audioDetectLevel = 0;
    private long silenceDetectLevel = 0;
    private long silenceDetectDuration = 0;
    private long energyDetectDuration = 0;

    private boolean isEnergyDetected = false;

    private String sessionId;
    private RoomInfo roomInfo;

    private boolean isAMR = false;

    public void start() {
        isQuit = false;

        AmfConfig config = AppInstance.getInstance().getConfig();

        audioDetectLevel = config.getAudioEnergyLevel();
        silenceDetectLevel = config.getSilenceEnergyLevel();
        silenceDetectDuration = config.getSilenceDetectDuration();
        energyDetectDuration = config.getEnergyDetectDuration();

        ffmpegThread = new Thread(new FfmpegRunnable());
        ffmpegThread.start();

        try {
            logger.debug("AiifRelay start {} codec {}", inputPipeName, inputCodec);
            inputPipeFile = new RandomAccessFile(inputPipeName, "rw");

            outputPipeFile = new RandomAccessFile(outputPipeName, "r");

            pipeOpened = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        rmqThread = new Thread(new RmqRelayRunnable());
        rmqThread.start();
    }

    public void stop() {
        logger.debug("stop ({})", inputPipeName);

        isQuit = true;

        if (pipeOpened) {
            pipeOpened = false;

            if (transcodingProcess != null) {
                ShellUtil.killShell( transcodingProcess);
            }

            if (inputPipeFile != null) {

                logger.info("Close input pipe ({})", inputPipeName);
                try {
                    inputPipeFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                logger.info("Close input pipe ({}) done", inputPipeName);

                deleteFile(inputPipeName);

            }
            if (outputPipeFile != null) {

                logger.info("Close output pipe ({})", outputPipeName);
                try {
                    outputPipeFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                logger.info("Close output pipe ({}) done", outputPipeName);

                deleteFile(outputPipeName);
            }

        }

        if (fileStream != null) {
            try {
                fileStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (rmqThread != null) {
            rmqThread.interrupt();
            rmqThread = null;
        }
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;

        if (sessionId != null) {
            SessionInfo sessionInfo = SessionManager.getInstance().getSession( sessionId);
            if (sessionInfo != null && sessionInfo.getConferenceId() != null) {
                roomInfo = RoomManager.getInstance().getRoomInfo( sessionInfo.getConferenceId());
            }
        }
    }

    public boolean send(byte[] buf, int size) {
        if (buf == null || buf.length == 0) {
            return false;
        }

        if (size > buf.length) {
            return false;
        }

        boolean result = false;

        if (inputPipeFile != null) {
            try {
                boolean alreadyWrite = false;

                if (isAMR) {
                    alreadyWrite = checkSID(buf, size);
                }

                if (!alreadyWrite) {
                    inputPipeFile.write(buf, RTP_HEADER_SIZE + 1, size - RTP_HEADER_SIZE - 1);
                }
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private static final int FRAME_TYPE_AMR = 8;
    private static final int FRAME_TYPE_SID = 9;

    private long lastTimestamp = 0;
    private int lastFrameType = 0;
    private int lastAMRMode = 0;

    public static final long toUnsignedInt(byte[] b) {
        long l = 0;

        l |= b[0] & 0xFF;
        l <<= 8;
        l |= b[1] & 0xFF;
        l <<= 8;
        l |= b[2] & 0xFF;
        l <<= 8;
        l |= b[3] & 0xFF;

        return l;
    }

    private boolean checkSID(byte[] buf, int size) {
        if (buf == null) {
            return false;
        }


        boolean result = false;

        byte[] timestampBuf = new byte[4];
        System.arraycopy(buf, 4, timestampBuf, 0, timestampBuf.length);

        long timestamp = toUnsignedInt(timestampBuf);

        int frameType= ((buf[RTP_HEADER_SIZE + 1] & 0xff) >> 3) & 0x0f;

//        logger.debug("AMR frame: timestamp {} frametype {}", timestamp, frameType);

        if (frameType == FRAME_TYPE_SID) {
            if (lastTimestamp > 0) {
                int timestampGap = (int)(((timestamp - lastTimestamp) / 20) / 16 - 1);
                if (timestampGap >= 0) {

                    result = true;
//                    logger.debug("AMR frame: write silence count {} before sid", timestampGap);
                    writeAMRSilencePacket(timestampGap);
                }
            }
        }
        else if (lastFrameType == FRAME_TYPE_SID && frameType != FRAME_TYPE_SID) {
            if (lastTimestamp > 0) {
                int timestampGap = (int)(((timestamp - lastTimestamp) / 20) / 16 - 1);
                if (timestampGap >= 0) {

//                    logger.debug("AMR frame: Now Voice Frame write silence count {} before {}", timestampGap, frameType);
                    writeAMRSilencePacket(timestampGap);
                }
            }
        }

        lastFrameType = frameType;
        if (frameType != FRAME_TYPE_SID) {
            lastAMRMode = frameType;
        }

        lastTimestamp = timestamp;

        return result;
    }

    private void writeAMRSilencePacket(int count) {

        //int dataSize = AMRSlience.getPayloadSize(FRAME_TYPE_AMR);
        int dataSize = AMRSlience.getPayloadSize( lastAMRMode);
        if (dataSize == 0) {
            return;
        }

//        logger.debug("AMR frame: BF write silence file name {} count {} {} size : {} ",lastAMRMode,count,inputPipeFile.toString(),dataSize);

        byte[] data = new byte[dataSize];
        AMRSlience.copySilenceBuffer(lastAMRMode, data, 0);

        if (inputPipeFile != null) {
            for (int i = 0; i < count; i++) {
                try {
                    inputPipeFile.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setRelayQueue(String queueName) {
        createPipe(queueName);
        rmqClient = RmqClient.getInstance(queueName);
    }

    private String inputCodec;

    public void setInputCodec(String codec) {
        inputCodec = codec;
    }

    private static final byte[] AMR_HEADER = { 0x23, 0x21, 0x41, 0x4D, 0x52, 0x2D, 0x57, 0x42, 0x0A };

    public void createPipe(String queueName) {
        inputPipeName = String.format("/tmp/%s_i", queueName);
        outputPipeName = String.format("/tmp/%s_o.wav", queueName);
        Process p;
        p = ShellUtil.createNamedPipe(inputPipeName);
        ShellUtil.waitShell(p);
        p = ShellUtil.createNamedPipe(outputPipeName);
        ShellUtil.waitShell(p);

    }

    public boolean deleteFile(String filename) {
        if (filename == null) {
            return false;
        }

        boolean result = false;
        File file = new File(filename);

        if (file.exists() && file.isFile()) {
            result = file.delete();
        }

        return result;
    }

    public void saveToFile(String filename) {
        try {
            fileStream = new FileOutputStream(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Process transcodingProcess = null;


    private long linearSum = 0;
    private int linearSumCount = 0;
    private short prevValue = 0;
    private long silenceStart;
    private long energyStart;

    class FfmpegRunnable implements Runnable {
        @Override
        public void run() {

            logger.info("Ffmpeg proc ({}) start", inputPipeName);

            transcodingProcess = ShellUtil.startAlawTranscoding(inputPipeName, outputPipeName);

            ShellUtil.waitShell(transcodingProcess);

            logger.info("Ffmpeg proc ({}) end", inputPipeName);
        }
    }

    private static final int LINEAR_PAYLOAD_SIZE = 320;

    class RmqRelayRunnable implements Runnable {
        @Override
        public void run() {
            logger.info("Rmq relay proc ({}) start. queue [{}]", outputPipeName, (rmqClient != null) ? "yes" : "no");

            byte[] pipeBuf = new byte[LINEAR_PAYLOAD_SIZE];
            try {
                logger.info("Rmq relay proc ({}) ready. isQuit [{}]", outputPipeName, isQuit);

                while (!isQuit) {
                    int size = outputPipeFile.read(pipeBuf);
                    if (size > 0) {

                        if (rmqClient != null && rmqClient.isConnected()) {

                            rmqClient.send(pipeBuf, size);

                            // Just for log
                            if (fileStream != null) {
                                try {
                                    fileStream.write(pipeBuf, 0, size);
                                } catch (Exception e) {
                                    logger.warn("Exception [{}] [{}]", e.getClass(), e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (audioDetectLevel > 0 && silenceDetectLevel > 0) {
                            energyDetect(pipeBuf, size, (rmqClient != null) ? true : false);
                        }

                    }
                }

            } catch (Exception e) {
                logger.warn("Exception [{}] [{}]", e.getClass(), e.getMessage());
                e.printStackTrace();
            }

            logger.info("Rmq relay proc ({}) end", outputPipeName);
        }

        private boolean energyDetect(byte[] buf, int length, boolean isCaller) {
            if (buf == null || length < RTP_HEADER_SIZE) {
                return false;
            }

            for (int i = RTP_HEADER_SIZE; i < length; i += 2) {
                short value = (short)((short)(((buf[i + 1] & 0xff) << 8) & 0xff00) | (short)(buf[i] & 0xff));

                if (value > 0 && value > prevValue) {
                    prevValue = value;
                }
                else if (value < 0 && prevValue > 0) {
                    linearSum += prevValue;
                    prevValue = 0;

                    if (linearSumCount >= 5) {
//                        logger.info("energy = {}", linearSum);
                        if (!isEnergyDetected && linearSum >= audioDetectLevel) {
                            if (silenceStart > 0) {
                                silenceStart = 0;
                            }
                            //
                            // TODO: Voice detected
                            //
                            long timestamp = System.currentTimeMillis();
                            if (energyStart == 0) {
                                energyStart = timestamp;
                            }
                            else if (timestamp - energyStart > energyDetectDuration) {
                                logger.info("Energy Detected [{}]", isCaller ? "caller" : "callee");

                                isEnergyDetected = true;
                                SessionStateManager.getInstance().setState(sessionId, SessionState.UPDATE, (Boolean)true);

                                if (roomInfo != null) {
                                    roomInfo.setVoice(true);
                                }
                            }
                        }
                        else if (isEnergyDetected) {
                            if (energyStart > 0) {
                                energyStart = 0;
                            }

                            if (linearSum < silenceDetectLevel) {
                                //
                                // TODO: Silence detected
                                //
                                long timestamp = System.currentTimeMillis();
                                if (silenceStart == 0) {
                                    silenceStart = timestamp;
                                }
                                else if (timestamp - silenceStart > silenceDetectDuration) {
                                    logger.info("Silence Detected [{}] interval [{}]", isCaller ? "caller" : "callee", timestamp - silenceStart);
                                    isEnergyDetected = false;
                                    SessionStateManager.getInstance().setState(sessionId, SessionState.UPDATE, (Boolean)false);

                                    if (roomInfo != null) {
                                        roomInfo.setVoice(false);
                                    }
                                }
                            }
                            else if (silenceStart > 0) {
                                silenceStart = 0;
                            }
                        }
                        else if (energyStart > 0) {
                            energyStart = 0;
                        }

                        linearSumCount = 0;
                        linearSum = 0;

                    }
                }
            }

            linearSumCount++;

            return true;
        }
    }

}

