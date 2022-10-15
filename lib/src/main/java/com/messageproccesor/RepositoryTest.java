package com.messageproccesor;

import com.messageproccesor.annotations.Qualify;
import com.messageproccesor.model.RepositoryProcessor;

@Qualify(name = HandlerTest.class)
public class RepositoryTest implements RepositoryProcessor<String> {
    @Override
    public boolean process(String s) {
        return false;
    }
}
