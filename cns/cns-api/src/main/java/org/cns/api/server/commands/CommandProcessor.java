package org.cns.api.server.commands;

import java.util.Map;

import org.cns.model.command.CommandInput;
import org.cns.model.command.ProcessingResult;

/**
 * Интерфейс процессора команд
 * 
 * @author johnson
 *
 */
public interface CommandProcessor<T extends Command> {

    /**
     * Возвращает перечень команд, зарегистрированных на сервере.
     * 
     * @return перечень команд, зарегистрированных на сервере.
     */
    public Map<String, T> getCommands();

    /**
     * Добавить команду к перечню обрабатываемых процессором
     * 
     * @param command
     */
    public void addCommand(T command);

    /**
     * Обработка команды
     * 
     * @param input
     * @return
     */
    public ProcessingResult processCommand(CommandInput input);

}
