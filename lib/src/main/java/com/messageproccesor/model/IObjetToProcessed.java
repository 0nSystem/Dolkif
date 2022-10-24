package com.messageproccesor.model;

public interface IObjetToProcessed<T> {
    String getHeader();
    T getBody();
}
