package org.cns.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;
import org.cns.api.client.ClientProtocol;
import org.cns.model.ChatHistory;

/**
 * Обработчик для отображения сообщений, приходящих от сервера. Данная реализация выводит данные на консоль.
 * 
 * @author johnson
 *
 */
public class OutputProcessor implements Runnable {

    private static final Logger logger = Logger.getLogger(OutputProcessor.class);

    private BufferedWriter consoleWriter;
    private ClientProtocol handler;

    /**
     * Конструктор
     * 
     * @param handler реализация протокола чата прикладного уровня 
     */
    public OutputProcessor(ClientProtocol handler) {
        this.consoleWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        this.handler = handler;
    }

    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        try {
            while (handler.isActive()) {
                handler.receiveData();
                ChatHistory ch = handler.getState().getChatHistory();
                synchronized (ch) {
                    for (String m : handler.getState().getChatHistory().getMessages()) {
                        consoleWriter.write(m);
                    }
                    consoleWriter.flush();
                    // here we have possibility to clear messages that was
                    // added while we write history to console - so in fact we
                    // need to sync operations on ChatHistory
                    ch.clear();
                }
                Thread.sleep(100);
            }
        } catch (Exception e) {
            handler.shutdown();
            logger.error("Error while running thread for printing data.", e);
        } finally {
            try {
                consoleWriter.close();
            } catch (IOException e) {
                logger.error("Error while trying to close console writer.", e);
            }
        }
    }

}
