package org.cns.server;

import java.nio.ByteBuffer;
import java.util.Queue;

import org.cns.api.server.MessageReader;
import org.cns.api.server.ServerInfo;

/**
 * Парсер сообщений с использованием маркера конца сообщения.
 * 
 * @author ivanovd
 *
 */
public class MarkeredMessageReader implements MessageReader {

    private ByteBuffer buffer;
    private StringBuilder msgAccum = new StringBuilder();
    private boolean messageReady = false;

    public MarkeredMessageReader(int bufferSize) {
        this.buffer = ByteBuffer.allocate(bufferSize);
    }

    @Override
    public void processBuffer(int bytesRead, Queue<String> incomingMessages) {
        buffer.flip();

        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);

        msgAccum.append(new String(bytes));

        // System.out.println(msgAccum);

        int idx = msgAccum.indexOf(ServerInfo.MSG_DELIM);
        if (idx != -1) {
            incomingMessages.add(msgAccum.substring(0, idx).trim());
            msgAccum.delete(0, idx + ServerInfo.MSG_DELIM.length());
            messageReady = true;
        } else {
            messageReady = false;
        }

        buffer.clear();
    }

    @Override
    public ByteBuffer getBuffer() {
        return buffer;
    }

    @Override
    public boolean isMessageReady() {
        return messageReady;
    }

    public String toString() {
        return msgAccum.toString();
    }

    public void reset() {
        msgAccum.setLength(0);
        messageReady = false;
    }

}
