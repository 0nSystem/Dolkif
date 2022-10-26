package com.messageproccesor.ClassToTest;

import com.messageproccesor.model.IObjetToProcessed;

public class ObjectProccesedTest implements IObjetToProcessed<String> {
    @Override
    public String getHeader() {
        return "print";
    }

    @Override
    public String getBody() {
        return "Hello World";
    }
}
