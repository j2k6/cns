package org.cns.server.commands;

import java.util.HashMap;
import java.util.Map;

import org.cns.api.server.commands.HttpCommand;
import org.cns.model.command.CommandInput;
import org.cns.model.command.ProcessingResult;
import org.cns.server.commands.http.GetCommand;
import org.cns.server.commands.http.PostCommand;

/**
 * Командный процессор HTTP-команд (GET, POST, и т.п.)
 * 
 * @author johnson
 *
 */
public class HttpCommandProcessor extends AbstractCommandProcessor<HttpCommand> {

    public HttpCommandProcessor() {
        super();
        addCommand(new GetCommand());
        addCommand(new PostCommand());
    }

    @Override
    protected Map<String, HttpCommand> createStorage() {
        return new HashMap<String, HttpCommand>();
    }

    @Override
    public ProcessingResult processCommand(CommandInput input) {
        String rawMsg = input.getMsg();

        if (rawMsg.startsWith("GET "))
            return getCommands().get("GET").execute(input);
        else if (rawMsg.startsWith("POST "))
            return getCommands().get("POST").execute(input);

        return ProcessingResult.NEXT;
    }

}
