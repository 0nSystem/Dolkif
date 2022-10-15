package com.messageproccesor;

import com.messageproccesor.annotations.Qualify;
import com.messageproccesor.model.RepositoryProcessor;


public class RepositoryTest implements RepositoryProcessor<String> {
    @Override
    public boolean process(String s) {
        return false;
    }
}
