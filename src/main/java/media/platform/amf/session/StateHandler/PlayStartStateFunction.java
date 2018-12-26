package media.platform.amf.session.StateHandler;

import media.platform.amf.AppInstance;
import media.platform.amf.common.ShellUtil;
import media.platform.amf.config.AmfConfig;
import media.platform.amf.rmqif.messages.FileData;
import media.platform.amf.room.RoomInfo;
import media.platform.amf.room.RoomManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import media.platform.amf.session.SessionInfo;
import media.platform.amf.session.SessionState;

import java.io.File;
import java.util.UUID;

public class PlayStartStateFunction extends PlayStateFunction implements StateFunction {
    private static final Logger logger = LoggerFactory.getLogger(PlayStartStateFunction.class);

    @Override
    public void run(SessionInfo sessionInfo, Object arg) {
        if (sessionInfo == null) {
            return;
        }

        logger.debug("{} PLAY startScheduler state", sessionInfo.getSessionId());

        if (sessionInfo.getServiceState() != SessionState.PLAY_START) {
            sessionInfo.setServiceState(SessionState.PLAY_START);
        }

        RoomInfo roomInfo = RoomManager.getInstance().getRoomInfo( sessionInfo.getConferenceId());
        if (roomInfo == null) {
            logger.error("[{}] No roomInfo found", sessionInfo.getSessionId());
            return;
        }

        if (roomInfo.getGroupId() < 0) {
            logger.error("[{}] No channel group found", sessionInfo.getSessionId());
            return;
        }

        if (arg != null && arg instanceof FileData) {
            FileData fileData = (FileData)arg;
            int toolId;


            boolean callerOnly;
            if (fileData.getPlayType() != null && fileData.getPlayType().equals(FileData.PLAY_TYPE_CALLER_ONLY)) {
                callerOnly = true;
            }
            else {
                callerOnly = false;
            }

            playFile(sessionInfo, roomInfo, fileData);
        }
        else {
            logger.error("[{}] Invalid file data");
        }
    }

    private boolean playFile(SessionInfo sessionInfo, RoomInfo roomInfo, FileData data) {
        if (sessionInfo == null) {
            logger.error("No session");
            return false;
        }

        if (roomInfo == null || data == null) {
            logger.error("[{}] Invalid argument", sessionInfo.getSessionId());
            return false;
        }

        logger.debug("[{}] Play file: channel [{}] mediaType [{}] file [{}] defVol [{}] mixVol [{}] types [{}]",
                sessionInfo.getSessionId(),
                data.getChannel(), data.getMediaType(), data.getPlayFile(),
                data.getDefVolume(), data.getMixVolume(), data.getPlayType());

        String filename;

        if (data.getMediaType() != null && data.getMediaType().equals(FileData.MEDIA_TYPE_STREAM)) {
//            String wavfile = String.format("/tmp/%s.wav", UUID.randomUUID().toString());
            String amrfile = String.format("/tmp/%s.amr", UUID.randomUUID().toString());
            logger.debug("[{}] wav file [{}]", sessionInfo.getSessionId(), amrfile);

            Process p = ShellUtil.convertHlsToAmr( data.getPlayFile(), amrfile);
            ShellUtil.waitShell(p);
            filename = amrfile;
        }
        else {
            AmfConfig config = AppInstance.getInstance().getConfig();
            filename = String.format("%s/%s", config.getLocalBasePath(), data.getPlayFile());

            File file = new File(filename);
            if (!file.exists()) {
                logger.error("[{}] File not found [{}]", sessionInfo.getSessionId(), filename);
                return false;
            }

            int comma = filename.lastIndexOf(".");

            if (comma > 0) {
                String ext = filename.substring(comma + 1);
                if (ext != null && ext.equals("pcm")) {

                    String amffile = String.format("%samr", filename.substring(0, comma + 1));
                    logger.debug("[{}] wav file [{}]", sessionInfo.getSessionId(), amffile);

                    Process p = ShellUtil.convertPcmToAmr(filename, amffile);
                    ShellUtil.waitShell(p);
                    filename = amffile;
                }
            }
        }

        logger.debug("[{}] playfile [{}]", sessionInfo.getSessionId(), filename);

        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }


        return true;
    }

    private boolean updatePlayChannel(SessionInfo sessionInfo, RoomInfo roomInfo, int toolId, boolean callerOnly) {
        if (sessionInfo == null) {
            return false;
        }

        logger.debug("[{}] Update play channel: channel [{}] callerOnly [{}]", sessionInfo.getSessionId(),
                toolId, callerOnly);

        String json;
        int groupId = roomInfo.getGroupId();
        int mixerId = roomInfo.getMixerId();

        if (groupId < 0 || mixerId < 0) {
            return false;
        }


        return true;
    }

}
