package com.messageproccesor.model;

public interface HandlerProcessor <T>{
    void executionProcess(RepositoryProcessor<T> tRepositoryProcessor);
}
