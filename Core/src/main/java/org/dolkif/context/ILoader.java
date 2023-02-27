package org.dolkif.context;


public interface ILoader {


    <T> T newInstance(Class<T> classType);
    IBeansContainer getBeansContainer();

}
