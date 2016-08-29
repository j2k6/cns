package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

import org.cns.client.TcpServerAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TcpServerAdapterTest {

    TcpServerAdapter adapter;
    Socket socket;

    String forReader = "Reading test";
    String forWriter = "Writing test";

    @Before
    public void setUp() throws Exception {
        adapter = new TcpServerAdapter() {

            @Override
            protected Socket createSocket(String host, int port) throws UnknownHostException, IOException {
                return socket;
            }

            @Override
            protected Reader createReader(InputStream is, String encoding) throws IOException {
                return new StringReader(forReader);
            }

            @Override
            protected Writer createWriter(OutputStream os, String encoding) throws IOException {
                return new StringWriter();
            }
        };

        socket = mock(Socket.class);
        adapter.connect("127.0.0.1", 5555);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConnect() {
        when(socket.isBound()).thenReturn(true);
        when(socket.isConnected()).thenReturn(true);
        when(socket.isClosed()).thenReturn(false);

        assertEquals(adapter.isOperational(), true);
    }

    @Test
    public void testSendData() {
        try {
            String v = "Writing test";
            adapter.sendData(v);
            assertEquals(v, forWriter);
        } catch (IOException e) {
            fail("testSendData failed." + e.getMessage());
        }
    }

    @Test
    public void testRecieveData() {
        try {
            String msg = adapter.recieveData();
            assertEquals(msg, forReader);
        } catch (IOException e) {
            fail("testSendData failed." + e.getMessage());
        }
    }

    @Test
    public void testDisconnect() {

        when(socket.isClosed()).thenReturn(true);
        when(socket.isBound()).thenReturn(false);
        when(socket.isConnected()).thenReturn(false);

        adapter.disconnect();

        assertEquals(adapter.isOperational(), false);
    }

}
