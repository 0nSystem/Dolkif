package com.messageproccesor.proccesor.Filters;

import com.messageproccesor.ClassToTest.ComponentTest;
import com.messageproccesor.ClassToTest.HandlerSingletonTest;
import com.messageproccesor.ClassToTest.RepositoryTest;
import com.messageproccesor.annotations.HeaderFilter;
import com.messageproccesor.annotations.Qualify;
import com.messageproccesor.annotations.Scope;
import com.messageproccesor.enums.PatternScope;
import com.messageproccesor.proccesor.ProcessExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;

/*
FilterAnnotation.filterByQualifyAnnotationAndGenericParamsCompatible
    revision
 */
public class TestFilterAnnotation {

    @Test
    public void testGetAnnotation(){
        Optional<Scope> scopeEmpty = FilterAnnotation.getAnnotation( Object.class, Scope.class );
        Optional< Scope > scopePresent = FilterAnnotation.getAnnotation( HandlerSingletonTest.class, Scope.class );

        Assert.assertTrue(scopeEmpty.isEmpty());
        Assert.assertTrue(scopePresent.isPresent());
    }

}

