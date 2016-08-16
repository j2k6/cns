package org.cns.server;

import java.nio.ByteBuffer;
import java.util.Queue;

import org.cns.api.server.ServerInfo;

/**
 * Класс для сборки сообщения из буфера. Опирается на маркер конца сообщения - проверяет наличие его в новой порции
 * данных и выставляет флаг готовности.
 * 
 * @author ivanovd
 *
 */
public class InBufferProcessor {

    private ByteBuffer buffer;
    private StringBuilder msgAccum = new StringBuilder();
    private boolean messageReady = false;

    public InBufferProcessor(int bufferSize) {
        this.buffer = ByteBuffer.allocate(bufferSize);
    }

    public void processBuffer(int bytesRead, Queue<String> incomingMessages) {
        buffer.flip();

        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);

        msgAccum.append(new String(bytes));

//        System.out.println(msgAccum);

        int idx = msgAccum.indexOf(ServerInfo.MSG_DELIM);
        if (msgAccum.indexOf(ServerInfo.MSG_DELIM) != -1) {
            incomingMessages.add(msgAccum.substring(0, idx).trim());
            msgAccum.delete(0, idx + ServerInfo.MSG_DELIM.length());
            messageReady = true;
        } else {
            messageReady = false;
        }

        buffer.clear();
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public boolean isReady() {
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
