package org.cns.api.server;

import org.cns.model.command.CommandInput;

/**
 * Интерфейс для реализации команд чата
 * 
 * @author johnson
 *
 */
public interface ChatCommand {

    /**
     * Возвращат имя команды. Например #nick
     * 
     * @return
     */
    public String getName();

    /**
     * Возвращает текстовое описание команды
     * 
     * @return
     */
    public String getDescription();

    /**
     * Признак приватной или общей команды. TODO: на текущий момент логика обработки команды не использует данный
     * признак
     * 
     * @return
     */
    public boolean isPublic();

    /**
     * Выполнение команды с указанными параметрами.
     * 
     * @param input
     */
    public void execute(CommandInput input);

}
