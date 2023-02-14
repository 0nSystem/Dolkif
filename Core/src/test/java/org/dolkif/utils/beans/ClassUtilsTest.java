package org.dolkif.utils.beans;

import lombok.val;
import org.dolkif.annotations.Qualify;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
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

    @Test
    public void testClassConstructorPublicWithNormalizeFields(){
        val mapConstructorAndFieldsWithGeneric = ClassUtils.getPublicConstructorWithNormalizeFields(ClassExample.class);
        val firstArgumentsMapGeneric = mapConstructorAndFieldsWithGeneric.entrySet().stream().findFirst().orElseThrow();
        Assert.assertEquals("Error get generic params", 1, firstArgumentsMapGeneric.getValue().length);

        val mapConstructorCorrectNotGeneric = ClassUtils.getPublicConstructorWithNormalizeFields(ClassInheritanceExample.class);
        val firstArgumentsMapNotGeneric = mapConstructorCorrectNotGeneric.entrySet().stream().findFirst().orElseThrow();
        Assert.assertEquals("Error dont get normalize params", 1, firstArgumentsMapNotGeneric.getValue().length);

    }

    private interface InterfaceExample{}
    private static class ClassExample<T> implements InterfaceExample{
        public ClassExample(T exampleParam){

        }
    }
    private static class ClassInheritanceExample extends ClassExample<String>{
        public ClassInheritanceExample(String exampleParam) {
            super(exampleParam);
        }
    }

}
