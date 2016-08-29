package test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.cns.api.server.ChannelInfo;
import org.cns.api.server.ServerInfo;
import org.cns.api.server.commands.ChatCommand;
import org.cns.model.command.CommandInput;
import org.cns.server.commands.ChatCommandProcessor;
import org.cns.server.commands.chat.HelpCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.EvictingQueue;

public class HelpCommandTest {

    Selector selector;
    
    SelectionKey key;
    Set<SelectionKey> keys;
    
    SocketChannel socketChannel;

    ServerInfo serverInfo;
    ChannelInfo channelInfo;

    String nick;

    Queue<String> bcMsgs = new ArrayDeque<String>();
    Queue<String> outMsgs = new ArrayDeque<String>();
    Queue<String> lastMsgs = EvictingQueue.create(100);

    CommandInput cmd;
    
    @Before
    public void setUp() throws Exception {
        
        
        key = new SelectionKey() {
            
            @Override
            public SelectableChannel channel() {
                return socketChannel;
            }

            @Override
            public Selector selector() {
                return null;
            }

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public void cancel() {
            }

            @Override
            public int interestOps() {
                return 0;
            }

            @Override
            public SelectionKey interestOps(int ops) {
                return null;
            }

            @Override
            public int readyOps() {
                return 0;
            }
            
        };
        
        keys = new HashSet<SelectionKey>();
        keys.add(key);
        
        selector = mock(Selector.class);
        when(selector.keys()).thenReturn(keys);
        
        socketChannel = mock(SocketChannel.class);
        
        serverInfo = new ServerInfo() {

            @Override
            public Selector getSelector() {
                return selector;
            }

            @Override
            public Queue<String> getLastMessages() {
                return lastMsgs;
            }
            
        };
        
        channelInfo = new ChannelInfo() {

            @Override
            public ServerInfo getServerInfo() {
                return serverInfo;
            }

            @Override
            public SocketChannel getChannel() {
                return socketChannel;
            }

            @Override
            public Queue<String> getOutMessages() {
                return outMsgs;
            }

            @Override
            public Queue<String> getBroadcastMessages() {
                return bcMsgs;
            }

            @Override
            public String getNickname() {
                return nick;
            }

            @Override
            public void setNickname(String nickname) {
                nick = nickname;
            }
            
        };
        
        key.attach(channelInfo);
        
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Проверка на выполнение команды помощи - в исходящих сообщениях д.б информация о доступных командах.
     */
    @Test
    public void testHelpCommand() {
        
        this.nick = "testUser";
        
        // создаем экземпляр - только для того, чтобы получить массив доступных команд
        ChatCommandProcessor ccp = new ChatCommandProcessor();
        
        CommandInput input = new CommandInput("#help", channelInfo);
        HelpCommand cmd = new HelpCommand() {

            @Override
            protected Collection<ChatCommand> getAvailiableCommands() {
                return ccp.getCommands().values();
            }
            
        };
        cmd.execute(input);
        
        String msg = outMsgs.poll();
        
        assertEquals(msg.contains("Availiable commands"), true);
    }

}
