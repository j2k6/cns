package org.cns.server.commands.http;

import org.cns.api.server.commands.HttpCommand;
import org.cns.model.command.CommandInput;
import org.cns.model.command.ProcessingResult;

/**
 * GET-запрос
 * 
 * @author johnson
 *
 */
public class GetCommand implements HttpCommand {

    @Override
    public String getName() {
        return "GET";
    }

    @Override
    public ProcessingResult execute(CommandInput input) {
        // делаем что-то с телом запроса - строго говоря у ГЕТ-запроса нет тела, надо разбирать URI
        String rawMsg = input.getMsg();

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
