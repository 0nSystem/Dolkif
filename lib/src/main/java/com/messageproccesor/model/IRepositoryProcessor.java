package com.messageproccesor.model;


public interface IRepositoryProcessor<T extends IObjetToProcessed> {

    boolean process(T t);


}
