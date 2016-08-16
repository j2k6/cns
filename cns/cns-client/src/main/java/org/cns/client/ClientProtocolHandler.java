package org.cns.client;

import org.apache.log4j.Logger;
import org.cns.api.client.ClientProtocol;
import org.cns.api.client.ClientState;
import org.cns.api.client.ServerAdapter;

/**
 * Реализация клиентского протокола - высокоуровневая логика работы с сообщениями и историей сообщений.
 * 
 * @author johnson
 *
 */
public class ClientProtocolHandler implements ClientProtocol {

    private static final Logger logger = Logger.getLogger(ClientProtocolHandler.class);

    // состояние хэндлера - нужно для того, чтобы можно было корректно завершать
    // операции в input/output процессорах
    private boolean active = false;

    // абстракция для хранения состояния клиентаа - история чата и т.п.
    private ClientState state;

    // адаптер сервера с которым идет работа
    private ServerAdapter adapter;

    public ClientProtocolHandler() {
        this.state = new ClientStateImpl();
    }

    @Override
    public void initialize(ServerAdapter adapter) {
        this.adapter = adapter;
        this.active = true;
    }

    @Override
    public void shutdown() {
        this.active = false;
        adapter.disconnect();
    }

    @Override
    public boolean isActive() {
        return this.active && this.adapter.isOperational();
    }

    @Override
    public void sendData(String input) {
        try {
            adapter.sendData(input);
        } catch (Exception e) {
            shutdown();
            logger.error("Error sending data to server.", e);
        }
    }

    @Override
    public void receiveData() {
        try {
            String msg = adapter.recieveData();
            if (msg != null) {
                state.getChatHistory().addMessage(msg);
            }
        } catch (Exception e) {
            shutdown();
            logger.error("Error recieving data from server.", e);
        }
    }

    @Override
    public ClientState getState() {
        return state;
    }

}
