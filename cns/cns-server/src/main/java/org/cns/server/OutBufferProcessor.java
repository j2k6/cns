package org.cns.server;

import java.nio.ByteBuffer;
import java.util.Queue;

/**
 * Процессинг сообщений в буфер для исходящих сообщений 
 * 
 * @author ivanovd
 *
 */
public class OutBufferProcessor {

    private ByteBuffer buffer;

    public OutBufferProcessor() {

    }

    public ByteBuffer processBuffer(Queue<String> outgoingMessages) {
        if (isReady()) {
            if (outgoingMessages.size() > 0) {
                StringBuilder msg = new StringBuilder(outgoingMessages.poll()).append("\r\n");
                buffer = ByteBuffer.wrap(msg.toString().getBytes());
            }
        }
        return buffer;
    }

    public ByteBuffer getReadyBuffer() {
        if (buffer.hasRemaining())
            buffer.compact();
        return this.buffer;
    }

    /**
     * Проверка готовности обработать новое сообщение из очереди сообщений
     * 
     * @return
     */
    public boolean isReady() {
        if (buffer != null)
            return !buffer.hasRemaining();
        else
            return true;
    }

}
