package org.dolkif.utils.beans;

import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.util.Optional;


public class AnnotationUtils {

    public static <T,R extends Annotation> Optional<R> getAnnotation(final @NonNull Class<T> classType, final @NonNull Class<R> annotationType) {
        R annotationResult = classType.getAnnotation(annotationType);
        if(annotationResult != null)
            return Optional.of(annotationResult);
        return Optional.empty();
    }
}
