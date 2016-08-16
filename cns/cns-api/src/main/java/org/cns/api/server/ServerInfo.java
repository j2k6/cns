package org.cns.api.server;

import java.nio.channels.Selector;
import java.util.Map;
import java.util.Queue;

/**
 * Описание состояние чат-сервера.
 * 
 * @author johnson
 *
 */
public interface ServerInfo {

    /**
     * Разделитель сообщений во входящем потоке данных для сервера,
     */
    public static final String MSG_DELIM = "\u0003";

    /**
     * Возвращает селектор доступных каналов, обслуживаемых чат-сервером.
     * 
     * @return селектор доступных каналов, обслуживаемых чат-сервером
     */
    public Selector getSelector();

    /**
     * Возвращает перечень команд, зарегистрированных на сервере.
     * 
     * @return перечень команд, зарегистрированных на сервере.
     */
    public Map<String, ChatCommand> getCommands();

    /**
     * Возвращает последние доступные сообщения.
     * 
     * @return
     */
    public Queue<String> getLastMessages();

}
