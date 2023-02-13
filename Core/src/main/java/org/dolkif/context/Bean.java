package org.dolkif.context;

import lombok.*;
import org.dolkif.ScopePattern;

public final class Bean {

    private Bean() throws UnsupportedOperationException{
        throw new UnsupportedOperationException("Error create instance, constructor not support");
    }
    @EqualsAndHashCode(callSuper = true)
    public static class Instance<T> extends BeanBase<T>{
        private Instance(Configuration configuration,T value){
            super(configuration,value);
        }
    }
    @EqualsAndHashCode(callSuper = true)
    public static class Type<T> extends BeanBase<Class<T>>{
        private Type(Configuration configuration,Class<T> value){
            super(configuration,value);
        }
    }

    @AllArgsConstructor
    public static class Configuration{

        private final String qualifierName;
        private final @NonNull ScopePattern scope;

    }

    @Getter
    @AllArgsConstructor
    public static class BeanBase<T> implements BeanContainerOperation<T> {
        private @NonNull Configuration configuration;
        private @NonNull T value;
    }

    public interface BeanContainerOperation<T>{
        Configuration getConfiguration();
        T getValue();
    }

}
