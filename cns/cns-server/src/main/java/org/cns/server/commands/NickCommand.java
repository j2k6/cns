package org.cns.server.commands;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import org.cns.api.server.ChannelInfo;
import org.cns.api.server.ChatCommand;
import org.cns.api.server.ServerInfo;
import org.cns.model.command.CommandInput;
import org.cns.server.ChannelState;

/**
 * Регистрация или смена ника. Команда выполняет проверку уникальности ника.
 * 
 * @author ivanovd
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
    public void execute(CommandInput input) {
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
                    ChannelState targetState = (ChannelState) targetKey.attachment();
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
    }

}
