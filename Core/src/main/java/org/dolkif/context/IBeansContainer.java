package org.dolkif.context;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface IBeansContainer {

    boolean addBean(final @NonNull Bean.BeanBase<?> beanBase);

    List<Bean.BeanBase<?>> getAllBeans();

    <T> List<Bean.BeanBase<?>> filterBean(final @NonNull Bean.BeanReference<T> beanReference);
    <T> Optional<Bean.BeanBase<?>> findBean(final @NonNull Bean.BeanReference<T> beanReference);
    <T> List<Bean.BeanBase<?>> filterBean(Class<T> classType);


}
