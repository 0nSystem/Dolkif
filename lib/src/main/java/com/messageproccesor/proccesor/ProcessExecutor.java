package com.messageproccesor.proccesor;


import com.messageproccesor.enums.PatternScope;
import com.messageproccesor.model.BeansContainer;
import com.messageproccesor.model.IServiceProccesor;
import com.messageproccesor.model.IObjetToProcessed;
import com.messageproccesor.model.IRepositoryProcessor;
import com.messageproccesor.utils.LoggerMessageProccesor;
import lombok.Getter;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
public class ProcessExecutor {
    //Cant modify into moment
    public final static PatternScope DEFAULT_PATTERN_SCOPE = PatternScope.SINGLETON;

    private final BeansContainer beansContainer;
    private final LoaderInstances loaderInstances;


    public ProcessExecutor(Map< Class< IServiceProccesor >, Set< Class< IRepositoryProcessor > > > allhandlerProcessorGroupingrepositories,
                           Set< Class< ? > > otherObjectsClass) {
        this.beansContainer = new BeansContainer(allhandlerProcessorGroupingrepositories,otherObjectsClass);
        this.loaderInstances = new LoaderInstances(this.beansContainer);
    }


    public <T extends IObjetToProcessed> void  exec(T objetToProcessed) throws NullPointerException{

        Optional<Set<Class<IServiceProccesor>>> handlerProcessorClass = UtilsProcessor
                .filterByContainGenericParams(
                        objetToProcessed.getClass(),
                        beansContainer.getAllhandlerProcessorGroupingrepositories().keySet()
                );
        if(handlerProcessorClass.isEmpty())
            throw new NullPointerException("IHandlerProcessor is empty");

        Set<Class<IRepositoryProcessor>> repositories = beansContainer.getAllhandlerProcessorGroupingrepositories()
                .get(handlerProcessorClass.get().stream()
                .findFirst().orElseThrow());
        if(repositories.isEmpty())
            throw new NullPointerException("IRepositoryProcessor is empty");

        Set<Class<IRepositoryProcessor>> repositoryFilter = repositories.stream()
                .filter(a->FilterAnnotation.filterByAnnotationFilterHeader(objetToProcessed.getHeader(), a))
                .collect(Collectors.toSet());
        if(repositoryFilter.isEmpty())
            throw new NullPointerException("IRepositoryProcessor compatible with HandlerProcessor is empty");

        injectRepositoryInHandlerToExecuteMethod(
                handlerProcessorClass.get()
                        .stream().findFirst()
                        .orElseThrow(() -> new NullPointerException("IHandlerProcessor is empty")),
                repositoryFilter ,
                objetToProcessed
        );

    }

    private <T extends IObjetToProcessed> void injectRepositoryInHandlerToExecuteMethod(Class< IServiceProccesor > iHandlerProcessorClass,
                                                                                        Set<Class<IRepositoryProcessor>> iRepositoryProccesor,
                                                                                        T objetToProcessed){
        IServiceProccesor handlerInstace = null;
        for (Class<IRepositoryProcessor> repositoryProcessorClass:
                iRepositoryProccesor) {
            try{
                if(handlerInstace == null)
                    handlerInstace = loaderInstances.loadInstancesWithPatternExecute(iHandlerProcessorClass);

                IRepositoryProcessor<?> iRepositoryProcessor = loaderInstances.loadInstancesWithPatternExecute(repositoryProcessorClass);

                handlerInstace.executionProcess(iRepositoryProcessor,objetToProcessed);

            }catch (Exception e){
                LoggerMessageProccesor.getLogger().log(Level.WARNING,"ProcessExecutor can´t executed method executionProcess" , e);
            }
        }
    }



}
