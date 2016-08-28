package org.cns.api.server.commands;

/**
 * Интерфейс для реализации команд чата
 * 
 * @author johnson
 *
 */
public interface ChatCommand extends Command {

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

}
