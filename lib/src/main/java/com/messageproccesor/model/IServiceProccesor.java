package com.messageproccesor.model;

public interface IServiceProccesor<T extends IObjetToProcessed>{
    void executionProcess(IRepositoryProcessor<T> tRepositoryProcessor , T t);
}
