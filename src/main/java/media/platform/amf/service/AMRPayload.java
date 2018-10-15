package media.platform.amf.service;

public class AMRPayload {

    public static int getPayloadSize16k(int headerByte) {
        int result = 0;

        switch (headerByte) {
            case 0x44:  result = 61;    break;
            case 0x3c:  result = 59;    break;
            case 0x34:  result = 51;    break;
            case 0x2c:  result = 47;    break;
            case 0x24:  result = 41;    break;
            case 0x1c:  result = 37;    break;
            case 0x14:  result = 33;    break;
            case 0x0c:  result = 24;    break;
            case 0x04:  result = 18;    break;
        }

        return result;
    }

    public static int getPayloadSize8k(int headerByte) {
        int result = 0;

        switch (headerByte) {
            case 0x3c:  result = 32;    break;
            case 0x34:  result = 27;    break;
            case 0x2c:  result = 21;    break;
            case 0x24:  result = 20;    break;
            case 0x1c:  result = 18;    break;
            case 0x14:  result = 17;    break;
            case 0x0c:  result = 14;    break;
            case 0x04:  result = 13;    break;
        }

        return result;
    }
}
