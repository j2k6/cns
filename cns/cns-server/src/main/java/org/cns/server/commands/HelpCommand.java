package org.cns.server.commands;

import org.cns.api.server.ChannelInfo;
import org.cns.api.server.ChatCommand;
import org.cns.api.server.ServerInfo;
import org.cns.model.ChatMessage;
import org.cns.model.command.CommandInput;

/**
 * Команда получения помощи по зарегистрированным на сервере командам
 * 
 * @author johnson
 *
 */
public class HelpCommand implements ChatCommand {

    @Override
    public String getName() {
        return "#help";
    }

    @Override
    public String getDescription() {
        return "Prints help message.";
    }

    @Override
    public boolean isPublic() {
        return false;
    }

    @Override
    public void execute(CommandInput input) {
        ChannelInfo ci = input.getChannelInfo();
        ServerInfo srvInfo = ci.getServerInfo();

        StringBuffer buf = new StringBuffer("Availiable commands:\r\n");
        for (ChatCommand command : srvInfo.getCommands().values()) {
            buf.append(String.format("Command: %s, desc: %s \r\n", command.getName(), command.getDescription()));
        }

        ChatMessage msg = new ChatMessage(input.getChannelInfo().getNickname(), buf.toString());

        input.getChannelInfo().getOutMessages().add(msg.toString());
    }

}
