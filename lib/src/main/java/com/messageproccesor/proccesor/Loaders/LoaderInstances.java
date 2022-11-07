package com.messageproccesor.proccesor.Loaders;


import com.messageproccesor.enums.PatternScope;
import com.messageproccesor.proccesor.BeansContainer;
import com.messageproccesor.model.IRepositoryProcessor;
import com.messageproccesor.model.IServiceProccesor;
import com.messageproccesor.proccesor.Filters.FilterAnnotation;
import com.messageproccesor.proccesor.Filters.FilterGenerics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class LoaderInstances {

    private final BeansContainer beansContainer;

    public LoaderInstances(BeansContainer beansContainer) {
        this.beansContainer = beansContainer;
    }

    public <T> T loadInstancesWithPatternExecute(Class<T> aClass)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NullPointerException {
        T instanceNew = null;
        if(FilterAnnotation.filterByPatternScopeAnnotationImpl(aClass, PatternScope.PROTOTYPE))
            instanceNew = loadInstances(aClass);
        else{
            /*
            Tener en si ya existe en el singleton
             */
            if(FilterGenerics.containsInterface(aClass, IServiceProccesor.class,true)){
                Optional<IServiceProccesor> handlerOptional = beansContainer.getHandlerSingleton()
                        .stream()
                        .filter(a->a.getClass().equals(aClass))
                        .findFirst();
                if(handlerOptional.isPresent())
                    instanceNew = (T) handlerOptional.get();
                else{
                    instanceNew = loadInstances(aClass);
                    beansContainer.getHandlerSingleton().add((IServiceProccesor< ? >) instanceNew);
                }

            }
            else if(FilterGenerics.containsInterface(aClass, IRepositoryProcessor.class,true)){
                Optional<IRepositoryProcessor> resitoryOptional = beansContainer.getRepositorySingleton()
                        .stream()
                        .filter(a->a.getClass().equals(aClass))
                        .findFirst();
                if(resitoryOptional.isPresent())
                    instanceNew = (T) resitoryOptional.get();
                else{
                    instanceNew = loadInstances(aClass);
                    beansContainer.getRepositorySingleton().add((IRepositoryProcessor< ? >) instanceNew);
                }
            }
            else{
                Optional<Object> optionalObject= beansContainer.getOtherObjectSingleton()
                        .stream()
                        .filter(a->a.getClass().equals(aClass))
                        .findFirst();
                if(optionalObject.isPresent())
                    instanceNew = (T) optionalObject.get();
                else{
                    instanceNew = loadInstances(aClass);
                    beansContainer.getOtherObjectSingleton().add(instanceNew);
                }
            }
        }

        if(instanceNew == null)
            throw new NullPointerException();

        return instanceNew;
    }

    private < T > T loadInstances(Class<T> aClass)
            throws NullPointerException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Set<Constructor< ? >> avalibleConstructors = getConstructorAvailable(aClass);
        int maxParamsAvailable = avalibleConstructors.stream()
                .mapToInt(constructor -> constructor.getParameterTypes().length)
                .max()
                .orElse(0);
        Constructor < ? > constructorToExecute = null ;
        for (Constructor < ? > constructor:
                avalibleConstructors) {
            if( maxParamsAvailable == constructor.getParameterTypes().length ){
                constructorToExecute = constructor;
                break;
            }
        }

        Set< Object > paramsToConstructor = getParamsToLoadConstructor(constructorToExecute);
        if(paramsToConstructor.size() == 0)
            return (T) constructorToExecute.newInstance();

        return (T) constructorToExecute.newInstance(paramsToConstructor);
    }

    private Set< Object > getParamsToLoadConstructor(Constructor < ? > constructor)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {

        Set<Object> paramsToConstructor = new HashSet<>();
        for (Class< ? > paramClass:
                constructor.getParameterTypes()) {
            paramsToConstructor.add(loadInstancesWithPatternExecute(paramClass));
        }

        return paramsToConstructor;
    }
    private Set<Constructor< ? > > getConstructorAvailable(Class< ? > aClass){

        Set< Constructor< ? > > mapWithAllConstructorAvalible = new HashSet<>();

        for (Constructor constructor:
                aClass.getConstructors()) {

            boolean isAvalible = false;

            if(constructor.getParameterTypes().length==0)
                isAvalible = true;

            for (Class< ? > contructorParam : constructor.getParameterTypes()) {
                isAvalible = beansContainer.allClassesFound().contains(contructorParam);
            }

            if(isAvalible)
                mapWithAllConstructorAvalible.add(constructor);

        }
        return mapWithAllConstructorAvalible;
    }
}
