package org.dolkif.utils.beans;



import lombok.val;
import org.dolkif.annotations.Autowired;
import org.dolkif.annotations.Bean;
import org.dolkif.annotations.Component;
import org.dolkif.annotations.Qualify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class AnnotationUtilsTest {

    @Test
    public void testContainsAnnotationInClass(){

        Optional<Component> annotationOwn = Optional.ofNullable(WithAnnotation.class.getAnnotation(Component.class));
        Optional<Component> inheritanceAnnotation = Optional.ofNullable(InheritanceClassAnnotation.class.getAnnotation(Component.class));

        Assertions.assertAll(() -> {
            Assertions.assertTrue(annotationOwn.isPresent());
            Assertions.assertTrue(inheritanceAnnotation.isPresent());
        });
    }

    @Test
    public void testContainsAnnotationInMethod(){
        Optional<Bean> methodAnnotationBeanOwn = Optional.ofNullable(WithAnnotation.class.getDeclaredMethods()[0].getAnnotation(Bean.class));
        val methodWithAnnotationInheritance = AnnotationUtils.getMethodsWithAnnotation(InheritanceClassAnnotation.class,Bean.class).stream().findFirst();

        Assertions.assertAll(() -> {
            Assertions.assertTrue(methodAnnotationBeanOwn.isPresent());
            Assertions.assertTrue(methodWithAnnotationInheritance.isPresent());
            Assertions.assertTrue(Optional.ofNullable(methodWithAnnotationInheritance.get().getAnnotation(Bean.class)).isPresent());
        });
    }

    @Test
    public void testContainsAnnotationInField(){
        Optional<Autowired> annotationBeanOwn = Optional.ofNullable(WithAnnotation.class.getDeclaredFields()[0].getAnnotation(Autowired.class));
        val fieldsWithAnnotationInheritance = AnnotationUtils.getFieldsWithAnnotation(WithAnnotation.class,Autowired.class).stream().findFirst();

        Assertions.assertAll(() -> {
            Assertions.assertTrue(annotationBeanOwn.isPresent());
            Assertions.assertTrue(fieldsWithAnnotationInheritance.isPresent());
            Assertions.assertTrue(Optional.ofNullable(fieldsWithAnnotationInheritance.get().getAnnotation(Autowired.class)).isPresent());
        });
    }



    @Component
    private static class WithAnnotation{
        @Qualify(name = "asd")
        @Autowired
        private String exampleToFieldAnnotation;

        @Bean
        private void exampleMethod(){
            return;
        }
    }
    private static class InheritanceClassAnnotation extends WithAnnotation{}
}
