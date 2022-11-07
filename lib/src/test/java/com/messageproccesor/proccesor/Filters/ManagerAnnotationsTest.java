package com.messageproccesor.proccesor.Filters;


import com.messageproccesor.ClassToTest.HandlerSingletonTest;

import com.messageproccesor.annotations.Scope;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class ManagerAnnotationsTest {

     @Test
    public void getQualifyAnnotationTest( ) {

         Optional< Scope > scopeEmpty = FilterAnnotation.getAnnotation( Object.class, Scope.class );
         Optional< Scope > scopePresent = FilterAnnotation.getAnnotation( HandlerSingletonTest.class, Scope.class );

         Assert.assertTrue(scopeEmpty.isEmpty());
         Assert.assertTrue(scopePresent.isPresent());

    }
}
