package com.messageproccesor.ClassToTest;

import com.messageproccesor.annotations.HeaderFilter;
import com.messageproccesor.annotations.Qualify;
import com.messageproccesor.model.IRepositoryProcessor;

@HeaderFilter(header = "print")
@Qualify(name = HandlerTest.class)
public class RepositoryTest implements IRepositoryProcessor<ObjectProccesedTest> {

    @Override
    public boolean process(ObjectProccesedTest stringObjectProccesedTest) {
        System.out.println(stringObjectProccesedTest.getBody());
        return false;
    }

}
