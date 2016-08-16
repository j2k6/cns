package org.cns.api.client;

import org.cns.model.ChatHistory;

/**
 * Состояние чат-клиента. Предназначен для хранения истории сообщений.
 * 
 * @author johnson
 *
 */
public interface ClientState {

    /**
     * Получить историю сообщений.
     * 
     * @return контейнер с историей сообщений
     */
    public ChatHistory getChatHistory();

}
