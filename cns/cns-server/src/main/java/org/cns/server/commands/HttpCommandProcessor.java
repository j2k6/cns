package org.cns.server.commands;

import java.util.HashMap;
import java.util.Map;

import org.cns.api.server.commands.HttpCommand;

/**
 * Командный процессор HTTP-команд (GET, POST, и т.п.)
 * @author johnson
 *
 */
public class HttpCommandProcessor extends AbstractCommandProcessor<HttpCommand> {

    public HttpCommandProcessor() {
        super();
    }

    @Override
    protected Map<String, HttpCommand> createStorage() {
        return new HashMap<String, HttpCommand>();
    }

}
