package org.cns.api.server;

import java.nio.ByteBuffer;
import java.util.Queue;

/**
 * Интерфейс парсера сообщений из входящего потока данных канала. Основная ответственность парсера - определять
 * очередное сообщение в потоке данных и складировать его в очередь разобранных сообщений.
 * 
 * @author ivanovd
 *
 */
public interface MessageReader {
    
    public ByteBuffer getBuffer();

    public void processBuffer(int bytesRead, Queue<String> incomingMessages);

    public boolean isMessageReady();

}
