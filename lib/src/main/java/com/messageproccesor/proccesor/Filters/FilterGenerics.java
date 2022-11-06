package com.messageproccesor.proccesor.Filters;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class FilterGenerics {
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
    /**
     * @param aClass
     * @param required
     * @param notGetBasicInterface represent first iteration to delete base interface and get all class implement interface
     * @return
     */
    public static boolean containsInterface(Class<?> aClass,Class<?> required,boolean notGetBasicInterface){
        if(aClass.getTypeName().equals(required.getTypeName())){
            if (notGetBasicInterface)
                return false;
            return true;
        }

        Class< ? >[] interfaces = aClass.getInterfaces();
        for (Class< ? > i:
                interfaces) {
            if(i.getTypeName().equals(required.getTypeName())){
                return true;
            }

            //sub interfaces
            if(i.getInterfaces().length > 0)
                return containsInterface(i,required,false);
        }

        return false;
    }

}
