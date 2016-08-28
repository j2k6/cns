package org.cns.server.commands.chat;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.cns.api.server.ServerInfo;
import org.cns.api.server.commands.ChatCommand;
import org.cns.model.ChatMessage;
import org.cns.model.command.CommandInput;
import org.cns.model.command.ProcessingResult;

/**
 * Команда подсчета кол-ва подсоединеных к серверу пользователей.
 * 
 * @author johnson
 *
 */
public class CountUsersCommand implements ChatCommand {

    @Override
    public String getName() {
        return "#count";
    }

    @Override
    public String getDescription() {
        return "Print number of connected users.";
    }

    @Override
    public boolean isPublic() {
        return false;
    }

    @Override
    public ProcessingResult execute(CommandInput input) {
        try {
            ServerInfo srvInfo = input.getChannelInfo().getServerInfo();
            Selector selector = srvInfo.getSelector();
            int counter = 0;
            for (SelectionKey targetKey : selector.keys()) {
                if (targetKey.isValid() && targetKey.channel() instanceof SocketChannel)
                    counter++;
            }

            ChatMessage msg = new ChatMessage(input.getChannelInfo().getNickname(),
                    String.format("Users connected: %s", counter));

            input.getChannelInfo().getOutMessages().add(msg.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return ProcessingResult.NEXT;
    }

}
