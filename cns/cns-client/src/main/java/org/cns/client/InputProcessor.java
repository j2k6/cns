package org.cns.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.cns.api.client.ClientProtocol;

/**
 * Обработчик данных, которые вводит пользователь. Данная реализация считывает данные из консоли.
 * 
 * @author johnson
 *
 */
public class InputProcessor implements Runnable {

    private static final Logger logger = Logger.getLogger(InputProcessor.class);

    private BufferedReader consoleReader;
    private ClientProtocol handler;

    /**
     * Конструктор.
     * 
     * @param handler
     *            реализация протокола чата прикладного уровня
     */
    public InputProcessor(ClientProtocol handler) {
        this.consoleReader = new BufferedReader(new InputStreamReader(System.in));
        this.handler = handler;
    }

    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        try {
            do {
                // если пользователь наберет магическую команду #quit - он выйдет из приложения
                String userInput = consoleReader.readLine();
                if (userInput.toLowerCase().startsWith("#quit")) {
                    handler.shutdown();
                } else {
                    handler.sendData(userInput);
                }
                Thread.sleep(100);
            } while (handler.isActive());

        } catch (Exception e) {
            handler.shutdown();
            logger.error("Error while trying to process user input", e);
        } finally {
            try {
                consoleReader.close();
            } catch (IOException e) {
                logger.error("Error while trying to close console input stream", e);
            }
        }
    }

}
