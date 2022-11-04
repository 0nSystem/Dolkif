package com.messageproccesor.proccesor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class UtilsProcessor {
    public static <T> Optional<Class<T>> filterByCompatibilityGenericParams(List<ParameterizedType> types, Set<Class<T>> classes){
        for (Class<T> handler:
                classes) {
            List<ParameterizedType> typesHandler = Arrays.stream(handler.getGenericInterfaces()).map(a -> (ParameterizedType) a).toList();
            for (ParameterizedType typeHandler:
                    typesHandler) {

                List<Type> typesFilterCoincided = Arrays.stream(typeHandler.getActualTypeArguments())
                        .filter(handlerTypeParams -> {
                            for (ParameterizedType typeSended :
                                    types) {
                                Optional<Type> optional = Arrays.stream(
                                        typeSended.getActualTypeArguments())
                                        .filter(type -> type.getTypeName().equals(handlerTypeParams.getTypeName()))
                                        .findFirst();

                                if (optional.isPresent())
                                    return true;
                            }
                            return false;
                        }).toList();

                if(!typesFilterCoincided.isEmpty())
                    return Optional.of(handler);
            }
        }
        return Optional.empty();
    }

    /**
     *
     * @param classRequired is a Generic Param Object Required
     * @param classes Group Class to find in yours generic params
     * @return Return
     * @param <T>
     */
    public static <T> Optional<Set<Class<T>>> filterByContainGenericParams(Class<?> classRequired, Set<Class<T>> classes){
        Set<Class<T>> returnSet = new HashSet<>();

        for (Class<T> aClass:
                classes) {
            List<ParameterizedType> typesHandler = Arrays.stream(aClass.getGenericInterfaces()).map(a -> (ParameterizedType) a).toList();
            for (ParameterizedType typeHandler:
                    typesHandler) {

                for (Type paramGeneric:
                     typeHandler.getActualTypeArguments()) {

                    if(paramGeneric.getTypeName().equals(classRequired.getTypeName()))
                        returnSet.add(aClass);

                }
            }
        }

        if(!returnSet.isEmpty())
            return Optional.of(returnSet);

        return Optional.empty();

    }
}
