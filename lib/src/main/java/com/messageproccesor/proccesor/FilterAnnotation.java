package com.messageproccesor.proccesor;

import com.messageproccesor.annotations.HeaderFilter;
import com.messageproccesor.annotations.Qualify;
import com.messageproccesor.annotations.Scope;
import com.messageproccesor.enums.PatternScope;
import com.messageproccesor.exceptions.ScopeAnnotationNotFoundException;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class FilterAnnotation {
    /**
     *
     * @param classes class to show annotation declare
     * @param patternScope to filter with pattern
     * @return
     * @throws ScopeAnnotationNotFoundException is throw if not exist in class {@link Scope}
     */
    private static boolean filterByPatternScopeAnnotation(Class<?> classes, PatternScope patternScope) throws ScopeAnnotationNotFoundException {

        Optional<Scope> scope = Arrays.stream(classes.getAnnotations())
                .filter( annotation -> annotation.annotationType().equals(Scope.class))
                .findFirst()
                .map(annotation -> (Scope) annotation);

        if(scope.isEmpty())
            throw new ScopeAnnotationNotFoundException();

        if(scope.get().pattern().equals(patternScope))
            return true;


        return false;
    }
    /**
     * this class control logic in {@see filterByPatternScopeAnnotation}
     * @param classes class to show annotation declare
     * @param patternScope to filter with pattern
     * @return
     * @throws ScopeAnnotationNotFoundException is throw if not exist in class {@link Scope}
     */
    public static boolean filterByPatternScopeAnnotationImpl(Class<?> classes, PatternScope patternScope){
        try {
            return filterByPatternScopeAnnotation(classes, patternScope);
        } catch (ScopeAnnotationNotFoundException e) {
            if(patternScope == ProcessExecutor.DEFAULT_PATTERN_SCOPE)
                return true;
        }
        return false;

    }

    /**
     *
     * @param aClass with annotation {@link Qualify} to search type
     * @param classes list to compare if is true class specific {@link Qualify}
     * @return first field because it can`t has multiple attributes
     * @param <T>
     */
    public static <T> Optional<Class<T>> filterByQualifyAnnotation(Class<?> aClass, Set<Class<T>> classes){
        for (Annotation annotation:
                aClass.getAnnotations()) {
            if(annotation.annotationType().equals(Qualify.class)){
                Qualify qualify = (Qualify) annotation;
                Optional<Class<T>> optionalClass = classes.stream()
                        .filter(handlerProcessorClass -> handlerProcessorClass.equals(qualify.name()))
                        .findFirst();

                if(optionalClass.isPresent()){
                    List<ParameterizedType> typeParams = Arrays.stream(aClass.getGenericInterfaces()).map(a -> (ParameterizedType) a).toList();
                    Set<Class<T>> classesFilter = Set.of(optionalClass.get());
                    return UtilsProcessor.filterByCompatibilityGenericParams(typeParams,classesFilter);
                }
            }
        }
        return Optional.empty();
    }

    /**
     *
     * @param header @{@link HeaderFilter} string
     * @param aClass class to search
     * @return
     */
    public static boolean filterByAnnotationFilterHeader(String header, Class<?> aClass){


        for (Annotation annotation:
                aClass.getAnnotations()) {

            if(annotation.annotationType().equals(HeaderFilter.class)){
                HeaderFilter headerFilter = (HeaderFilter) annotation;
                if(headerFilter.header().equals(header))
                    return true;
            }
        }
        return false;
    }
}
