package org.dolkif.utils.beans;



import lombok.val;
import org.dolkif.annotations.Autowired;
import org.dolkif.annotations.Bean;
import org.dolkif.annotations.Component;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class AnnotationUtilsTest {

    @Test
    public void testContainsAnnotationInClass(){

        Optional<Component> annotationOwn = AnnotationUtils.getAnnotation(WithAnnotation.class, Component.class);
        Assert.assertTrue( "Error read Annotation in class: WithAnnotation.class with declaration annotation: @Component", annotationOwn.isPresent());

        Optional<Component> inheritanceAnnotation = AnnotationUtils.getAnnotation(InheritanceClassAnnotation.class, Component.class);
        Assert.assertTrue("Error read Annotation in class: InheritanceClassAnnotation.class with inheritanceAnnotation annotation: @Component",inheritanceAnnotation.isPresent());

    }

    @Test
    public void testContainsAnnotationInMethod(){
        Optional<Bean> methodAnnotationBeanOwn = AnnotationUtils.getAnnotation(WithAnnotation.class.getDeclaredMethods()[0],Bean.class);
        Assert.assertTrue("Error annotation bean not found", methodAnnotationBeanOwn.isPresent());

        val methodWithAnnotationInheritance = AnnotationUtils.getMethodsWithAnnotation(InheritanceClassAnnotation.class,Bean.class).stream().findFirst();
        Assert.assertTrue("Error annotation bean not found in Inheritance", methodWithAnnotationInheritance.isPresent());
        Assert.assertTrue("Error annotation getting annotation", AnnotationUtils.getAnnotation(methodWithAnnotationInheritance.get(),Bean.class).isPresent());
    }

    @Test
    public void testContainsAnnotationInField(){
        Optional<Autowired> annotationBeanOwn = AnnotationUtils.getAnnotation(WithAnnotation.class.getDeclaredFields()[0],Autowired.class);
        Assert.assertTrue("Error annotation in own field not found ", annotationBeanOwn.isPresent());

        val fieldsWithAnnotationInheritance = AnnotationUtils.getFieldsWithAnnotation(WithAnnotation.class,Autowired.class).stream().findFirst();
        Assert.assertTrue("Error not found annotation in field inheritance", fieldsWithAnnotationInheritance.isPresent());
        Assert.assertTrue("Error annotation in inherance field not found", AnnotationUtils.getAnnotation(fieldsWithAnnotationInheritance.get(),Autowired.class).isPresent());
    }



    @Component
    private static class WithAnnotation{
        @Autowired
        private String exampleToFieldAnnotation;

        @Bean
        private void exampleMethod(){
            return;
        }
    }
    private static class InheritanceClassAnnotation extends WithAnnotation{}
}
