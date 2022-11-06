package com.messageproccesor.proccesor.Filters;

import com.messageproccesor.enums.PatternScope;
import org.junit.Assert;
import org.junit.Test;

public class TestFilterAnnotation {

    @Test
    public void notFoundAnnotations(){
        Class< ? > tClass = Object.class;

        try{
            FilterAnnotation.filterByPatternScopeAnnotationImpl(tClass, PatternScope.PROTOTYPE);
            Assert.fail();
        }catch (Exception e){
            Assert.assertTrue(true);
        }

        

    }


}
