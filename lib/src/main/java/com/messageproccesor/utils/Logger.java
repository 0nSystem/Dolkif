package com.messageproccesor.utils;

public class Logger {
    private static java.util.logging.Logger logger;

    public static java.util.logging.Logger getLogger(){
        if(logger == null)
            logger = java.util.logging.Logger.getLogger(Logger.class.getName());

        return logger;
    }
}
