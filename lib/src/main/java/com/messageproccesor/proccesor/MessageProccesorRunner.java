package com.messageproccesor.proccesor;

import com.messageproccesor.annotations.Qualify;
import com.messageproccesor.exceptions.ExceptionHandlerNotCompatibleWithRepository;
import com.messageproccesor.model.IHandlerProcessor;
import com.messageproccesor.model.IRepositoryProcessor;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLClassLoader;
import java.util.*;

import lombok.Getter;


public class MessageProccesorRunner {
    private static ClassProccesor classProccesor=null;

    @Getter
    private static final Map<Class<IHandlerProcessor>, Set<Class<IRepositoryProcessor>>> handlerProcessorGroupingrepositories=new HashMap<>();
    public static void run(Class  aClass){
        classProccesor=ClassProccesor.from(aClass.getClassLoader());
        makeGroupsHandlers();
        makeRepositoriesInGroup();
    }
    private static void makeGroupsHandlers(){
        classProccesor.getResource().stream().filter(a->{
            try {
                Class cl=URLClassLoader.getSystemClassLoader().loadClass(a.getResoucePath());
                return containsInterface(cl, IHandlerProcessor.class,true);
            } catch (ClassNotFoundException e) {
                return false;
            }
        }).forEach(a->{
            try {
                Class<?> aClass=URLClassLoader.getSystemClassLoader().loadClass(a.getResoucePath());
                if(!handlerProcessorGroupingrepositories.containsKey(aClass)){
                    handlerProcessorGroupingrepositories.put((Class<IHandlerProcessor>) aClass,new HashSet<>());
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
                return containsInterface(cl, IRepositoryProcessor.class,true);
            } catch (ClassNotFoundException e) {
                return false;
            }
        }).forEach(a->{
            try {
                Class<?> aClass=URLClassLoader.getSystemClassLoader().loadClass(a.getResoucePath());
                Type[] typesAClass=aClass.getGenericInterfaces();
                Class<?> classfilterQualify=null;

                Optional<Class<IHandlerProcessor>> optional=filterByQualifyAnnotation(aClass,handlerProcessorGroupingrepositories.keySet());
                if(optional.isPresent())
                    classfilterQualify=optional.get();

                if(classfilterQualify==null){
                    List<ParameterizedType> parameterizedTypes=Arrays.stream(aClass.getGenericInterfaces()).map(type->(ParameterizedType)type).toList();
                    optional=UtilsProcessor.filterByCompatibilityGenericParams(parameterizedTypes,handlerProcessorGroupingrepositories.keySet());
                    if (optional.isPresent())
                        classfilterQualify=optional.get();
                }

                if(classfilterQualify==null){
                    throw new ExceptionHandlerNotCompatibleWithRepository();
                }

                handlerProcessorGroupingrepositories.get(classfilterQualify).add((Class<IRepositoryProcessor>) aClass);
            } catch (ClassNotFoundException|ExceptionHandlerNotCompatibleWithRepository e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static <T> Optional<Class<T>> filterByQualifyAnnotation(Class<?> aClass,Set<Class<T>> classes){
        for (Annotation annotation:
                aClass.getAnnotations()) {
            if(annotation.annotationType().equals(Qualify.class)){
                Qualify qualify=(Qualify) annotation;
                Optional<Class<T>> optionalClass=classes.stream().filter(handlerProcessorClass -> handlerProcessorClass.equals(qualify.name())).findFirst();
                if(optionalClass.isPresent()){
                    List<ParameterizedType> typeParams= Arrays.stream(aClass.getGenericInterfaces()).map(a -> (ParameterizedType) a).toList();
                    Set<Class<T>> classesFilter=Set.of(optionalClass.get());
                    return UtilsProcessor.filterByCompatibilityGenericParams(typeParams,classesFilter);
                }
            }
        }
        return Optional.empty();
    }


    /**
     *
     * @param aClass
     * @param required
     * @param notGetBasicInterface represent first iteration to delete base interface and get all class implement interface
     * @return
     */
    private static boolean containsInterface(Class<?> aClass,Class<?> required,boolean notGetBasicInterface){
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
