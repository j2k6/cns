package org.cns.api.client;

import java.io.IOException;

/**
 * Интерфейс адаптера для низкоуровневого взаимодействия с чат-сервером.
 * 
 * @author johnson
 *
 */
public interface ServerAdapter {

    /**
     * Выполнить попытку соединения с указанным чат-сервером
     * 
     * @param host
     *            адрес или имя чат-сервера
     * @param port
     *            порт прослушиваемый чат-сервером
     * @throws IOException
     *             в случае ошибок из-за невозможности выполнить операцию подключени.
     */
    public void connect(String host, int port) throws IOException;

    /**
     * Закрыть соединение с сервером и освободить ресурсы.
     */
    public void disconnect();

    /**
     * Отправка данных на сервер.
     * 
     * @param data
     *            данные для отправки
     * @throws IOException
     *             при ошибках выполнить операцию передачи данных на чат-сервер
     */
    public void sendData(String data) throws IOException;

    /**
     * Получение сообщений с чат-сервера.
     * 
     * @return новое сообщение
     * @throws IOException
     *             при ошибках выполнить операцию передачи данных на чат-сервер
     */
    public String recieveData() throws IOException;

    /**
     * Проверка статуса адаптера.
     * 
     * @return true если можно выполнять операции приемки-передачи данных, false в других случаях
     */
    public boolean isOperational();
}
