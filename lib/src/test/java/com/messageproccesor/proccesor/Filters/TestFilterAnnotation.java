package com.messageproccesor.proccesor.Filters;

import com.messageproccesor.ClassToTest.ComponentTest;
import com.messageproccesor.ClassToTest.HandlerSingletonTest;
import com.messageproccesor.ClassToTest.RepositoryTest;
import com.messageproccesor.annotations.HeaderFilter;
import com.messageproccesor.annotations.Qualify;
import com.messageproccesor.enums.PatternScope;
import com.messageproccesor.proccesor.ProcessExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/*
FilterAnnotation.filterByQualifyAnnotationAndGenericParamsCompatible
    revision
 */
public class TestFilterAnnotation {

    @Test
    //With Actual Pattern deafult is Singleton
    public void testPatternScopeAnnotationImplNotFound(){
        Class< ? > tClass = Object.class;

        if(!Arrays.stream(tClass.getAnnotations())
                .anyMatch(a-> a.annotationType().equals(Qualify.class))){
            Assert.assertTrue(
                    FilterAnnotation.
                            filterByPatternScopeAnnotationImpl(tClass, ProcessExecutor.DEFAULT_PATTERN_SCOPE));
        }

        Assert.assertFalse(
                FilterAnnotation.filterByPatternScopeAnnotationImpl(tClass,PatternScope.PROTOTYPE)
        );

    }
    @Test
    public void testPatternScopeAnnotationImplFound(){
        Class< ? > tClass = HandlerSingletonTest.class;
        Assert.assertTrue(
                FilterAnnotation.filterByPatternScopeAnnotationImpl(tClass, PatternScope.SINGLETON));
        Assert.assertFalse(
                FilterAnnotation.filterByPatternScopeAnnotationImpl(tClass, PatternScope.PROTOTYPE));
    }

    @Test
    public void testAnnotationFilterHeader(){
        Class< ? > objectWithNotContainerHeaderFilter = Object.class;
        Assert.assertFalse(FilterAnnotation
                .filterByAnnotationFilterHeader("NotExist",objectWithNotContainerHeaderFilter)
        );

        Class < ? > repositoryWithHeaderFilter = RepositoryTest.class;
        Annotation annotation = Arrays.stream(repositoryWithHeaderFilter.getAnnotations())
                .filter(a -> a.annotationType().equals(HeaderFilter.class))
                .findFirst()
                .orElseThrow();
        HeaderFilter headerFilter = (HeaderFilter) annotation;
        Assert.assertTrue(
                FilterAnnotation.filterByAnnotationFilterHeader(
                        headerFilter.header(),
                        repositoryWithHeaderFilter
                )
        );
    }

    @Test
    public void filterAnnotationComponent(){
        Class < ? > classNotFoundAnnotationComponent = Object.class;
        Assert.assertFalse(FilterAnnotation
                .filterByAnnotationComponent(classNotFoundAnnotationComponent)
        );

        Class < ? > classWithAnnotationComponent = ComponentTest.class ;
        Assert.assertTrue(FilterAnnotation
                .filterByAnnotationComponent(classWithAnnotationComponent));
    }

    @Test
    public void filterByQualifyAnnotation(){
        Class < ? > classNotFoundQualifyAnnotation = Object.class;
        Assert.assertTrue(
                FilterAnnotation.filterQualifyAnnotation(classNotFoundQualifyAnnotation).isEmpty());

        Class < ? > classWithQualifyAnnotation = RepositoryTest.class;
        Assert.assertTrue(
                FilterAnnotation.filterQualifyAnnotation(classWithQualifyAnnotation).isPresent());
    }

}

