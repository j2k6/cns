package org.cns.server;

import org.cns.api.server.MessageReader;
import org.cns.model.ServerType;

/**
 * Фабрика для создания парсеров сообщений.
 * 
 * @author ivanovd
 *
 */
public class MessageReaderFactory {

    public ServerType type;

    public MessageReaderFactory(ServerType type) {
        this.type = type;
    }

    public MessageReader create(int bufferSize) {
        if (type.equals(ServerType.HTTP))
            return new HttpMessageReader(bufferSize);
        else if (type.equals(ServerType.TCP))
            return new MarkeredMessageReader(bufferSize);
        else
            throw new RuntimeException("ServerType not supported");
    }

}
