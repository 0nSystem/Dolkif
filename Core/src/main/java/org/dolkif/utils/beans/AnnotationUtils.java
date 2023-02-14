package org.dolkif.utils.beans;

import lombok.NonNull;
import lombok.val;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class AnnotationUtils {

    public static <R extends Annotation> Optional<R> getAnnotation(final @NonNull Class<?> classType, final @NonNull Class<R> annotationType) {
        return checkResultAnnotation(classType.getAnnotation(annotationType));
    }
    public static <T extends Annotation> Optional<T> getAnnotation(final @NonNull Method method, final @NonNull Class<T> annotationType){
        return checkResultAnnotation(method.getAnnotation(annotationType));
    }

    public static <T extends Annotation> Optional<T> getAnnotation(final @NonNull Field field, final @NonNull Class<T> annotationType){
        return checkResultAnnotation(field.getAnnotation(annotationType));
    }

    public static <T extends Annotation> List<Field> getFieldsWithAnnotation(final @NonNull Class<?> classType, final @NonNull Class<T> annotationType){
        List<Field> fieldsAnnotationToResult = new ArrayList<>();
        val listFieldsWithAnnotationInThisClass = Arrays.stream(classType.getDeclaredFields())
                .filter(field -> getAnnotation(field, annotationType).isPresent())
                .toList();
        if(!listFieldsWithAnnotationInThisClass.isEmpty())
            fieldsAnnotationToResult.addAll(listFieldsWithAnnotationInThisClass);
        if(classType.getSuperclass() != null)
            fieldsAnnotationToResult.addAll(getFieldsWithAnnotation(classType.getSuperclass(),annotationType));

        return fieldsAnnotationToResult;

    }
    public static <T extends Annotation> List<Method> getMethodsWithAnnotation(final @NonNull Class<?> classType, final @NonNull Class<T> annotationType){
        List<Method> methodsAnnotatingToResult = new ArrayList<>();
        val listFieldsAnnotation = Arrays.stream(classType.getDeclaredMethods())
                .filter(method -> getAnnotation(method,annotationType).isPresent())
                .toList();
        if(!listFieldsAnnotation.isEmpty())
            methodsAnnotatingToResult.addAll(listFieldsAnnotation);
        if(classType.getSuperclass() != null)
            methodsAnnotatingToResult.addAll(getMethodsWithAnnotation(classType.getSuperclass(),annotationType));

        return methodsAnnotatingToResult;
    }

    private static <T extends Annotation> Optional<T> checkResultAnnotation(T annotation){
        if(annotation != null)
            return Optional.of(annotation);
        return Optional.empty();
    }

}
