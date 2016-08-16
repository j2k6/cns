package org.cns.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.cns.api.client.ServerAdapter;
import org.cns.api.server.ServerInfo;

/**
 * Низкоуровневая логика работы с сокетами при взаимодействии с сервером.
 * 
 * @author johnson
 *
 */
public class TcpServerAdapter implements ServerAdapter {

    private static final Logger logger = Logger.getLogger(ChatClient.class);

    // лочки для синхронизации операций ввода-вывода с сокетом из разных ниток
    private Object readLock;
    private Object writeLock;

    private Socket serverSocket;

    private Reader reader;
    private Writer writer;

    public TcpServerAdapter() {
        this.readLock = new Object();
        this.writeLock = new Object();
    }

    protected Socket createSocket(String host, int port) throws UnknownHostException, IOException {
        return new Socket(host, port);
    }

    protected Reader createReader(InputStream is, String encoding) throws IOException {
        return new BufferedReader(new InputStreamReader(is, encoding));
    }

    protected Writer createWriter(OutputStream os, String encoding) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(os, encoding));
    }

    /**
     * Создает новое соединение с сервером. Дает закончить текущие операции ввода-вывода другим ниткам. WARN: Не
     * проверяет, было ли закрыто старое соединение.
     */
    @Override
    public synchronized void connect(String host, int port) throws UnknownHostException, IOException {
        synchronized (writeLock) {
            synchronized (readLock) {
                this.serverSocket = createSocket(host, port);

                this.reader = createReader(this.serverSocket.getInputStream(), "UTF-8");
                this.writer = createWriter(this.serverSocket.getOutputStream(), "UTF-8");
            }
        }
    }

    @Override
    public void sendData(String data) throws IOException {
        synchronized (writeLock) {
            this.writer.write(data);
            this.writer.write(ServerInfo.MSG_DELIM); // помечаем окончание очередного сообщения
            this.writer.flush();
        }
    }

    private char[] buffer = new char[1024];

    @Override
    public String recieveData() throws IOException {
        synchronized (readLock) {
            if (reader.ready()) {
                int charsRead = 0;
                StringBuffer message = new StringBuffer();
                while (reader.ready() && (charsRead = reader.read(buffer)) != -1) {
                    message.append(new String(buffer).substring(0, charsRead));
                }
                return message.toString();
            }
            return null;
        }
    }

    @Override
    public synchronized void disconnect() {
        synchronized (writeLock) {
            synchronized (readLock) {
                try {
                    reader.close();
                    writer.flush();
                    writer.close();
                    serverSocket.close();
                } catch (IOException e) {
                    logger.error(e);
                    reader = null;
                    writer = null;
                    serverSocket = null;
                }
            }
        }
    }

    @Override
    public boolean isOperational() {
        if (serverSocket.isBound() && serverSocket.isConnected() && !serverSocket.isClosed())
            return true;
        else
            return false;
    }

}
