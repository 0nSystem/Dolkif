package com.messageproccesor.model;

public interface IHandlerProcessor<T extends IObjetToProcessed>{
    void executionProcess(IRepositoryProcessor<T> tRepositoryProcessor,T t);
}
