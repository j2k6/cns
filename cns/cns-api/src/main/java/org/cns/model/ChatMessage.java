package org.cns.model;

import java.util.Calendar;

/**
 * Враппер над текстовым сообщение. Используется в основном для форматирования исходящих сообщений
 * 
 * @author johnson
 *
 */
public class ChatMessage {

    private String dt;
    private String nickname;
    private String payload;

    @SuppressWarnings("deprecation")
    public ChatMessage(String nickname, String payload) {
        this.dt = Calendar.getInstance().getTime().toGMTString();
        this.nickname = nickname;
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public String getNickname() {
        return nickname;
    }

    public String getDt() {
        return dt;
    }

    public String toString() {
        return String.format("[%s] [%s] %s", getDt(), getNickname(), getPayload());
    }
}
