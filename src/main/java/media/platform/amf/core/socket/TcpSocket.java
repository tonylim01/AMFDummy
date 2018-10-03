/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 * @file TcpSocket.java
 * @author Tony Lim
 *
 */

package media.platform.amf.core.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

public class TcpSocket {
    private static final Logger logger = LoggerFactory.getLogger(TcpSocket.class);

    private Socket clientSocket = null;
    private DataOutputStream outputStream = null;
    private DataInputStream inputStream = null;

    public boolean connect(String ip, int port) {
        if (clientSocket != null) {
            disconnect();
        }

        try {
            clientSocket = new Socket(ip, port);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();

            disconnect();
            return false;
        }

        try {
            inputStream = new DataInputStream(clientSocket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();

            disconnect();
            return false;
        }
        return true;
    }

    public void disconnect() {
        if (clientSocket == null) {
            return;
        }

        try {
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        clientSocket = null;
    }

    public int send(byte[] bytes) {
        if (bytes == null || clientSocket == null || outputStream == null) {
            return -1;
        }

        try {
            outputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bytes.length;
    }

    /**
     * Reads data from the socket until the read size = readBytes
     * @param buffer
     * @param readBytes
     * @return
     */
    public int read(byte[] buffer, int readBytes) {
        if (buffer == null || clientSocket == null || inputStream == null) {
            return -1;
        }

        int result;
        int pos = 0;

        do {
            try {
                result = inputStream.read(buffer, pos, readBytes);
                if (result <= 0) {
                    break;
                }
                pos += result;
            } catch (Exception e) {
                e.printStackTrace();
                return (e.getClass() == TimeoutException.class) ? 0 : -1;
            }

        } while (pos < readBytes);

        return pos;
    }

    /**
     * Reads data from the socket and returns the read length
     * @param buffer
     * @return
     */
    public int read(byte[] buffer) {
        if (buffer == null || clientSocket == null || inputStream == null) {
            return -1;
        }

        int result;

        try {
            result = inputStream.read(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return (e.getClass() == TimeoutException.class) ? 0 : -1;
        }

        return result;
    }
}
