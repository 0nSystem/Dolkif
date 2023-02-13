package org.dolkif.utils;

public class DolkifFactory {
    private static LoggerDolkif logger;

    public static LoggerDolkif getLogger(){
        if(logger == null)
            logger = new LoggerDolkif(ApplicationProperties.APPLICATION_NAME,ApplicationProperties.LOG_SEPARATOR);

        return logger;
    }
}
