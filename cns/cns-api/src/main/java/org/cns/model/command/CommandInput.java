package org.cns.model.command;

import org.cns.api.server.ChannelInfo;

/**
 * Описание данных, доступных для обработчиков команд.
 * 
 * @author johnson
 *
 */
public class CommandInput {

    /**
     * Сообщение с содержащейся командой
     */
    private String msg;

    /**
     * Ассоциированная с командой информация о канале, по которому пришла команда
     */
    private ChannelInfo channelInfo;

    /**
     * Конструктор
     * 
     * @param msg
     * @param channelInfo
     */
    public CommandInput(String msg, ChannelInfo channelInfo) {
        this.msg = msg;
        this.channelInfo = channelInfo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }

}
