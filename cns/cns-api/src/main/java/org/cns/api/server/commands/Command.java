package org.cns.api.server.commands;

import org.cns.model.command.CommandInput;
import org.cns.model.command.ProcessingResult;

/**
 * Команда, обрабатываемой сервером
 * 
 * @author johnson
 *
 */
public interface Command {

    /**
     * Возвращает имя команды. Должно быть уникальным в рамках одного комманд-процессора, в котором регистрируется
     * команда.
     * 
     * @return
     */
    public String getName();
    
    /**
     * Выполнение команды с указанными параметрами.
     * 
     * @param input
     */
    public ProcessingResult execute(CommandInput input);

}
