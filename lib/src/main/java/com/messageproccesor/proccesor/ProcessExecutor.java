package com.messageproccesor.proccesor;


import com.messageproccesor.annotations.HeaderFilter;
import com.messageproccesor.model.IHandlerProcessor;
import com.messageproccesor.model.IObjetToProcessed;
import com.messageproccesor.model.IRepositoryProcessor;
import com.messageproccesor.utils.LoggerMessageProccesor;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ProcessExecutor {

    public static <T extends IObjetToProcessed> void  exec(T objetToProcessed) throws NullPointerException{

        Optional<Set<Class<IHandlerProcessor>>> handlerProcessorClass = UtilsProcessor.filterByContainGenericParams(objetToProcessed.getClass(),MessageProccesorRunner.getHandlerProcessorGroupingrepositories().keySet());
        if(handlerProcessorClass.isEmpty())
            throw new NullPointerException("IHandlerProcessor is null");

        Set<Class<IRepositoryProcessor>> repositories = MessageProccesorRunner.getHandlerProcessorGroupingrepositories()
                .get(handlerProcessorClass.get().stream()
                .findFirst().orElseThrow());
        if(repositories.isEmpty())
            throw new NullPointerException("IRepositoryProcessor is null");

        Set<Class<IRepositoryProcessor>> repositoryFilter = repositories.stream()
                .filter(a->filterByAnnotationFilterHeader(objetToProcessed.getHeader(), a))
                .collect(Collectors.toSet());
        if(repositoryFilter.isEmpty())
            throw new NullPointerException("IRepositoryProcessor compatible with HandlerProcessor is null");

        injectRepositoryInHandlerAndExecProcces(
                handlerProcessorClass.orElseThrow(() -> new NullPointerException("IHandlerProcessor is null"))
                        .stream().findFirst().orElseThrow(() -> new NullPointerException("IHandlerProcessor is null")),
                repositoryFilter ,
                objetToProcessed
        );

    }

    private static <T extends IObjetToProcessed> void injectRepositoryInHandlerAndExecProcces(Class<IHandlerProcessor> iHandlerProcessorClass,Set<Class<IRepositoryProcessor>> iRepositoryProccesor,T objetToProcessed){
        IHandlerProcessor handlerInstace = null;
        for (Class<IRepositoryProcessor> repositoryProcessorClass:
                iRepositoryProccesor) {
            try{
                if(handlerInstace==null){
                    handlerInstace= (IHandlerProcessor) Arrays.stream(iHandlerProcessorClass.getConstructors())
                            .filter(constructor->constructor.getParameterTypes().length == 0)
                            .findFirst().orElseThrow()
                            .newInstance();
                }

                IRepositoryProcessor<?> iRepositoryProcessor= (IRepositoryProcessor<?>) Arrays.stream(repositoryProcessorClass.getConstructors())
                        .filter(repoConstruc->repoConstruc.getParameterTypes().length == 0)
                        .findFirst().orElseThrow().newInstance();

                handlerInstace.executionProcess(iRepositoryProcessor,objetToProcessed);

            }catch (Exception e){
                LoggerMessageProccesor.getLogger().log(Level.WARNING,"ProcessExecutor can´t executed method executionProcess" , e);
            }
        }
    }

    private static boolean filterByAnnotationFilterHeader(String header,Class<?> aClass){
        for (Annotation annotation:
                aClass.getAnnotations()) {

            if(annotation.annotationType().equals(HeaderFilter.class)){
                HeaderFilter headerFilter = (HeaderFilter) annotation;
                if(headerFilter.header().equals(header))
                    return true;
            }
        }
        return false;
    }


}
