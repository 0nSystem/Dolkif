package org.dolkif.context;


import lombok.NonNull;

public interface ILoader {


    <T> T newInstance(final @NonNull Bean.BeanReference<T> beanReference);
    IBeansContainer getBeansContainer();

}
