package com.messageproccesor.model;

import com.messageproccesor.annotations.Proccesor;

@Proccesor
public interface RepositoryProcessor<T> {

    boolean process(T t);


}
