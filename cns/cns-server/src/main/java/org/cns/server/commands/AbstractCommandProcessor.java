package org.cns.server.commands;

import java.util.Collections;
import java.util.Map;

import org.cns.api.server.commands.Command;
import org.cns.api.server.commands.CommandProcessor;

/**
 * Базовый класс командного процессора
 * 
 * @author johnson
 *
 * @param <T>
 */
public abstract class AbstractCommandProcessor<T extends Command> implements CommandProcessor<T> {

    // зарегистрированные команды сервера
    private Map<String, T> commands;

    public AbstractCommandProcessor() {
        commands = createStorage();
    }

    @Override
    public Map<String, T> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    public void addCommand(T command) {
        this.commands.put(command.getName(), command);
    }

    protected abstract Map<String, T> createStorage();

}
