package org.dolkif.context;


import java.lang.reflect.InvocationTargetException;

public interface ILoader {


    <T> T getInstance(Class<T> classType) throws InvocationTargetException, IllegalAccessException, InstantiationException;
    IBeansContainer getBeansContainer();

}
