package de.danielprinz.hskl.nk.api.crypto;

import java.util.logging.*;

public class LoggerUtil {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static LoggerUtil instance = new LoggerUtil();
    private final Logger logger;
    private String prefix = "API";

    private LoggerUtil() {

        this.logger = Logger.getLogger(this.prefix);

        // set up the logger
        for(Handler handler : this.logger.getParent().getHandlers()) {
            this.logger.getParent().removeHandler(handler);
        }

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                String color;
                if(record.getLevel().intValue() <= Level.CONFIG.intValue())
                    color = ANSI_WHITE;
                else if(record.getLevel().intValue() == Level.INFO.intValue())
                    color = ANSI_PURPLE;
                else if(record.getLevel().intValue() == Level.WARNING.intValue())
                    color = ANSI_YELLOW;
                else
                    color = ANSI_RED;

                return color + "[" + prefix + "] [" + record.getLevel().getName() + "] " + record.getMessage() + ANSI_RESET + "\n";
            }
        });
        consoleHandler.setLevel(Level.ALL);
        this.logger.addHandler(consoleHandler);
        this.logger.setLevel(Level.ALL);

    }


    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setLogLevel(Level logLevel) {
        this.logger.setLevel(logLevel);
    }

    public static void log(Level level, String msg) {
        getInstance().logger.log(level, msg);
    }

    private static LoggerUtil getInstance() {
        return instance;
    }

}
