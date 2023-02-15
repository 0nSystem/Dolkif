package org.dolkif.context;

import java.util.List;
import java.util.Optional;

public final class BeansContainer implements IBeansContainer {

    @Override
    public boolean addBean(Bean.BeanBase<?> beanBase) {
        return false;
    }

    @Override
    public List<Bean.BeanBase<?>> getAllBeans() {
        return null;
    }

    @Override
    public <T> List<Bean.BeanBase<?>> filterBean(Bean.BeanReference<T> beanReference) {
        return null;
    }

    @Override
    public <T> Optional<Bean.BeanBase<?>> findBean(Bean.BeanReference<T> beanReference) {
        return Optional.empty();
    }
}
