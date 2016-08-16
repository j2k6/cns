package org.cns.client;

import org.cns.api.client.ClientProtocol;
import org.cns.api.client.ServerAdapter;

/**
 * Реализация клиента чата. По факту - набор из двух ниток, одна читает сообщения от пользователя и отправляет
 * серверному адаптеру. Вторая - ждет сообщений от сервера, складывает в историю сообщений и показывает историю
 * пользователю.
 * 
 * @author johnson
 *
 */
public class ChatClient {

    private ClientProtocol handler;

    public ChatClient() {

    }

    public void init(ServerAdapter sa) {
        this.handler = new ClientProtocolHandler();
        this.handler.initialize(sa);

        // нитка для чтения входных данных от пользователя
        InputProcessor inputProcessor = new InputProcessor(this.handler);
        inputProcessor.start();

        // нитка для получения и отображения входящих сообщений от чат-сервера
        OutputProcessor outputProcessor = new OutputProcessor(this.handler);
        outputProcessor.start();
    }

    public ClientProtocol getHandler() {
        return handler;
    }

}
