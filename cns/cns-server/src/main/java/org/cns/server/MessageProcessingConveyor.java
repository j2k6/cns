package org.cns.server;

import java.util.ArrayList;
import java.util.List;

import org.cns.api.server.commands.CommandProcessor;
import org.cns.model.command.CommandInput;
import org.cns.model.command.ProcessingResult;

/**
 * Конвейер для обработки сообщений. В конвейере могут участвовать один или больше обработчиков команд. Порядок
 * обработки команд - в порядке добавления процессоров к конвейеру.
 * 
 * @author johnson
 *
 */
public class MessageProcessingConveyor {

    // перечень обработчиков команд
    private List<CommandProcessor<?>> processors;

    public MessageProcessingConveyor() {
        this.processors = new ArrayList<CommandProcessor<?>>();
    }

    public void addProcessor(CommandProcessor<?> processor) {
        this.processors.add(processor);
    }

    /**
     * Обработка сообщений по конвейеру из процессоров
     * 
     * @param message
     */
    public void processMessages(String message, ChannelState state) {
        CommandInput input = new CommandInput(message, state);
        for (CommandProcessor<?> processor : processors) {

            ProcessingResult result = processor.processCommand(input);

            while (result.equals(ProcessingResult.REPEAT)) {
                result = processor.processCommand(input);
            }

            if (result.equals(ProcessingResult.STOP))
                break;
        }
    }

}
