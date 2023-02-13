package org.dolkif.utils.beans;



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



    @Component
    private static class WithAnnotation{}
    private static class InheritanceClassAnnotation extends WithAnnotation{}
}
