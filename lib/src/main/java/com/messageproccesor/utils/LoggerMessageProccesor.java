package com.messageproccesor.utils;

import java.util.logging.Logger;

public class LoggerMessageProccesor{
    private static Logger logger;



    public static Logger getLogger(){
        if(logger==null)
            logger=Logger.getLogger(LoggerMessageProccesor.class.getName());
        return  logger;
    }
}
