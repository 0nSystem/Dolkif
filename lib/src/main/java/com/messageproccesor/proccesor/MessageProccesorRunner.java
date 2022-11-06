package com.messageproccesor.proccesor;

import com.messageproccesor.exceptions.ExceptionHandlerNotCompatibleWithRepository;
import com.messageproccesor.model.IServiceProccesor;
import com.messageproccesor.model.IRepositoryProcessor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;


import com.messageproccesor.proccesor.Filters.FilterAnnotation;
import com.messageproccesor.proccesor.Filters.FilterGenerics;
import com.messageproccesor.proccesor.Loaders.FilesToLoadClass;
import lombok.Getter;


public class MessageProccesorRunner {
    private static FilesToLoadClass classProccesor = null;
    @Getter
    private static ProcessExecutor processExecutor = null;


    public static void run(Class< ? > aClass){
        classProccesor = FilesToLoadClass.from(aClass.getClassLoader());
        Set< Class< ? > > otherObjectsClass = findOtherObjectsDifferentAServiceProcessorAndRepositories();
        Map< Class< IServiceProccesor >, Set< Class < IRepositoryProcessor > > > handlerProcessorGroupingrepositories = new HashMap<>();
        makeGroupsHandlers(handlerProcessorGroupingrepositories);
        makeRepositoriesInGroup(handlerProcessorGroupingrepositories);
        processExecutor = new ProcessExecutor(
                handlerProcessorGroupingrepositories,
                otherObjectsClass);

    }

    private static Set< Class < ? > > findOtherObjectsDifferentAServiceProcessorAndRepositories(  ){
        return classProccesor.getResource()
                .stream()
                .filter(a->{
                    try {
                        Class cl = URLClassLoader.getSystemClassLoader()
                                .loadClass(a.getResoucePath());
                        if(
                                FilterAnnotation.filterByAnnotationComponent(cl)
                                &&!FilterGenerics.containsInterface(cl, IServiceProccesor.class,true)
                                && !FilterGenerics.containsInterface(cl, IRepositoryProcessor.class,true)
                        )
                            return true;
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                   return false;
                }).map(a-> {
                    try {
                        return URLClassLoader.getSystemClassLoader().loadClass(a.getResoucePath());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toSet());
    }

    private static void makeGroupsHandlers(Map< Class< IServiceProccesor >, Set< Class< IRepositoryProcessor > > > handlerProcessorGroupingrepositoriesPrototype){
        classProccesor.getResource()
                .stream()
                .filter(a->{
                    try {
                        Class cl = URLClassLoader.getSystemClassLoader()
                                .loadClass(a.getResoucePath());
                        return FilterGenerics.containsInterface(cl, IServiceProccesor.class,true);
                    } catch (ClassNotFoundException e) {
                        return false;
                    }
                })
                .forEach(a->{
                    try {

                        Class<?> aClass = URLClassLoader.getSystemClassLoader().loadClass(a.getResoucePath());
                        if(!handlerProcessorGroupingrepositoriesPrototype.containsKey(aClass))
                            handlerProcessorGroupingrepositoriesPrototype.put((Class<IServiceProccesor>) aClass,new HashSet<>());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });


    }
    private static void makeRepositoriesInGroup(Map< Class< IServiceProccesor >, Set< Class< IRepositoryProcessor > > > handlerProcessorGroupingrepositories){
        classProccesor.getResource()
                .stream().filter( a ->{
                    try {
                        Class cl = URLClassLoader.getSystemClassLoader()
                                .loadClass(a.getResoucePath());
                        return FilterGenerics.containsInterface(cl, IRepositoryProcessor.class,true);
                    } catch (ClassNotFoundException e) {
                        return false;
                    }
                }).forEach(a -> {
                    try {
                        Class< ? > aClass = URLClassLoader.getSystemClassLoader().loadClass(a.getResoucePath());
                        Type[] typesAClass = aClass.getGenericInterfaces();
                        Class< ? > classfilterQualify = null;

                        Optional< Class< IServiceProccesor > > optional = FilterAnnotation.
                                filterByQualifyAnnotationAndGenericParamsCompatible(
                                        aClass,
                                        handlerProcessorGroupingrepositories.keySet()
                                );
                        if( optional.isPresent() )
                            classfilterQualify = optional.get();

                        if( classfilterQualify == null ){
                            List<ParameterizedType> parameterizedTypes = Arrays.stream(aClass.getGenericInterfaces())
                                        .map(type -> (ParameterizedType) type)
                                        .toList();

                            optional = FilterGenerics
                                    .filterByCompatibilityGenericParams(
                                            parameterizedTypes,
                                            handlerProcessorGroupingrepositories.keySet()
                                    );
                            if ( optional.isPresent() )
                                classfilterQualify = optional.get();
                        }

                        if(classfilterQualify == null)
                            throw new ExceptionHandlerNotCompatibleWithRepository();

                        handlerProcessorGroupingrepositories.get(classfilterQualify).add( ( Class< IRepositoryProcessor > ) aClass );
                    } catch (ClassNotFoundException|ExceptionHandlerNotCompatibleWithRepository e) {
                        throw new RuntimeException(e);
                    }
                });
    }






}
