package org.cns.server.commands.http;

import org.cns.api.server.commands.HttpCommand;
import org.cns.model.command.CommandInput;
import org.cns.model.command.ProcessingResult;

public class PostCommand implements HttpCommand {

    @Override
    public String getName() {
        return "POST";
    }

    @Override
    public ProcessingResult execute(CommandInput input) {
        // достаем тело запроса
        String rawMsg = input.getMsg();
        int delimiterIdx = rawMsg.indexOf("\r\n\r\n");
        String body = rawMsg.substring(delimiterIdx + 4);

        input.setMsg(body);

        return ProcessingResult.NEXT;
    }

    @Override
    public String getQueryString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getHost() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getHttpVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getContentLenght() {
        // TODO Auto-generated method stub
        return 0;
    }

}
