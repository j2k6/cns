package org.cns.server.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.cns.api.server.ChannelInfo;
import org.cns.api.server.commands.ChatCommand;
import org.cns.model.ChatMessage;
import org.cns.model.command.CommandInput;
import org.cns.model.command.ProcessingResult;
import org.cns.server.commands.chat.CountUsersCommand;
import org.cns.server.commands.chat.HelpCommand;
import org.cns.server.commands.chat.NickCommand;

/**
 * Обработчик команд чата - основная логика предметной области по обработке сообщений и команд
 * 
 * @author johnson
 *
 */
public class ChatCommandProcessor extends AbstractCommandProcessor<ChatCommand> {

    public ChatCommandProcessor() {
        super();
        addCommand(new HelpCommand() {

            @Override
            protected Collection<ChatCommand> getAvailiableCommands() {
                return getCommands().values();
            }

        });
        addCommand(new NickCommand());
        addCommand(new CountUsersCommand());
    }

    @Override
    protected Map<String, ChatCommand> createStorage() {
        return new HashMap<String, ChatCommand>();
    }

    /**
     * Обработчик сообщений в чат - каждое сообщение проверяется на наличие команды.
     * <p>
     * Если команды не было обнаружено - сообщение считается обычным и кладется в очередь общих исходящих сообщений
     * <p>
     * Выполняется дополнительная проверка регистрации пользователя - в случае если пользователь не был зарегистрирован
     * под незанятым ником - ему выдается соответствующее сообщение.
     * 
     * @param chatMsg
     */
    @Override
    public ProcessingResult processCommand(CommandInput input) {
        ChannelInfo state = input.getChannelInfo();

        Queue<String> outMsgs = state.getOutMessages();
        Queue<String> broadcastMsgs = state.getBroadcastMessages();
        Queue<String> lastMsgs = state.getServerInfo().getLastMessages();

        // проверка и обработка регистрации пользователя
        String rawMsg = input.getMsg();
        String command = extractCommand(rawMsg);

        if (state.getNickname() == null) {
            if ("#nick".equals(command)) {
                return getCommands().get("#nick").execute(input);
            } else {
                outMsgs.add("You have to register your nickname with #nick command. Type #quit to quit.");
                return ProcessingResult.NEXT;
            }
        } else {
            // пользователь зарегистрирован - обрабатываем его возможные команды
            ChatCommand commandExecutor = getCommands().get(command);
            if (commandExecutor != null) {
                return commandExecutor.execute(input);
            } else {
                // общее сообщение - добавляем к последним общим сообщения для новых пользователей и в очередь для
                // широковещательной рассылки уже подключенным
                ChatMessage chatMessage = new ChatMessage(state.getNickname(), rawMsg);
                String msg = chatMessage.toString();
                lastMsgs.add(msg);
                broadcastMsgs.add(msg);
            }
        }

        return ProcessingResult.NEXT;
    }

    /**
     * Возвращает команду в начале строки, если она там есть, иначе null. Команда начинается на #, после команды д.б.
     * пробел.
     *
     * @param message
     * @return
     */
    protected String extractCommand(String message) {
        String commandToCheck = null;
        if (message.startsWith("#")) {
            int cmdEnd = message.indexOf(' ');
            if (cmdEnd > 1) {
                commandToCheck = message.substring(0, cmdEnd);
            }
        }
        return commandToCheck;
    }
}
