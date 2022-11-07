package com.messageproccesor.proccesor.Filters;

import com.messageproccesor.annotations.Qualify;
import com.messageproccesor.annotations.Scope;
import com.messageproccesor.enums.PatternScope;
import com.messageproccesor.exceptions.ScopeAnnotationNotFoundException;
import com.messageproccesor.proccesor.ProcessExecutor;
import java.lang.reflect.ParameterizedType;
import java.util.*;


public class FilterAnnotation {



    
    /**
     *
     * @param aClass with annotation {@link Qualify} to search type
     * @param classes list to compare if is true class specific {@link Qualify}
     * @return first field because it can`t has multiple attributes
     * @param <T>
     */
    public static < T > Optional< Class < T > > filterByQualifyAnnotationAndGenericParamsCompatible(
            Class< ? > aClass,
            Set<Class< T > > classes
    ){
        Optional< Qualify > qualify = FilterAnnotation.getAnnotation(aClass, Qualify.class);
        if(qualify.isEmpty())
            return Optional.empty();
        
        Optional< Class < T > > optionalClass = classes.stream()
                .filter(handlerProcessorClass -> handlerProcessorClass.equals(qualify.get().name()))
                .findFirst();

        if( optionalClass.isPresent() ){
            List< ParameterizedType > typeParams = Arrays.stream(aClass.getGenericInterfaces())
                    .map(a -> ( ParameterizedType ) a )
                    .toList();
            Set< Class < T > > classesFilter = Set.of(optionalClass.get());

            Optional< Class< T > > objectCompatible = FilterGenerics.filterByCompatibilityGenericParams(typeParams,classesFilter);
            if( objectCompatible.isPresent() )
                return objectCompatible;
        }
        return Optional.empty();
    }

    public static < T > Optional< T > getAnnotation( Class < ? > toFilter,Class< T > required){
        return Arrays.stream(toFilter.getAnnotations())
                .filter( a -> a.annotationType().equals( required ) )
                .findFirst()
                .map(a -> ( T ) a);
    }

}
