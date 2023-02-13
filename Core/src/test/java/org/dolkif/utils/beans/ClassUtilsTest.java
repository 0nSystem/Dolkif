package org.dolkif.utils.beans;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class ClassUtilsTest {


    @Test
    public void testFindClassInheritanceAndInterfaceImplementations(){
        Optional<Class<String>> errorClassNotContains = ClassUtils.findClassInheritanceAndInterfaceImplementations(String.class,ClassExample.class);
        Assert.assertTrue("Error class find is equals class type",errorClassNotContains.isEmpty());

        Optional<Class<ClassExample>> classExample = ClassUtils.findClassInheritanceAndInterfaceImplementations(ClassExample.class, ClassExample.class);
        Assert.assertTrue("Error class find not equals class type",classExample.isPresent());

        Optional<Class<InterfaceExample>> classExampleImplementInterfaceExample = ClassUtils.findClassInheritanceAndInterfaceImplementations(InterfaceExample.class,ClassExample.class);
        Assert.assertTrue("Error class find not equals class InterfaceExample", classExampleImplementInterfaceExample.isPresent());

        Optional<Class<ClassExample>> classInheritanceExample = ClassUtils.findClassInheritanceAndInterfaceImplementations(ClassExample.class,ClassInheritanceExample.class);
        Assert.assertTrue("Error class find not equals class InterfaceExample", classInheritanceExample.isPresent());
    }


    private interface InterfaceExample{}
    private static class ClassExample implements InterfaceExample{}
    private static class ClassInheritanceExample extends ClassExample{}

}
