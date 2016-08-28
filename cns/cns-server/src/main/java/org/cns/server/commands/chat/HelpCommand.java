package org.cns.server.commands.chat;

import java.util.Collection;

import org.cns.api.server.ChannelInfo;
import org.cns.api.server.commands.ChatCommand;
import org.cns.model.ChatMessage;
import org.cns.model.command.CommandInput;
import org.cns.model.command.ProcessingResult;

/**
 * Команда получения помощи по зарегистрированным на сервере командам
 * 
 * @author johnson
 *
 */
public abstract class HelpCommand implements ChatCommand {

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
    public ProcessingResult execute(CommandInput input) {
        ChannelInfo channelInfo = input.getChannelInfo();

        StringBuffer buf = new StringBuffer("Availiable commands:\r\n");
        for (ChatCommand command : getAvailiableCommands()) {
            buf.append(String.format("Command: %s, desc: %s \r\n", command.getName(), command.getDescription()));
        }

        ChatMessage msg = new ChatMessage(input.getChannelInfo().getNickname(), buf.toString());
        channelInfo.getOutMessages().add(msg.toString());

        return ProcessingResult.NEXT;
    }

    protected abstract Collection<ChatCommand> getAvailiableCommands();

}
