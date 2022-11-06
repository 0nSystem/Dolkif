package com.messageproccesor.ClassToTest;

import com.messageproccesor.annotations.Scope;
import com.messageproccesor.enums.PatternScope;
import com.messageproccesor.model.IServiceProccesor;
import com.messageproccesor.model.IRepositoryProcessor;

@Scope(pattern = PatternScope.SINGLETON)
public class HandlerSingletonTest implements IServiceProccesor<ObjectProccesedTest> {
    @Override
    public void executionProcess(IRepositoryProcessor<ObjectProccesedTest> stringRepositoryProcessor, ObjectProccesedTest objectProccesedTest) {
        System.out.println("Start Executing");
        stringRepositoryProcessor.process(objectProccesedTest);
        System.out.println("Finish Executing");

    }

}
