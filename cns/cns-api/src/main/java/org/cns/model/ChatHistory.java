package org.cns.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Контейнер для хранения данных чата, используется на клиенте
 * 
 * @author johnson
 *
 */
public class ChatHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    private Object writeLock;

    private List<String> storage;

    public ChatHistory() {
        this.writeLock = new Object();
        this.storage = new ArrayList<String>();
    }

    public void addMessage(String message) {
        synchronized (writeLock) {
            this.storage.add(message);
        }
    }

    public void addAll(ChatHistory history) {
        synchronized (writeLock) {
            this.storage.addAll(history.getMessages());
        }
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(storage);
    }

    public void clear() {
        synchronized (writeLock) {
            storage.clear();
        }
    }

}
