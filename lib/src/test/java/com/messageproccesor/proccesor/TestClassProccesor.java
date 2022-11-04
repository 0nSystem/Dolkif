package com.messageproccesor.proccesor;

import com.messageproccesor.ClassToTest.ObjectProccesedTest;
import org.junit.Assert;
import org.junit.Test;

public class TestClassProccesor {
    @Test
    public void basicRunClassProccesor(){
        MessageProccesorRunner.run(TestClassProccesor.class);
        MessageProccesorRunner.getHandlerProcessorGroupingrepositories().entrySet().forEach(classSetEntry -> {
            System.out.println("key->"+classSetEntry.getKey().getTypeName()+", value->"+classSetEntry.getValue().toString());
        });

        ObjectProccesedTest objetToProcessed=new ObjectProccesedTest();
        ProcessExecutor.exec(objetToProcessed);

        Assert.assertEquals(1,MessageProccesorRunner.getHandlerProcessorGroupingrepositories().entrySet().size());
    }
}
