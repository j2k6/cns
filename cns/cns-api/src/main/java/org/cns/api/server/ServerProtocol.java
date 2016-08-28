package org.cns.api.server;

import org.cns.api.server.commands.ChatCommand;
import org.cns.model.ChatMessage;

public interface ServerProtocol {
    
    public static final String MSG_DELIM = "\u0003";

	public void start();

	public void stop();

	public void processCommand(ChatCommand command);

	public void broadcastChatMessage(ChatMessage message);
}
