package org.cns.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.cns.api.server.ChatCommand;
import org.cns.api.server.ServerInfo;
import org.cns.server.commands.CountUsersCommand;
import org.cns.server.commands.HelpCommand;
import org.cns.server.commands.NickCommand;

import com.google.common.collect.EvictingQueue;

/**
 * Чат-сервер с основынм бесконечным циклом работы.
 * 
 * @author johnson
 *
 */
public class ChatServer implements Runnable {

    private static final Logger logger = Logger.getLogger(ChatServer.class);

    // канал сервера, прослушивающий входящие соединения
    private ServerSocketChannel serverSocketChanel;
    private Selector selector;

    // обработчик состояний каналов
    private ChannelStateProcessor stateProcessor;

    // зарегистрированные команды сервера
    private Map<String, ChatCommand> commands;

    // очередь для последние сообщения
    private Queue<String> lastMessages;

    private ServerInfo info;

    public ChatServer(String host, int port) throws IOException {
        this.selector = Selector.open();
        this.serverSocketChanel = ServerSocketChannel.open();
        this.serverSocketChanel.socket().bind(new InetSocketAddress(host, port));
        this.serverSocketChanel.configureBlocking(false);
        this.serverSocketChanel.register(selector, SelectionKey.OP_ACCEPT);

        this.stateProcessor = new ChannelStateProcessor();

        this.commands = new HashMap<String, ChatCommand>();

        this.commands.put("#help", new HelpCommand());
        this.commands.put("#nick", new NickCommand());
        this.commands.put("#count", new CountUsersCommand());

        this.lastMessages = EvictingQueue.create(100);

        this.info = new ServerInfo() {

            private Map<String, ChatCommand> readOnlyCommands = Collections.unmodifiableMap(commands);

            @Override
            public Selector getSelector() {
                return selector;
            }

            @Override
            public Map<String, ChatCommand> getCommands() {
                return readOnlyCommands;
            }

            @Override
            public Queue<String> getLastMessages() {
                return lastMessages;
            }

        };
    }

    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        try {
            while (this.serverSocketChanel.isOpen()) {
                selector.select();
                Iterator<SelectionKey> iter = this.selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isValid() && key.isAcceptable()) {
                        this.handleAccept(key);
                    }

                    if (key.isValid() && key.isReadable()) {
                        this.handleRead(key);
                    }

                    if (key.isValid() && key.isWritable()) {
                        this.handleWrite(key);
                    }

                    if (key.isValid()) {
                        this.handleBroadcast(key);
                    }

                }
            }

        } catch (Exception e) {
            logger.error("Error occured while running server: ", e);
        }
    }

    /**
     * Создание нового подключения
     * 
     * @param key
     * @throws IOException
     */
    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
        channel.configureBlocking(false);
        channel.register(key.selector(), SelectionKey.OP_READ, new ChannelState(channel, 20, info));

        InetSocketAddress addr = (InetSocketAddress) channel.getRemoteAddress();
        logger.info(String.format("Accepting new connection: ip: %s, remote port: %s",
                addr.getAddress().getHostAddress(), addr.getPort()));
    }

    /**
     * Чтение сообщений из канала, обработка на предмет команд и обновление состояния канала.
     * 
     * @param key
     * @throws IOException
     */
    private void handleRead(SelectionKey key) {
        try {
            ChannelState state = (ChannelState) key.attachment();
            int interestedOps = stateProcessor.processIncomingMessages(state);
            if (interestedOps > 0)
                key.interestOps(interestedOps);
        } catch (Exception e) {
            logger.error("Error reading channel - connection lost? Closing client channel.", e);
            key.cancel();
        }
    }

    /**
     * Отправка накопленных сообщений для канала
     * 
     * @param key
     * @throws IOException
     */
    public void handleWrite(SelectionKey key) throws IOException {
        try {
            ChannelState state = (ChannelState) key.attachment();
            int interestedOps = stateProcessor.processOutgoingMessages(state);
            if (interestedOps > 0)
                // key.interestOps(interestedOps);
                key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        } catch (Exception e) {
            logger.error("Error writing channel - connection lost? Closing client channel.", e);
            key.cancel();
        }
    }

    /**
     * Распределение широковещательных сообщений по каналам
     * 
     * @param key
     * @throws IOException
     */
    public void handleBroadcast(SelectionKey key) throws IOException {
        try {
            ChannelState state = (ChannelState) key.attachment();
            if (state != null) {
                Queue<String> broadcastMsgs = state.getBroadcastMessages();
                if (broadcastMsgs.size() > 0) {
                    String msg = broadcastMsgs.poll();
                    for (SelectionKey targetKey : selector.keys()) {
                        if (!targetKey.equals(key)) { // себе не пересылаем сообщение
                            if (targetKey.isValid() && targetKey.channel() instanceof SocketChannel) {
                                ChannelState targetState = (ChannelState) targetKey.attachment();
                                targetState.getOutMessages().add(msg);
                                targetKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error handle broadcast. Reason:", e);
        }
    }

}
