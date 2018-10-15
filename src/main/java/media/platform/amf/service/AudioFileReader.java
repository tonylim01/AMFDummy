package media.platform.amf.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class AudioFileReader {

    private String filename;
    private byte[] buffer = null;
    private int pos;

    public AudioFileReader(String filename) {
        this.filename = filename;
        this.pos = 0;
    }

    public boolean load() {
        File file = new File(filename);

        if (file == null) {
            return false;
        }

        buffer = new byte[(int)file.length()];
        int totalRead = 0;
        int bytesRemaining = 0;
        InputStream is = null;

        try {
            is = new BufferedInputStream(new FileInputStream(file));

            while (totalRead < buffer.length) {
                bytesRemaining = buffer.length - totalRead;
                int bytesRead = is.read(buffer, totalRead, bytesRemaining);
                if (bytesRead > 0) {
                    totalRead += bytesRead;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return true;
    }

    public void get(byte[] dst, int start, int length) {

        if (pos + length < buffer.length) {
            System.arraycopy(buffer, pos, dst, start, length);
            pos += length;
        }
        else {
            System.arraycopy(buffer, pos, dst, start, buffer.length - pos);
            pos = 0;
        }

    }

    public void get(byte[] dst, int length) {

        get(dst, 0, length);
    }

    public byte[] getAMRWBPayload() {

        byte[] dst = null;
        int payloadSize = AMRPayload.getPayloadSize16k(buffer[pos]);
        if (payloadSize > 0) {
            dst = new byte[payloadSize + 1];
            dst[0] = (byte)0xf0;
            get(dst, 1, payloadSize);
        }
        else {
            // TODO: Error
        }

        return dst;
    }

    public byte[] getAMRWNPayload() {

        byte[] dst = null;
        int payloadSize = AMRPayload.getPayloadSize8k(buffer[pos]);
        if (payloadSize > 0) {
            dst = new byte[payloadSize + 1];
            dst[0] = (byte)0xf0;
            get(dst, 1, payloadSize);
        }
        else {
            // TODO: Error
        }

        return dst;
    }
}
