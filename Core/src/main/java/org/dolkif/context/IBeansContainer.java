package org.dolkif.context;

import java.util.List;
import java.util.Optional;

public interface IBeansContainer {

    boolean addBean(Bean.BeanBase<?> beanBase);

    List<Bean.BeanBase<?>> getAllBeans();

    <T> List<Bean.BeanBase<?>> filterBean(Bean.BeanReference<T> beanReference);
    <T> Optional<Bean.BeanBase<?>> findBean(Bean.BeanReference<T> beanReference);


}
