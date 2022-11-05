package com.messageproccesor.proccesor;


import com.messageproccesor.enums.PatternScope;
import com.messageproccesor.exceptions.NotSupportException;
import com.messageproccesor.model.IServiceProccesor;
import com.messageproccesor.model.IObjetToProcessed;
import com.messageproccesor.model.IRepositoryProcessor;
import com.messageproccesor.utils.LoggerMessageProccesor;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ProcessExecutor {
    //Cant modify into moment
    public final static PatternScope DEFAULT_PATTERN_SCOPE = PatternScope.SINGLETON;

    @Getter
    private final Map< Class<IServiceProccesor>, Set<Class<IRepositoryProcessor>> > allhandlerProcessorGroupingrepositories;
    @Getter
    private final Set< Class< ? > > otherObjectsClass;


    private final Set< IServiceProccesor > handlerSingleton = new HashSet<>();
    private final Set< IRepositoryProcessor > repositorySingleton = new HashSet<>();

    public ProcessExecutor(Map< Class< IServiceProccesor >, Set< Class< IRepositoryProcessor > > > allhandlerProcessorGroupingrepositories,
                           Set< Class< ? > > otherObjectsClass) {
        this.allhandlerProcessorGroupingrepositories = allhandlerProcessorGroupingrepositories;
        this.otherObjectsClass = otherObjectsClass;
    }


    public <T extends IObjetToProcessed> void  exec(T objetToProcessed) throws NullPointerException{

        Optional<Set<Class<IServiceProccesor>>> handlerProcessorClass = UtilsProcessor
                .filterByContainGenericParams(
                        objetToProcessed.getClass(),
                        allhandlerProcessorGroupingrepositories.keySet()
                );
        if(handlerProcessorClass.isEmpty())
            throw new NullPointerException("IHandlerProcessor is empty");

        Set<Class<IRepositoryProcessor>> repositories = allhandlerProcessorGroupingrepositories
                .get(handlerProcessorClass.get().stream()
                .findFirst().orElseThrow());
        if(repositories.isEmpty())
            throw new NullPointerException("IRepositoryProcessor is empty");

        Set<Class<IRepositoryProcessor>> repositoryFilter = repositories.stream()
                .filter(a->FilterAnnotation.filterByAnnotationFilterHeader(objetToProcessed.getHeader(), a))
                .collect(Collectors.toSet());
        if(repositoryFilter.isEmpty())
            throw new NullPointerException("IRepositoryProcessor compatible with HandlerProcessor is empty");

        injectRepositoryInHandlerClassAndExecProcces(
                handlerProcessorClass.get()
                        .stream().findFirst()
                        .orElseThrow(() -> new NullPointerException("IHandlerProcessor is empty")),
                repositoryFilter ,
                objetToProcessed
        );

    }


    private static <T extends IObjetToProcessed> void injectRepositoryInHandlerClassAndExecProcces(Class< IServiceProccesor > iHandlerProcessorClass,
                                                                                                   Set<Class<IRepositoryProcessor>> iRepositoryProccesor,
                                                                                                   T objetToProcessed){
        IServiceProccesor handlerInstace = null;
        for (Class<IRepositoryProcessor> repositoryProcessorClass:
                iRepositoryProccesor) {
            try{
                if(handlerInstace == null)
                    handlerInstace= (IServiceProccesor) loadInstancesWithPattern(iHandlerProcessorClass);

                IRepositoryProcessor<?> iRepositoryProcessor = loadInstancesWithPattern(repositoryProcessorClass);

                handlerInstace.executionProcess(iRepositoryProcessor,objetToProcessed);

            }catch (Exception e){
                LoggerMessageProccesor.getLogger().log(Level.WARNING,"ProcessExecutor can´t executed method executionProcess" , e);
            }
        }
    }

    private static <T> T loadInstancesWithPattern(Class<T> aClass) throws InvocationTargetException, InstantiationException, IllegalAccessException, NotSupportException {
        T instanceNew = null;

        if(FilterAnnotation.filterByPatternScopeAnnotationImpl(aClass,PatternScope.PROTOTYPE))
            instanceNew = (T) Arrays.stream(aClass.getConstructors())
                    .filter(constructor->constructor.getParameterTypes().length == 0)
                    .findFirst().orElseThrow()
                    .newInstance();
        else
            throw new NotSupportException();
        return instanceNew;
    }



}
