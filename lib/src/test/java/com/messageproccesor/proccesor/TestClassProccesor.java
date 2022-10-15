package com.messageproccesor.proccesor;

import org.junit.Assert;
import org.junit.Test;

import java.net.URLClassLoader;

public class TestClassProccesor {
    @Test
    public void basicRunClassProccesor(){
        ClassProccesor classProccesor=ClassProccesor.from(ClassLoader.getSystemClassLoader());
        Assert.assertTrue(classProccesor.getResource().size()>1);
    }
}
