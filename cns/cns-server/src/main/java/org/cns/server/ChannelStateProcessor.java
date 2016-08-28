package org.cns.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * Логика обработки состояния канала
 * 
 * @author johnson
 *
 */
public class ChannelStateProcessor {

    /**
     * Конструктор
     */
    public ChannelStateProcessor() {
    }

    /**
     * Обработка входящих сообщений
     * 
     * @param channel
     * @param state
     * @throws IOException
     */
    public int processIncomingMessages(ChannelState state, MessageProcessingConveyor messageProcessingConveyor) throws IOException {
        SocketChannel channel = state.getChannel();
        InBufferProcessor inBuffProcessor = state.getInBufferProcessor();
        Queue<String> inMsgs = state.getInMessages();
        
        int bytesRead = channel.read(inBuffProcessor.getBuffer());
        if (bytesRead == -1) {
            channel.close();
        } else if (bytesRead > 0) {
            inBuffProcessor.processBuffer(bytesRead, inMsgs);
            // в результате обработки входящих данных канала получили как минимум одно сообщение - обрабатываем его
            // через процессор сообщений
            if (inBuffProcessor.isReady()) {
                String rawMsg = null;
                while ((rawMsg = inMsgs.poll()) != null) {
                    messageProcessingConveyor.processMessages(rawMsg, state);
                }
            }
            return SelectionKey.OP_READ | SelectionKey.OP_WRITE;
        }
        return 0;
    }

    /**
     * Обработка исходящих сообщений
     * 
     * @param state
     * @throws IOException
     */
    public int processOutgoingMessages(ChannelState state) throws IOException {
        int res = 0;
        SocketChannel channel = state.getChannel();

        Queue<String> msgQueue = state.getOutMessages();
        OutBufferProcessor outBufProcessor = state.getOutBufferState();

        // проверяем готовность буфера и наличие сообщений в очереди
        if (outBufProcessor.isReady()) {
            // буфер свободен и есть сообщения для отправки - отправляем их
            if (msgQueue.size() > 0) {
                channel.write(outBufProcessor.processBuffer(msgQueue));
            }
        } else {
            // если буфер занят - дописываем его остатки в канал
            channel.write(outBufProcessor.getReadyBuffer());
        }

        // если в очереди исходящих сообщений есть записи - продолжаем висеть в режиме отправки сообщений, иначе
        // переходим в режим приемки сообщений
        if (msgQueue.size() > 0) {
            res = SelectionKey.OP_WRITE;
        } else {
            res = SelectionKey.OP_READ;
        }

        return res;
    }

}
