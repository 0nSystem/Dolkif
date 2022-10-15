package com.messageproccesor.proccesor;

import com.messageproccesor.annotations.Qualify;
import com.messageproccesor.exceptions.ExceptionHandlerNotCompatibleWithRepository;
import com.messageproccesor.model.HandlerProcessor;
import com.messageproccesor.model.RepositoryProcessor;
import com.messageproccesor.utils.LoggerMessageProccesor;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Level;

public class MessageProccesorRunner {
    private static ClassProccesor classProccesor=null;

    @Getter
    private static Map<Class<HandlerProcessor>, Set<Class<RepositoryProcessor>>> handlerProcessorGroupingrepositories=new HashMap<>();
    public static void run(Class  aClass){
        classProccesor=ClassProccesor.from(aClass.getClassLoader());
        makeGroupsHandlers();
        makeRepositoriesInGroup();
    }
    private static void makeGroupsHandlers(){
        classProccesor.getResource().stream().filter(a->{
            try {
                Class cl=URLClassLoader.getSystemClassLoader().loadClass(a.getResoucePath());
                return containsInterface(cl,HandlerProcessor.class,true);
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
    private static void makeRepositoriesInGroup(){
        classProccesor.getResource().stream().filter(a->{
            try {
                Class cl=URLClassLoader.getSystemClassLoader().loadClass(a.getResoucePath());
                return containsInterface(cl, RepositoryProcessor.class,true);
            } catch (ClassNotFoundException e) {
                return false;
            }
        }).forEach(a->{
            try {
                Class<?> aClass=URLClassLoader.getSystemClassLoader().loadClass(a.getResoucePath());
                boolean compatible=false;
                Type[] typesAClass=aClass.getGenericInterfaces();
                Class<?> classfilterQualify=null;

                for (Annotation annotation:
                    aClass.getAnnotations()) {
                    if(annotation.annotationType().equals(Qualify.class)){
                        Qualify qualify=(Qualify) annotation;
                        Optional<Class<HandlerProcessor>> optionalClass=handlerProcessorGroupingrepositories.keySet().stream().filter(handlerProcessorClass -> handlerProcessorClass.equals(qualify.name())).findFirst();
                        if(optionalClass.isPresent()){
                            classfilterQualify=optionalClass.get();
                            break;
                        }

                    }
                }

                if(classfilterQualify==null){
                    for (Class<?> handler:
                        handlerProcessorGroupingrepositories.keySet()) {

                        Type[] typesHandler=handler.getGenericInterfaces();
                        for (Type typeHandler:
                             typesHandler) {
                            Optional<Type> type=Arrays.stream(typesAClass).filter(typeClass->typeClass.getTypeName().equals(typeHandler.getTypeName())).findFirst();
                            if(type.isPresent()){
                                classfilterQualify=handler;
                                break;
                            }
                        }
                    }
                }

                if(classfilterQualify==null){
                    throw new ExceptionHandlerNotCompatibleWithRepository();
                }

                handlerProcessorGroupingrepositories.get(classfilterQualify).add((Class<RepositoryProcessor>) aClass);
            } catch (ClassNotFoundException|ExceptionHandlerNotCompatibleWithRepository e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     *
     * @param aClass
     * @param required
     * @param notGetBasicInterface represent first iteration to delete base interface and get all class implement interface
     * @return
     */
    private static boolean containsInterface(Class<?> aClass,Class required,boolean notGetBasicInterface){
        if(aClass.getTypeName().equals(required.getTypeName())){
            if(notGetBasicInterface)
                return false;
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
                return containsInterface(i,required,false);
        }
        return false;
    }

}
