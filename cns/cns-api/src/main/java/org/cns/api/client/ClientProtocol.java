package org.cns.api.client;

/**
 * Описание протокола прикладного уровня с т.з. чат-клиента.
 * 
 * @author johnson
 *
 */
public interface ClientProtocol {

    /**
     * Инициализация протокола.
     * 
     * @param adapter
     *            адаптер для чат-сервера
     */
    public void initialize(ServerAdapter adapter);

    /**
     * Завершение работы протокола.
     */
    public void shutdown();

    /**
     * Проверка состояния протокола. Получать и отправлять сообщения можно в активном состоянии.
     * 
     * @return true если протокол и адаптер иницализирован и готов к работе, false в случае неготовности протокола
     */
    public boolean isActive();

    /**
     * Отправка данных на чат-сервер.
     * 
     * @param input
     *            текст сообщения
     */
    public void sendData(String input);

    /**
     * Приемка данных от чат-сервера. Данные складываются в историю сообщений в контейнер состояния чат-клиента.
     */
    public void receiveData();

    /**
     * Клиентское состояние.
     * 
     * @return клиентское состояние с историей сообщений
     */
    public ClientState getState();
}
