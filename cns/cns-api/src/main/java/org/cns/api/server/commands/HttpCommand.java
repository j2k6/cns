package org.cns.api.server.commands;

public interface HttpCommand extends Command {

    public String getQueryString();

    public String getHost();

    public String getHttpVersion();

    public int getContentLenght();

}
