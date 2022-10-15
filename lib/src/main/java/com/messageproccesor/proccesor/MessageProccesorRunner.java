package com.messageproccesor.proccesor;

import com.messageproccesor.model.HandlerProcessor;
import com.messageproccesor.model.RepositoryProcessor;
import com.messageproccesor.utils.LoggerMessageProccesor;
import lombok.Getter;

import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Level;

public class MessageProccesorRunner {
    private static ClassProccesor classProccesor=null;

    @Getter
    private static Map<Class<HandlerProcessor>, Set<RepositoryProcessor>> handlerProcessorGroupingrepositories=new HashMap<>();
    public static void run(Class  aClass){
        classProccesor=ClassProccesor.from(aClass.getClassLoader());
        makeGroupsHandlers();
    }
    private static void makeGroupsHandlers(){
        classProccesor.getResource().stream().filter(a->{
            try {
                Class cl=ClassLoader.getSystemClassLoader().loadClass(a.getResoucePath());
                return containsClassRecursive(cl,HandlerProcessor.class);
            } catch (ClassNotFoundException e) {
                return false;
            }
        }).forEach(a->{
            try {
                Class<?> aClass=URLClassLoader.getSystemClassLoader().loadClass(a.getResoucePath());
                if(!handlerProcessorGroupingrepositories.containsKey(aClass)){
                    handlerProcessorGroupingrepositories.put((Class<HandlerProcessor>) aClass,new HashSet<>());
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static boolean containsClassRecursive(Class<?> aClass,Class required){
        if(aClass.getTypeName().equals(required.getTypeName())){
            return true;
        }
        Class<?>[] interfaces=aClass.getInterfaces();
        for (Class<?> i:
                interfaces) {
            if(i.getTypeName().equals(required.getTypeName())){
                return true;
            }

            //sub interfaces
            if(i.getInterfaces().length>0)
                return containsClassRecursive(i,required);
        }
        return false;
    }
}
