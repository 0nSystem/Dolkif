package com.messageproccesor.proccesor.Filters;

import com.messageproccesor.ClassToTest.HandlerSingletonTest;
import com.messageproccesor.ClassToTest.ObjectProccesedTest;
import com.messageproccesor.ClassToTest.RepositoryTest;
import com.messageproccesor.model.IRepositoryProcessor;
import com.messageproccesor.model.IServiceProccesor;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestFilterGenerics {

    @Test
    public void containsInterface(){
        Class < ? > classWithNotHasInterface = Object.class;
        Assert.assertFalse(
                FilterGenerics.containsInterface(classWithNotHasInterface,Object.class,true)
        );

        Class < ? > classWithHasInterface = RepositoryTest.class;
        Class < ? > interfaceRequired = IRepositoryProcessor.class;
        Assert.assertTrue(
                FilterGenerics.containsInterface(
                        classWithHasInterface,
                        interfaceRequired,
                        true)
        );
    }

    @Test
    public void filterByCompatibilityGenericParam(){

        List<ParameterizedType> notHasCompatibleParams = Arrays.stream(Object.class
                        .getGenericInterfaces())
                .map(a -> (ParameterizedType) a)
                .toList();
        Set< Class < Object > > setClassWithNotHaveCompatibleParams = Set.of( Object.class );
        Assert.assertTrue(
                FilterGenerics.filterByCompatibilityGenericParams(
                        notHasCompatibleParams,
                        setClassWithNotHaveCompatibleParams
                ).isEmpty()
        );

        List<ParameterizedType> paramsRequired = Arrays.stream(RepositoryTest.class.getGenericInterfaces())
                .map(a -> (ParameterizedType) a)
                .toList();
        Set< Class < HandlerSingletonTest > > listWithCompatibleGenericParams = new HashSet<>();
        listWithCompatibleGenericParams.add(HandlerSingletonTest.class);
        Assert.assertTrue(
                FilterGenerics.filterByCompatibilityGenericParams(
                        paramsRequired,
                        listWithCompatibleGenericParams
                ).isPresent()
        );
    }

    @Test
    public void filterByContainGenericParams(){
        Class < ObjectProccesedTest > objectRequiredContain = ObjectProccesedTest.class;

        Set < Class < Object > > listClassNotContainerObjectRequired = Set.of( Object.class );
        Assert.assertTrue(FilterGenerics
                .filterByContainGenericParams(
                        objectRequiredContain,
                        listClassNotContainerObjectRequired
                ).isEmpty()
        );

        Set< Class < HandlerSingletonTest > > listClassContainsObjectGenericRequired = Set.of( HandlerSingletonTest.class );
        Assert.assertTrue(FilterGenerics
                .filterByContainGenericParams(
                        objectRequiredContain,
                        listClassContainsObjectGenericRequired
                ).isPresent()
        );
    }
}
