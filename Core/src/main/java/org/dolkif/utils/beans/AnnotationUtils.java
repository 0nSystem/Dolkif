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
    public static <T extends Annotation> List<Field> getFieldsWithAnnotation(final @NonNull Class<?> classType, final @NonNull Class<T> annotationType){
        List<Field> fieldsAnnotationToResult = new ArrayList<>();
        val listFieldsWithAnnotationInThisClass = Arrays.stream(classType.getDeclaredFields())
                .filter(field -> Optional.ofNullable(field.getAnnotation(annotationType)).isPresent())
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
                .filter(method -> Optional.ofNullable(method.getAnnotation(annotationType)).isPresent())
                .toList();
        if(!listFieldsAnnotation.isEmpty())
            methodsAnnotatingToResult.addAll(listFieldsAnnotation);
        if(classType.getSuperclass() != null)
            methodsAnnotatingToResult.addAll(getMethodsWithAnnotation(classType.getSuperclass(),annotationType));

        return methodsAnnotatingToResult;
    }

}
