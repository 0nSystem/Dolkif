package com.messageproccesor.ClassToTest;

import com.messageproccesor.model.IServiceProccesor;
import com.messageproccesor.model.IRepositoryProcessor;


public class HandlerTest implements IServiceProccesor<ObjectProccesedTest> {
    @Override
    public void executionProcess(IRepositoryProcessor<ObjectProccesedTest> stringRepositoryProcessor,ObjectProccesedTest objectProccesedTest) {
        System.out.println("Start Executing");
        stringRepositoryProcessor.process(objectProccesedTest);
        System.out.println("Finish Executing");

    }

}
