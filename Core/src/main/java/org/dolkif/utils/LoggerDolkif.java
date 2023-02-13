package org.dolkif.utils;

import lombok.Getter;
import lombok.NonNull;


import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerDolkif {

    @Getter
    private final Logger logger;
    private String loggerSeparator = "<- | ->";

    protected LoggerDolkif(final @NonNull String nameLogger,final String loggerSeparator){
        this.logger = Logger.getLogger(nameLogger);
        if(loggerSeparator != null && !loggerSeparator.isBlank())
            this.loggerSeparator = loggerSeparator;
    }

    public void log(final @NonNull Level level, final @NonNull String message){
        logger.log(level,String.format("%s %s %s", ApplicationProperties.APPLICATION_NAME, loggerSeparator,message));
    }
    public void log(final @NonNull Level level, final @NonNull String message, final @NonNull Throwable ex){
        logger.log(level,String.format("%s %s %s %s \n %s", ApplicationProperties.APPLICATION_NAME, loggerSeparator,message, loggerSeparator, ex.getMessage()));
    }
}
