/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file RmqMessageType.java
 * @author Tony Lim
 *
 */

package media.platform.amf.rmqif.types;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RmqMessageType {

    public static final String RMQ_MSG_STR_INBOUND_SET_OFFER_REQ    = "mfmp_set_offer_req";
    public static final String RMQ_MSG_STR_INBOUND_SET_OFFER_RES    = "mfmp_set_offer_res";
    public static final String RMQ_MSG_STR_INBOUND_GET_ANSWER_REQ   = "mfmp_get_answer_req";
    public static final String RMQ_MSG_STR_INBOUND_GET_ANSWER_RES   = "mfmp_get_answer_res";
    public static final String RMQ_MSG_STR_OUTBOUND_GET_OFFER_REQ	= "mfmp_outbound_set_offer_req";
    public static final String RMQ_MSG_STR_OUTBOUND_GET_OFFER_RES	= "mfmp_outbound_set_offer_res";
    public static final String RMQ_MSG_STR_OUTBOUND_SET_ANSWER_REQ	= "mfmp_outbound_get_answer_req";
    public static final String RMQ_MSG_STR_OUTBOUND_SET_ANSWER_RES	= "mfmp_outbound_get_answer_res";
    public static final String RMQ_MSG_STR_NEGO_DONE_REQ	        = "mfmp_nego_done_req";
    public static final String RMQ_MSG_STR_NEGO_DONE_RES	        = "mfmp_nego_done_res";
    public static final String RMQ_MSG_STR_COMMAND_REQ	            = "mfmp_command_req";
    public static final String RMQ_MSG_STR_COMMAND_RES	            = "mfmp_command_res";
    public static final String RMQ_MSG_STR_PLAY_PROMPT_REQ	        = "mfmp_play_prompt_req";
    public static final String RMQ_MSG_STR_PLAY_PROMPT_ACK	        = "mfmp_play_prompt_ack";
    public static final String RMQ_MSG_STR_PLAY_PROMPT_RES	        = "mfmp_play_prompt_res";
    public static final String RMQ_MSG_STR_PLAY_COLLECT_REQ	        = "mfmp_play_collect_req";
    public static final String RMQ_MSG_STR_PLAY_COLLECT_ACK	        = "mfmp_play_collect_ack";
    public static final String RMQ_MSG_STR_PLAY_COLLECT_RES    	    = "mfmp_play_collect_res";
    public static final String RMQ_MSG_STR_PLAY_RECORD_REQ	        = "mfmp_play_record_req";
    public static final String RMQ_MSG_STR_PLAY_RECORD_ACK     	    = "mfmp_play_record_ack";
    public static final String RMQ_MSG_STR_PLAY_RECORD_RES	        = "mfmp_play_record_res";
    public static final String RMQ_MSG_STR_STOP_RECORD_REQ     	    = "mfmp_stop_record_req";
    public static final String RMQ_MSG_STR_STOP_RECORD_RES     	    = "mfmp_stop_record_res";
    public static final String RMQ_MSG_STR_CONTROL_FILE_REQ    	    = "mfmp_control_file_req";
    public static final String RMQ_MSG_STR_CONTROL_FILE_RES    	    = "mfmp_control_file_res";
    public static final String RMQ_MSG_STR_PLAY_ASR_REQ	            = "mfmp_play_asr_req";
    public static final String RMQ_MSG_STR_PLAY_ASR_ACK    	        = "mfmp_play_asr_ack";
    public static final String RMQ_MSG_STR_PLAY_ASR_RES	            = "mfmp_play_asr_res";
    public static final String RMQ_MSG_STR_STOP_PLAY_REQ            = "mfmp_stop_play_req";
    public static final String RMQ_MSG_STR_STOP_PLAY_RES	        = "mfmp_stop_play_res";
    public static final String RMQ_MSG_STR_HANGUP_REQ	            = "mfmp_hangup_req";
    public static final String RMQ_MSG_STR_HANGUP_RES	            = "mfmp_hangup_res";
    public static final String RMQ_MSG_STR_LONGCALL_CHECK_REQ	    = "mfmp_longcall_check_req";
    public static final String RMQ_MSG_STR_LONGCALL_CHECK_RES	    = "mfmp_longcall_check_res";
    public static final String RMQ_MSG_STR_CREATE_CONFERENCE_REQ	= "mfmp_create_conference_req";
    public static final String RMQ_MSG_STR_CREATE_CONFERENCE_RES	= "mfmp_create_conference_res";
    public static final String RMQ_MSG_STR_DELETE_CONFERENCE_REQ	= "mfmp_delete_conference_req";
    public static final String RMQ_MSG_STR_DELETE_CONFERENCE_RES	= "mfmp_delete_conference_res";
    public static final String RMQ_MSG_STR_JOIN_CONFERENCE_REQ	    = "mfmp_join_conference_req";
    public static final String RMQ_MSG_STR_JOIN_CONFERENCE_RES	    = "mfmp_join_conference_res";
    public static final String RMQ_MSG_STR_WITHDRAW_CONFERENCE_REQ	= "mfmp_withdraw_conference_req";
    public static final String RMQ_MSG_STR_WITHDRAW_CONFERENCE_RES	= "mfmp_withdraw_conference_res";
    public static final String RMQ_MSG_STR_UPDATE_CONFERENCE_REQ	= "mfmp_update_conference_req";
    public static final String RMQ_MSG_STR_UPDATE_CONFERENCE_RES	= "mfmp_update_conference_res";
    public static final String RMQ_MSG_STR_STARTRECORD_CONFERENCE_REQ	= "mfmp_start_record_conference_req";
    public static final String RMQ_MSG_STR_STARTRECORD_CONFERENCE_RES	= "mfmp_start_record_conference_res";
    public static final String RMQ_MSG_STR_STOPRECORD_CONFERENCE_REQ	= "mfmp_stop_record_conference_req";
    public static final String RMQ_MSG_STR_STOPRECORD_CONFERENCE_RES	= "mfmp_stop_record_conference_res";
    public static final String RMQ_MSG_STR_RECORD_CONFERENCE_RPT	= "mfmp_record_conference_rpt";
    public static final String RMQ_MSG_STR_RECORD_CONFERENCE_ACK	= "mfmp_record_conference_ack";
    public static final String RMQ_MSG_STR_CHANGE_CONFERENCE_REQ	= "mfmp_change_conference_req";
    public static final String RMQ_MSG_STR_CHANGE_CONFERENCE_RES	= "mfmp_change_conference_res";
    public static final String RMQ_MSG_STR_DTMF_CONFERENCE_RPT	    = "mfmp_dtmf_conference_rpt";
    public static final String RMQ_MSG_STR_DTMF_CONFERENCE_ACK      = "mfmp_dtmf_conference_ack";
    public static final String RMQ_MSG_STR_PLAY_CONFERENCE_REQ      = "mfmp_play_conference_req";
    public static final String RMQ_MSG_STR_PLAY_CONFERENCE_ACK      = "mfmp_play_conference_ack";
    public static final String RMQ_MSG_STR_PLAY_CONFERENCE_RES      = "mfmp_play_conference_res";

    public static final String RMQ_MSG_STR_HEARTBEAT                = "mfmp_heartbeat_indi";

    public static final String RMQ_MSG_STR_SERVICE_START_REQ        = "mfmp_service_start_req";
    public static final String RMQ_MSG_STR_SERVICE_START_RES        = "mfmp_service_start_res";
    public static final String RMQ_MSG_STR_COMMAND_STOP_REQ         = "msmp_command_stop_req";
    public static final String RMQ_MSG_STR_COMMAND_STOP_RES         = "msmp_command_stop_res";
    public static final String RMQ_MSG_STR_COMMAND_DONE_REQ         = "msmp_command_done_req";
    public static final String RMQ_MSG_STR_COMMAND_DONE_RES         = "msmp_command_done_res";
    public static final String RMQ_MSG_STR_COMMAND_END_REQ          = "msmp_command_end_req";
    public static final String RMQ_MSG_STR_COMMAND_END_RES          = "msmp_command_end_res";

    public static final String RMQ_MSG_STR_DTMF_DETECT_REQ          = "mfmp_dtmf_det_req";
    public static final String RMQ_MSG_STR_DTMF_DETECT_RES          = "mfmp_dtmf_det_res";

    public static final String RMQ_MSG_STR_LOGIN_REQ	= "mfmp_login_req";
    public static final String RMQ_MSG_STR_LOGIN_RES	= "mfmp_login_res";

    public static final int RMQ_MSG_TYPE_UNDEFINED = 0;
    public static final int RMQ_MSG_TYPE_INBOUND_SET_OFFER_REQ = 0x0001;
    public static final int RMQ_MSG_TYPE_INBOUND_SET_OFFER_RES = 0x1001;
    public static final int RMQ_MSG_TYPE_INBOUND_GET_ANSWER_REQ = 0x0002;
    public static final int RMQ_MSG_TYPE_INBOUND_GET_ANSWER_RES = 0x1002;
    public static final int RMQ_MSG_TYPE_OUTBOUND_SET_OFFER_REQ = 0x0003;
    public static final int RMQ_MSG_TYPE_OUTBOUND_SET_OFFER_RES = 0x1003;
    public static final int RMQ_MSG_TYPE_OUTBOUND_GET_ANSWER_REQ = 0x0004;
    public static final int RMQ_MSG_TYPE_OUTBOUND_GET_ANSWER_RES = 0x1004;

    public static final int RMQ_MSG_TYPE_HANGUP_REQ = 0x0005;
    public static final int RMQ_MSG_TYPE_HANGUP_RES = 0x1005;

    public static final int RMQ_MSG_TYPE_NEGO_DONE_REQ = 0x0006;
    public static final int RMQ_MSG_TYPE_NEGO_DONE_RES = 0x1006;
    public static final int RMQ_MSG_TYPE_COMMAND_REQ = 0x0007;
    public static final int RMQ_MSG_TYPE_COMMAND_RES = 0x1007;
    public static final int RMQ_MSG_TYPE_LONGCALL_CHECK_REQ = 0x0008;
    public static final int RMQ_MSG_TYPE_LONGCALL_CHECK_RES = 0x1008;

    public static final int RMQ_MSG_TYPE_PLAY_PROMPT_REQ = 0x0011;
    public static final int RMQ_MSG_TYPE_PLAY_PROMPT_RES = 0x1011;
    public static final int RMQ_MSG_TYPE_PLAY_PROMPT_ACK = 0x2011;
    public static final int RMQ_MSG_TYPE_PLAY_COLLECT_REQ = 0x0012;
    public static final int RMQ_MSG_TYPE_PLAY_COLLECT_RES = 0x1012;
    public static final int RMQ_MSG_TYPE_PLAY_COLLECT_ACK = 0x2012;
    public static final int RMQ_MSG_TYPE_PLAY_RECORD_REQ = 0x0013;
    public static final int RMQ_MSG_TYPE_PLAY_RECORD_RES = 0x1013;
    public static final int RMQ_MSG_TYPE_PLAY_RECORD_ACK = 0x2013;
    public static final int RMQ_MSG_TYPE_STOP_PLAY_REQ = 0x0014;
    public static final int RMQ_MSG_TYPE_STOP_PLAY_RES = 0x1014;
    public static final int RMQ_MSG_TYPE_STOP_RECORD_REQ = 0x0015;
    public static final int RMQ_MSG_TYPE_STOP_RECORD_RES = 0x1015;
    public static final int RMQ_MSG_TYPE_CONTROL_FILE_REQ = 0x0016;
    public static final int RMQ_MSG_TYPE_CONTROL_FILE_RES = 0x1016;
    public static final int RMQ_MSG_TYPE_PLAY_ASR_REQ = 0x0017;
    public static final int RMQ_MSG_TYPE_PLAY_ASR_RES = 0x1017;
    public static final int RMQ_MSG_TYPE_PLAY_ASR_ACK = 0x2017;

    public static final int RMQ_MSG_TYPE_CREATE_CONFERENCE_REQ = 0x0021;
    public static final int RMQ_MSG_TYPE_CREATE_CONFERENCE_RES = 0x1021;
    public static final int RMQ_MSG_TYPE_DELETE_CONFERENCE_REQ = 0x0022;
    public static final int RMQ_MSG_TYPE_DELETE_CONFERENCE_RES = 0x1022;
    public static final int RMQ_MSG_TYPE_JOIN_CONFERENCE_REQ = 0x0023;
    public static final int RMQ_MSG_TYPE_JOIN_CONFERENCE_RES = 0x1023;
    public static final int RMQ_MSG_TYPE_WITHDRAW_CONFERENCE_REQ = 0x0024;
    public static final int RMQ_MSG_TYPE_WITHDRAW_CONFERENCE_RES = 0x1024;
    public static final int RMQ_MSG_TYPE_UPDATE_CONFERENCE_REQ = 0x0025;
    public static final int RMQ_MSG_TYPE_UPDATE_CONFERENCE_RES = 0x1025;
    public static final int RMQ_MSG_TYPE_STARTRECORD_CONFERENCE_REQ = 0x0026;
    public static final int RMQ_MSG_TYPE_STARTRECORD_CONFERENCE_RES = 0x1026;
    public static final int RMQ_MSG_TYPE_STOPRECORD_CONFERENCE_REQ = 0x0027;
    public static final int RMQ_MSG_TYPE_STOPRECORD_CONFERENCE_RES = 0x1027;
    public static final int RMQ_MSG_TYPE_RECORD_CONFERENCE_RPT = 0x0028;
    public static final int RMQ_MSG_TYPE_RECORD_CONFERENCE_ACK = 0x2028;
    public static final int RMQ_MSG_TYPE_CHANGE_CONFERENCE_REQ = 0x0029;
    public static final int RMQ_MSG_TYPE_CHANGE_CONFERENCE_RES = 0x1029;
    public static final int RMQ_MSG_TYPE_DTMF_CONFERENCE_RPT = 0x002a;
    public static final int RMQ_MSG_TYPE_DTMF_CONFERENCE_ACK = 0x202a;
    public static final int RMQ_MSG_TYPE_PLAY_CONFERENCE_REQ = 0x002b;
    public static final int RMQ_MSG_TYPE_PLAY_CONFERENCE_RES = 0x102b;
    public static final int RMQ_MSG_TYPE_PLAY_CONFERENCE_ACK = 0x202b;

    public static final int RMQ_MSG_TYPE_HEARTBEAT = 0x0091;

    public static final int RMQ_MSG_TYPE_SERVICE_START_REQ = 0x0031;
    public static final int RMQ_MSG_TYPE_SERVICE_START_RES = 0x1031;
    public static final int RMQ_MSG_TYPE_COMMAND_STOP_REQ = 0x0032;
    public static final int RMQ_MSG_TYPE_COMMAND_STOP_RES = 0x1032;
    public static final int RMQ_MSG_TYPE_COMMAND_DONE_REQ = 0x0033;
    public static final int RMQ_MSG_TYPE_COMMAND_DONE_RES = 0x1033;
    public static final int RMQ_MSG_TYPE_COMMAND_END_REQ = 0x0034;
    public static final int RMQ_MSG_TYPE_COMMAND_END_RES = 0x1034;

    public static final int RMQ_MSG_TYPE_DTMF_DETECT_REQ = 0x0035;
    public static final int RMQ_MSG_TYPE_DTMF_DETECT_RES = 0x1035;

    public static final int RMQ_MSG_TYPE_LOGIN_REQ = 0x0099;
    public static final int RMQ_MSG_TYPE_LOGIN_RES = 0x1099;


    public static final int RMQ_MSG_COMMON_REASON_CODE_SUCCESS = 0;
    public static final int RMQ_MSG_COMMON_REASON_CODE_FAILURE = -1;
    public static final int RMQ_COMMON_REASON_CODE_TIMEOUT = -2;
    public static final int RMQ_MSG_COMMON_REASON_CODE_WRONG_PARAM = -3;
    public static final int RMQ_MSG_COMMON_REASON_CODE_ALREADY_EXIST = -4;

    private static Map<String, Integer> typeMap() {
        return Collections.unmodifiableMap(Stream.of(
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_INBOUND_SET_OFFER_REQ, RMQ_MSG_TYPE_INBOUND_SET_OFFER_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_INBOUND_SET_OFFER_RES, RMQ_MSG_TYPE_INBOUND_SET_OFFER_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_INBOUND_GET_ANSWER_REQ, RMQ_MSG_TYPE_INBOUND_GET_ANSWER_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_INBOUND_GET_ANSWER_RES, RMQ_MSG_TYPE_INBOUND_GET_ANSWER_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_OUTBOUND_GET_OFFER_REQ, RMQ_MSG_TYPE_OUTBOUND_SET_OFFER_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_OUTBOUND_GET_OFFER_RES, RMQ_MSG_TYPE_OUTBOUND_SET_OFFER_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_OUTBOUND_SET_ANSWER_REQ, RMQ_MSG_TYPE_OUTBOUND_GET_ANSWER_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_OUTBOUND_SET_ANSWER_RES, RMQ_MSG_TYPE_OUTBOUND_GET_ANSWER_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_NEGO_DONE_REQ, RMQ_MSG_TYPE_NEGO_DONE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_NEGO_DONE_RES, RMQ_MSG_TYPE_NEGO_DONE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_COMMAND_REQ, RMQ_MSG_TYPE_COMMAND_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_COMMAND_RES, RMQ_MSG_TYPE_COMMAND_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_PROMPT_REQ, RMQ_MSG_TYPE_PLAY_PROMPT_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_PROMPT_ACK, RMQ_MSG_TYPE_PLAY_PROMPT_ACK),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_PROMPT_RES, RMQ_MSG_TYPE_PLAY_PROMPT_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_COLLECT_REQ, RMQ_MSG_TYPE_PLAY_COLLECT_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_COLLECT_ACK, RMQ_MSG_TYPE_PLAY_COLLECT_ACK),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_COLLECT_RES, RMQ_MSG_TYPE_PLAY_COLLECT_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_RECORD_REQ, RMQ_MSG_TYPE_PLAY_RECORD_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_RECORD_ACK, RMQ_MSG_TYPE_PLAY_RECORD_ACK),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_RECORD_RES, RMQ_MSG_TYPE_PLAY_RECORD_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STOP_RECORD_REQ, RMQ_MSG_TYPE_STOP_RECORD_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STOP_RECORD_RES, RMQ_MSG_TYPE_STOP_RECORD_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_CONTROL_FILE_REQ, RMQ_MSG_TYPE_CONTROL_FILE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_CONTROL_FILE_RES, RMQ_MSG_TYPE_CONTROL_FILE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_ASR_REQ, RMQ_MSG_TYPE_PLAY_ASR_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_ASR_ACK, RMQ_MSG_TYPE_PLAY_ASR_ACK),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_ASR_RES, RMQ_MSG_TYPE_PLAY_ASR_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STOP_PLAY_REQ, RMQ_MSG_TYPE_STOP_PLAY_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STOP_PLAY_RES, RMQ_MSG_TYPE_STOP_PLAY_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_HANGUP_REQ, RMQ_MSG_TYPE_HANGUP_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_HANGUP_RES, RMQ_MSG_TYPE_HANGUP_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_LONGCALL_CHECK_REQ, RMQ_MSG_TYPE_LONGCALL_CHECK_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_LONGCALL_CHECK_RES, RMQ_MSG_TYPE_LONGCALL_CHECK_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_CREATE_CONFERENCE_REQ, RMQ_MSG_TYPE_CREATE_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_CREATE_CONFERENCE_RES, RMQ_MSG_TYPE_CREATE_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_DELETE_CONFERENCE_REQ, RMQ_MSG_TYPE_DELETE_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_DELETE_CONFERENCE_RES, RMQ_MSG_TYPE_DELETE_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_JOIN_CONFERENCE_REQ, RMQ_MSG_TYPE_JOIN_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_JOIN_CONFERENCE_RES, RMQ_MSG_TYPE_JOIN_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_WITHDRAW_CONFERENCE_REQ, RMQ_MSG_TYPE_WITHDRAW_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_WITHDRAW_CONFERENCE_RES, RMQ_MSG_TYPE_WITHDRAW_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_UPDATE_CONFERENCE_REQ, RMQ_MSG_TYPE_UPDATE_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_UPDATE_CONFERENCE_RES, RMQ_MSG_TYPE_UPDATE_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STARTRECORD_CONFERENCE_REQ, RMQ_MSG_TYPE_STARTRECORD_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STARTRECORD_CONFERENCE_RES, RMQ_MSG_TYPE_STARTRECORD_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STOPRECORD_CONFERENCE_REQ, RMQ_MSG_TYPE_STOPRECORD_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STOPRECORD_CONFERENCE_RES, RMQ_MSG_TYPE_STOPRECORD_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_RECORD_CONFERENCE_RPT, RMQ_MSG_TYPE_RECORD_CONFERENCE_RPT),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_RECORD_CONFERENCE_ACK, RMQ_MSG_TYPE_RECORD_CONFERENCE_ACK),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_CHANGE_CONFERENCE_REQ, RMQ_MSG_TYPE_CHANGE_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_CHANGE_CONFERENCE_RES, RMQ_MSG_TYPE_CHANGE_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_DTMF_CONFERENCE_RPT, RMQ_MSG_TYPE_DTMF_CONFERENCE_RPT),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_DTMF_CONFERENCE_ACK, RMQ_MSG_TYPE_DTMF_CONFERENCE_ACK),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_CONFERENCE_REQ, RMQ_MSG_TYPE_PLAY_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_CONFERENCE_ACK, RMQ_MSG_TYPE_PLAY_CONFERENCE_ACK),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_CONFERENCE_RES, RMQ_MSG_TYPE_PLAY_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_SERVICE_START_REQ, RMQ_MSG_TYPE_SERVICE_START_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_SERVICE_START_RES, RMQ_MSG_TYPE_SERVICE_START_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_COMMAND_STOP_REQ, RMQ_MSG_TYPE_COMMAND_STOP_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_COMMAND_STOP_RES, RMQ_MSG_TYPE_COMMAND_STOP_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_COMMAND_DONE_REQ, RMQ_MSG_TYPE_COMMAND_DONE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_COMMAND_DONE_RES, RMQ_MSG_TYPE_COMMAND_DONE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_COMMAND_END_REQ, RMQ_MSG_TYPE_COMMAND_END_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_COMMAND_END_RES, RMQ_MSG_TYPE_COMMAND_END_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_DTMF_DETECT_REQ, RMQ_MSG_TYPE_DTMF_DETECT_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_DTMF_DETECT_RES, RMQ_MSG_TYPE_DTMF_DETECT_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_LOGIN_REQ, RMQ_MSG_TYPE_LOGIN_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_LOGIN_RES, RMQ_MSG_TYPE_LOGIN_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_HEARTBEAT, RMQ_MSG_TYPE_HEARTBEAT)
                ).collect(Collectors.toMap((e) -> e.getKey(), (e) ->e.getValue())));
    }

    private static Map<Integer, String> typeStrMap() {
        return Collections.unmodifiableMap(Stream.of(
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_INBOUND_SET_OFFER_REQ, "InboundSetOfferReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_INBOUND_SET_OFFER_RES, "InboundSetOfferRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_INBOUND_GET_ANSWER_REQ, "InboundGetAnswerReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_INBOUND_GET_ANSWER_RES, "InboundGetAnswerRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_OUTBOUND_SET_OFFER_REQ, "OutboundGetOfferReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_OUTBOUND_SET_OFFER_RES, "OutboundGetOfferRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_OUTBOUND_GET_ANSWER_REQ, "OutboundSetOfferReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_OUTBOUND_GET_ANSWER_RES, "OutboundSetOfferRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_NEGO_DONE_REQ, "NegoDoneReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_NEGO_DONE_RES, "NegoDoneRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_COMMAND_REQ, "CommandReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_COMMAND_RES, "CommandRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_PROMPT_REQ, "PlayPromptReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_PROMPT_ACK, "PlayPromptAck"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_PROMPT_RES, "PlayPromptRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_COLLECT_REQ, "PlayCollectReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_COLLECT_ACK, "PlayCollectAck"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_COLLECT_RES, "PlayCollectRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_RECORD_REQ, "PlayRecordReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_RECORD_ACK, "PlayRecordAck"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_RECORD_RES, "PlayRecordRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_STOP_RECORD_REQ, "StopRecordReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_STOP_RECORD_RES, "StopRecordRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_CONTROL_FILE_REQ, "ControlFileReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_CONTROL_FILE_RES, "ControlFileRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_ASR_REQ, "PlayASRReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_ASR_ACK, "PlayASRAck"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_ASR_RES, "PlayASRRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_STOP_PLAY_REQ, "StopPlayReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_STOP_PLAY_RES, "StopPlayRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_HANGUP_REQ, "HangupReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_HANGUP_RES, "HangupRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_LONGCALL_CHECK_REQ, "LongcallCheckReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_LONGCALL_CHECK_RES, "LongcallCheckRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_CREATE_CONFERENCE_REQ, "CreateConferenceReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_CREATE_CONFERENCE_RES, "CreateConferenceRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_DELETE_CONFERENCE_REQ, "DeleteConferenceReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_DELETE_CONFERENCE_RES, "DeleteConferenceRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_JOIN_CONFERENCE_REQ, "JoinConferenceReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_JOIN_CONFERENCE_RES, "JoinConferenceRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_WITHDRAW_CONFERENCE_REQ, "WithdrawConferenceReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_WITHDRAW_CONFERENCE_RES, "WithdrawConferenceRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_UPDATE_CONFERENCE_REQ, "UpdateConferenceReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_UPDATE_CONFERENCE_RES, "UpdateConferenceRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_STARTRECORD_CONFERENCE_REQ, "StartRecordConferenceReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_STARTRECORD_CONFERENCE_RES, "StartRecordConferenceRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_STOPRECORD_CONFERENCE_REQ, "StopRecordConferenceReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_STOPRECORD_CONFERENCE_RES, "StopRecordConferenceRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_RECORD_CONFERENCE_RPT, "RecordConferenceRpt"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_RECORD_CONFERENCE_ACK, "RecordConferenceAck"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_CHANGE_CONFERENCE_REQ, "ChangeConferenceReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_CHANGE_CONFERENCE_RES, "ChangeConferenceRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_DTMF_CONFERENCE_RPT, "DTMFConferenceRpt"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_DTMF_CONFERENCE_ACK, "DTMFConferenceAck"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_CONFERENCE_REQ, "PlayConferenceReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_CONFERENCE_ACK, "PlayConferenceAck"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_PLAY_CONFERENCE_RES, "PlayConferenceRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_SERVICE_START_REQ, "ServiceStartReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_SERVICE_START_RES, "ServiceStartRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_COMMAND_STOP_REQ, "CommandStopReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_COMMAND_STOP_RES, "CommandStopRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_COMMAND_END_REQ, "CommandEndReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_COMMAND_END_RES, "CommandEndRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_DTMF_DETECT_REQ, "DtmfDetReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_DTMF_DETECT_RES, "DtmfDetRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_LOGIN_REQ, "LogInReq"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_LOGIN_RES, "LogInRes"),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_TYPE_HEARTBEAT, "Heartbeat")

        ).collect(Collectors.toMap((e) -> e.getKey(), (e) ->e.getValue())));
    }

    public static int getMessageType(String typeStr) {
        Integer value = typeMap().get(typeStr);
        return (value == null) ? RMQ_MSG_TYPE_UNDEFINED : value.intValue();
    }

    public static String getMessageTypeStr(int messageType) {
        return typeStrMap().get(messageType);
    }
}
