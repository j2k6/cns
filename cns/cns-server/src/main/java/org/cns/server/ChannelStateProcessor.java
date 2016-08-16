package org.cns.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

import org.cns.api.server.ChatCommand;
import org.cns.model.ChatMessage;
import org.cns.model.command.CommandInput;

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
    public int processIncomingMessages(ChannelState state) throws IOException {
        SocketChannel channel = state.getChannel();
        InBufferProcessor inBuffProcessor = state.getInBufferProcessor();
        int bytesRead = channel.read(inBuffProcessor.getBuffer());
        if (bytesRead == -1) {
            channel.close();
        } else if (bytesRead > 0) {
            inBuffProcessor.processBuffer(bytesRead, state.getInMessages());
            // в результате обработки входящих данных канала получили как минимум одно сообщение - обрабатываем его
            // через процессор команд
            if (inBuffProcessor.isReady()) {
                processCommands(state);
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

    /**
     * Обработчик очереди входящих сообщений - каждое сообщение проверяется на наличие команды.
     * <p>
     * Если команды не было обнаружено - сообщение считается обычным и кладется в очередь общих исходящих сообщений
     * <p>
     * Выполняется дополнительная проверка регистрации пользователя - в случае если пользователь не был зарегистрирован
     * под незанятым ником - ему выдается соответствующее сообщение.
     * 
     * @param chatMsg
     */
    public void processCommands(ChannelState state) {
        Queue<String> inMsgs = state.getInMessages();
        Queue<String> outMsgs = state.getOutMessages();
        Queue<String> broadcastMsgs = state.getBroadcastMessages();
        Queue<String> lastMsgs = state.getServerInfo().getLastMessages();

        Map<String, ChatCommand> commands = state.getServerInfo().getCommands();

        String rawMsg = null;
        while ((rawMsg = inMsgs.poll()) != null) {

            // проверка и обработка регистрации пользователя
            if (state.getNickname() == null) {
                if (rawMsg.startsWith("#nick")) {
                    CommandInput input = new CommandInput(rawMsg, state);
                    commands.get("#nick").execute(input);
                } else {
                    outMsgs.add("You have to register your nickname with #nick command. Type #quit to quit.");
                }
            } else {
                // пользователь зарегистрирован - обрабатываем его возможные команды
                if (rawMsg.startsWith("#")) {

                    for (ChatCommand cmd : commands.values()) {
                        if (rawMsg.startsWith(cmd.getName())) {
                            CommandInput input = new CommandInput(rawMsg, state);
                            cmd.execute(input);
                            break;
                        }
                    }

                } else {
                    // общее сообщение - добавляем к последним общим сообщения для новых пользователей и в очередь для
                    // широковещательной рассылки уже подключенным
                    ChatMessage chatMessage = new ChatMessage(state.getNickname(), rawMsg);
                    String msg = chatMessage.toString();
                    lastMsgs.add(msg);
                    broadcastMsgs.add(msg);
                }
            }
        }
    }

}
