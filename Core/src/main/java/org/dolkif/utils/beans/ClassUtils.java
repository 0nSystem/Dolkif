package org.dolkif.utils.beans;

import lombok.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

public class ClassUtils {

    /**
     * This is one alternatice at "instace of"
     *
     * @param classToFind params filter is equals
     * @param classType base to find
     * @return Is found return present
     * @param <T> Param to find
     * @param <R> Param base
     */
    public static <T,R> Optional<Class<T>> findClassInheritanceAndInterfaceImplementations(final @NonNull Class<T> classToFind, final @NonNull Class<R> classType){
        if(classToFind.equals(classType))
            return Optional.of(classToFind);

        Optional<Class<T>> resultFilter = Arrays.stream(classType.getInterfaces())
                .filter(aClass -> aClass.equals(classToFind))
                .findFirst().map(c->(Class<T>)c);

        if(resultFilter.isEmpty() && classType.getSuperclass() != null)
            resultFilter = findClassInheritanceAndInterfaceImplementations(classToFind,classType.getSuperclass());

        return resultFilter;
    }
    public static <T> Map<Constructor<T>, Type[]> getPublicConstructorWithFormalFields(final @NonNull Class<T> classType){
        return  Arrays.stream(classType.getConstructors())
                .collect(Collectors.toMap(
                        constructor -> (Constructor<T>)constructor,
                        Constructor::getGenericParameterTypes,
                        (objects, objects2) -> objects
                ));
    }






}
