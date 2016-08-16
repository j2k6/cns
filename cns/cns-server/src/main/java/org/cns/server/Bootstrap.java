package org.cns.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.cns.model.ServerType;

/**
 * Инициализация сервера
 * 
 * @author johnson
 *
 */
public class Bootstrap {

    private static final Logger logger = Logger.getLogger(Bootstrap.class);

    private static ServerType type;
    private static String host;
    private static int port;

    public static void main(String[] args) throws Exception {

        // инициализируем параметры командной строки
        Options options = initCliOptions();

        // нет параметров в командной строке - выводим справку
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("cns-server", options);
            System.exit(0);
        }

        // парсим параметры командной строки
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        logger.info("Command line parameters parsed...");

        type = ServerType.valueOf(cmd.getOptionValue("t"));
        if (!type.equals(ServerType.TCP)) {
            logger.error("Only TCP server type supported for now.");
            System.exit(-1);
        }

        String[] hostAndPort = cmd.getOptionValues("h");
        host = hostAndPort[0];
        port = Integer.valueOf(hostAndPort[1]);

        // если добрались до этой точки - значит хватает данных для
        // инициализации подсистем
        initAndRunChat();
    }

    /**
     * Задаем перечень параметров командной строки
     * 
     * @return
     */
    private static Options initCliOptions() {
        Options options = new Options();

        options.addOption(Option.builder("t").argName("type").hasArgs().numberOfArgs(1).required()
                .desc("Chat server type - for example, HTTP or TCP. NOTE: Only TCP support implemented for now.")
                .build());
        options.addOption(Option.builder("h").argName("host:port").hasArgs().numberOfArgs(2).valueSeparator(':')
                .desc("Chat server host including port in host:port format - for example, mychatserver:3804.")
                .required().build());

        return options;
    }

    /**
     * Инициализация подсистем чата
     */
    private static void initAndRunChat() {
        try {
            logger.info("Trying to initilize chat server...");

            ChatServer chatServer = new ChatServer(host, port);
            chatServer.start();

            logger.info("Chat server initialized.");

        } catch (Exception e) {
            logger.error(e);
        }
    }

}
