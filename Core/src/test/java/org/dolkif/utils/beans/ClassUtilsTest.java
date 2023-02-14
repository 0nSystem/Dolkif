package org.dolkif.utils.beans;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Optional;

public class ClassUtilsTest {


    @Test
    public void testFindClassInheritanceAndInterfaceImplementations(){
        Optional<Class<String>> errorClassNotContains = ClassUtils.findClassInheritanceAndInterfaceImplementations(String.class,ClassExample.class);
        Optional<Class<ClassExample>> classExample = ClassUtils.findClassInheritanceAndInterfaceImplementations(ClassExample.class, ClassExample.class);
        Optional<Class<InterfaceExample>> classExampleImplementInterfaceExample = ClassUtils.findClassInheritanceAndInterfaceImplementations(InterfaceExample.class,ClassExample.class);
        Optional<Class<ClassExample>> classInheritanceExample = ClassUtils.findClassInheritanceAndInterfaceImplementations(ClassExample.class,ClassInheritanceExample.class);

        Assertions.assertAll(() -> {
            Assertions.assertTrue(errorClassNotContains.isEmpty());
            Assertions.assertTrue(classExample.isPresent());
            Assertions.assertTrue(classExampleImplementInterfaceExample.isPresent());
            Assertions.assertTrue(classInheritanceExample.isPresent());
        });
    }

    @Test
    public void testClassConstructorPublicWithNormalizeFields(){
        val mapConstructorAndFieldsWithGeneric = ClassUtils.getPublicConstructorWithNormalizeFields(ClassExample.class);
        val firstArgumentsMapGeneric = mapConstructorAndFieldsWithGeneric.entrySet().stream().findFirst().orElseThrow();

        val mapConstructorCorrectNotGeneric = ClassUtils.getPublicConstructorWithNormalizeFields(ClassInheritanceExample.class);
        val firstArgumentsMapNotGeneric = mapConstructorCorrectNotGeneric.entrySet().stream().findFirst().orElseThrow();

        Assertions.assertAll(() -> {
            Assertions.assertEquals(1,firstArgumentsMapGeneric.getValue().length);
            Assertions.assertEquals(1,firstArgumentsMapNotGeneric.getValue().length);
        });
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
