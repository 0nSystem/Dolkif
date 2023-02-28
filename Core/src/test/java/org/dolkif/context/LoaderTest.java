package org.dolkif.context;


import lombok.val;
import org.dolkif.App;
import org.dolkif.annotations.Bean;
import org.dolkif.annotations.Component;
import org.dolkif.annotations.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

public class LoaderTest {
    private final static String WORD = "Hello World";
    @Test
    public void getInstanceWithConstructorEmpty(){
        ILoader loader = new Loader(new BeansContainer(), new CheckerDependencies());

        try {
            String a = loader.getInstance(String.class);
        } catch (Exception e) {
            Assertions.fail("Error instance empty constructor",e);
        }
    }

    @Test
    public void getInstaceWithEmptyMethodParams(){

        try {
            val resourceEmptyParamsMethod = ReaderClass.of(ClassLoader.getSystemClassLoader()).getResource().stream()
                    .filter(resource -> resource.getResoucePath()
                    .contains(EmptyParamsMethod.class.getName()))
                    .collect(Collectors.toSet());
            ILoader loader = new Loader(new BeansContainer(), new CheckerDependencies(),resourceEmptyParamsMethod);
        } catch (Exception e) {
            Assertions.fail("Error instance empty params method",e);
        }
    }

    @Configuration
    private static class EmptyParamsMethod{
        @Bean
        public String asdasd(){
            return WORD;
        }

    }
}
