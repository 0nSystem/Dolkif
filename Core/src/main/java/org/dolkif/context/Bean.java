package org.dolkif.context;

import lombok.*;

import java.lang.annotation.Annotation;
import java.util.List;

public final class Bean {

    private Bean() throws UnsupportedOperationException{
        throw new UnsupportedOperationException("Error create instance, constructor not support");
    }
    public enum ScopePattern {
        SINGLETON,PROTOTYPE
    }
    public enum TypeReference{
        FIELDS_CLASS,
        PARAMS_EXECUTABLE_METHOD,
        PARAMS_EXECUTABLE_CONSTRUCTOR,
        CLASS,
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
        Instance(Configuration configuration,T value){
            super(configuration,value);
        }
    }
    @EqualsAndHashCode(callSuper = true)
    public static final class Type<T> extends BeanBase<Class<T>>{
        private @Getter final List<BeanReference<?>> dependencies;
        Type(final @NonNull Configuration configuration,final @NonNull Class<T> value,final @NonNull List<BeanReference<?>> dependencies){
            super(configuration,value);
            this.dependencies = dependencies;
        }
    }


    @Data
    @AllArgsConstructor
    public static abstract class BeanBase<T> implements BeanContainerOperation<T> {
        private final @NonNull Configuration configuration;
        private final @NonNull T value;

        public static <T> BeanBase<T> of(final @NonNull Configuration configuration,final @NonNull T value,final @NonNull List<BeanReference<?>> beanDependencies){ //TODO PENDING TO TEST
            if (value instanceof Class<?>)
                return (BeanBase<T>) new Type<>(configuration, (Class<T>) value,beanDependencies);
            else
                return new Instance<>(configuration,value);
        }
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
