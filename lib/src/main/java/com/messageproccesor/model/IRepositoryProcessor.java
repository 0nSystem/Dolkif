package com.messageproccesor.model;

import com.messageproccesor.annotations.Proccesor;

@Proccesor
public interface IRepositoryProcessor<T extends IObjetToProcessed> {

    boolean process(T t);


}
