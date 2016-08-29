package org.cns.server;

import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

import org.cns.api.server.ChannelInfo;
import org.cns.api.server.MessageReader;
import org.cns.api.server.ServerInfo;

/**
 * Описание состояние канала - очереди входящих и исходящих сообщений, процессоры буферов ввода/вывода и т.п.
 * 
 * @author johnson
 *
 */
public class ChannelState implements ChannelInfo {

    // back ref to channel
    private SocketChannel channel;

    // back ref to server info
    private ServerInfo serverInfo;

    // ник пользователя
    private String nickname;

    // полученные входящие сообщения
    private Queue<String> inMessages;

    // исходящие сообщения
    private Queue<String> outMessages;

    // общие исходящие сообщения
    private Queue<String> broadcastMessages;

    // обработчик буфера для входящих сообщений
    private MessageReader reader;

    // обработчик буфера для исходящих сообщений
    private OutBufferProcessor outBufferProcessor;

    public ChannelState(SocketChannel channel, MessageReader reader, ServerInfo info) {
        this.channel = channel;
        this.inMessages = new ArrayDeque<String>();
        this.outMessages = new ArrayDeque<String>();
        this.broadcastMessages = new ArrayDeque<String>();
        this.reader = reader;
        this.outBufferProcessor = new OutBufferProcessor();
        this.serverInfo = info;
    }

    public MessageReader getMessageReader() {
        return reader;
    }

    public OutBufferProcessor getOutBufferState() {
        return outBufferProcessor;
    }

    public Queue<String> getInMessages() {
        return inMessages;
    }

    @Override
    public Queue<String> getOutMessages() {
        return outMessages;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public SocketChannel getChannel() {
        return channel;
    }

    @Override
    public ServerInfo getServerInfo() {
        return this.serverInfo;
    }

    public Queue<String> getBroadcastMessages() {
        return this.broadcastMessages;
    }

}
