package org.cns.server.commands.chat;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import org.cns.api.server.ChannelInfo;
import org.cns.api.server.ServerInfo;
import org.cns.api.server.commands.ChatCommand;
import org.cns.model.command.CommandInput;
import org.cns.model.command.ProcessingResult;

/**
 * Регистрация или смена ника. Команда выполняет проверку уникальности ника.
 * 
 * @author johnson
 *
 */
public class NickCommand implements ChatCommand {

    @Override
    public String getName() {
        return "#nick";
    }

    @Override
    public String getDescription() {
        return "Register own nickname";
    }

    @Override
    public boolean isPublic() {
        return false;
    }

    @Override
    public ProcessingResult execute(CommandInput input) {
        boolean changeAccepted = true;
        
        ChannelInfo ci = input.getChannelInfo();
        ServerInfo srvInfo = ci.getServerInfo();

        String nick = input.getMsg().substring(getName().length()).trim();

        if (nick.isEmpty()) {
            changeAccepted = false;
        } else {
            Selector selector = srvInfo.getSelector();
            for (SelectionKey targetKey : selector.keys()) {
                if (targetKey.isValid() && targetKey.channel() instanceof SocketChannel) {
                    ChannelInfo targetState = (ChannelInfo) targetKey.attachment();
                    if (nick.equals(targetState.getNickname()))
                        changeAccepted = false;
                }
            }
        }

        if (changeAccepted) {
            ci.setNickname(nick);
            ci.getOutMessages().add("Nickname accepted.");
            Queue<String> lastMsgs = srvInfo.getLastMessages();
            for (String lm : lastMsgs) {
                ci.getOutMessages().add(lm);
            }
        } else {
            ci.getOutMessages().add("Nickname rejected - try to choose another nickname.");
        }

        return ProcessingResult.NEXT;
    }

}
