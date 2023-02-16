package org.dolkif.context;

import lombok.*;

import java.lang.annotation.Annotation;

public final class Bean {

    private Bean() throws UnsupportedOperationException{
        throw new UnsupportedOperationException("Error create instance, constructor not support");
    }
    public enum ScopePattern {
        SINGLETON,PROTOTYPE
    }
    public enum TypeReference{
        FIELDS_CLASS,
        PARAMS_EXECUTABLE,
        NULL

    }

    @AllArgsConstructor
    @Data
    public static final class BeanReference<T> {
        private final @NonNull TypeReference typeReference;
        private final @NonNull Class<T> classType;
        private final @NonNull Annotation[] annotationsLoaded;
    }
    @EqualsAndHashCode(callSuper = true)
    public static final class Instance<T> extends BeanBase<T>{
        public Instance(Configuration configuration,T value){
            super(configuration,value);
        }
    }
    @EqualsAndHashCode(callSuper = true)
    public static final class Type<T> extends BeanBase<Class<T>>{
        public Type(Configuration configuration, Class<T> value){
            super(configuration,value);
        }
    }


    @Data
    @AllArgsConstructor
    public static abstract class BeanBase<T> implements BeanContainerOperation<T> {
        private final @NonNull Configuration configuration;
        private final @NonNull T value;
    }

    public interface BeanContainerOperation<T>{
        @NonNull Configuration getConfiguration();
        @NonNull T getValue();
    }

    @AllArgsConstructor
    @Data
    public static class Configuration{

        private final String qualifierName;
        private final @NonNull ScopePattern scope;

    }

}
