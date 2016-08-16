package org.cns.api.server;

import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * Интерфейс для доступа к состоянию канала и состоянию сервера
 * 
 * @author johnson
 *
 */
public interface ChannelInfo {

    /**
     * Возвращает информацию о состоянии сервера
     * 
     * @return состояние сервера
     */
    public ServerInfo getServerInfo();

    /**
     * Возвращает канал, ассоциированный с состоянием
     * 
     * @return канал, ассоциированный с состоянием
     */
    public SocketChannel getChannel();

    /**
     * Возвращает очередь исходящих сообщений для канала
     * 
     * @return
     */
    public Queue<String> getOutMessages();

    /**
     * Возвращает никнейм пользователя, оссоциированный с состоянием
     * 
     * @return никнейм пользователя, оссоциированный с состоянием
     */
    public String getNickname();

    /**
     * Задает никнейм пользователя, оссоциированный с состоянием
     * 
     * @param nickname
     *            никнейм пользователя, оссоциированный с состоянием
     */
    public void setNickname(String nickname);

}
