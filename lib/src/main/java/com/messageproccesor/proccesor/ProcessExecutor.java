package com.messageproccesor.proccesor;


import com.messageproccesor.annotations.HeaderFilter;
import com.messageproccesor.model.IHandlerProcessor;
import com.messageproccesor.model.IObjetToProcessed;
import com.messageproccesor.model.IRepositoryProcessor;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class ProcessExecutor {

    public <T extends IObjetToProcessed> void  exec(T objetToProcessed) throws NullPointerException{

        Optional<Set<Class<IHandlerProcessor>>> handlerProcessorClass = UtilsProcessor.filterByContainGenericParams(objetToProcessed.getClass(),MessageProccesorRunner.getHandlerProcessorGroupingrepositories().keySet());
        if(handlerProcessorClass.isEmpty())
            return;

        Set<Class<IRepositoryProcessor>> repositories = MessageProccesorRunner.getHandlerProcessorGroupingrepositories()
                .get(handlerProcessorClass.get().stream()
                .findFirst().orElseThrow());
        if(repositories.isEmpty())
            return;

        Set<Class<IRepositoryProcessor>> repositoryFilter = repositories.stream()
                .filter(a->filterByAnnotationFilterHeader(objetToProcessed.getHeader(), a))
                .collect(Collectors.toSet());
        if(repositoryFilter.isEmpty())
            return;


        IHandlerProcessor handlerInstace = null;
        for (Class<IRepositoryProcessor> repositoryProcessorClass:
             repositoryFilter) {
            try{
                if(handlerInstace==null){
                    handlerInstace= (IHandlerProcessor<?>) Arrays.stream(handlerProcessorClass.get()
                                    .stream().findFirst().orElseThrow().getConstructors())
                            .filter(constructor->constructor.getParameterTypes().length==0)
                            .findFirst().orElseThrow()
                            .newInstance();
                }

                IRepositoryProcessor<?> iRepositoryProcessor= (IRepositoryProcessor<?>) Arrays.stream(repositoryProcessorClass.getConstructors())
                        .filter(repoConstruc->repoConstruc.getParameterTypes().length==0)
                        .findFirst().orElseThrow().newInstance();

                handlerInstace.executionProcess(iRepositoryProcessor,objetToProcessed);


            }catch (Exception e){

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
