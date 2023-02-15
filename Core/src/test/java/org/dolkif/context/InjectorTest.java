package org.dolkif.context;

import lombok.val;
import org.dolkif.ScopePattern;
import org.dolkif.annotations.Bean;
import org.dolkif.annotations.Configuration;
import org.dolkif.annotations.Qualify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Parameter;
import java.util.List;

public class InjectorTest {


    @Test
    public void testCheckAvalibleExecutable(){
        IInjector injector = new Injector();

        boolean methodInstanceBeanIsValid = injector.checkAvailableExecutable(ConfigurationBean.class.getMethods()[0]);
        boolean methodInstanceBeanIsNotValidByNotComponentAnnotation = injector.checkAvailableExecutable(ErrorConfigurationBeanNotComponentAnnotation.class.getDeclaredMethods()[0]);
        boolean methodInstaceBeanIsNotValidByNotBeanAnnotation = injector.checkAvailableExecutable(ErrorConfigurationBeanNotBeanAnnotation.class.getDeclaredMethods()[0]);

        Assertions.assertAll(() -> {
            Assertions.assertTrue(methodInstanceBeanIsValid);
            Assertions.assertFalse(methodInstanceBeanIsNotValidByNotComponentAnnotation);
            Assertions.assertFalse(methodInstaceBeanIsNotValidByNotBeanAnnotation);
        });
    }

    @Test
    public void testParamsIsAvailable(){
        IInjector injector = new Injector();
        val beanType = new org.dolkif.context.Bean.Type<>(
                new org.dolkif.context.Bean.Configuration("nameBean", ScopePattern.SINGLETON), String.class
        );

        boolean paramIsValid =  injector.paramsIsAvailable(
                ConfigurationBean.class.getDeclaredMethods()[0].getParameters()[0],
                beanType
        );
        boolean paramNotValidNotFoundQualify = injector.paramsIsAvailable(
                ErrorConfigurationBeanNotQualifyAnnotation.class.getDeclaredMethods()[0].getParameters()[0],
                beanType
        );

        Assertions.assertAll(() -> {
            Assertions.assertTrue(paramIsValid);
            Assertions.assertFalse(paramNotValidNotFoundQualify);
        });
    }

    @Test
    public void testGetAvailableParamsCheckingExecutable(){
        IInjector injector = new Injector();
        List<org.dolkif.context.Bean.BeanBase<?>> beanBases = List.of(
                new org.dolkif.context.Bean.Type<>(
                        new org.dolkif.context.Bean.Configuration("nameBean", ScopePattern.SINGLETON),
                        String.class
                )
        );

        val presentOptionalMapParamsWithBeanBase = injector.getAvailableParamsCheckingExecutable(
                ConfigurationBean.class.getDeclaredMethods()[0],beanBases
        );
        val emptyOptionalMapParamsWithBeanBase = injector.getAvailableParamsCheckingExecutable(
                ErrorConfigurationWithMethodMultiplesParams.class.getDeclaredMethods()[0],beanBases
        );



        Assertions.assertAll(() -> {
            Assertions.assertTrue(presentOptionalMapParamsWithBeanBase.isPresent());
            Assertions.assertEquals(1, presentOptionalMapParamsWithBeanBase.get().size());
            Assertions.assertTrue(emptyOptionalMapParamsWithBeanBase.isEmpty());
        });

    }


    @Configuration
    private static class ConfigurationBean {
        @Bean
        public String exampleExecutionMethod(@Qualify(name = "nameBean") String nameBean){
            return "Example Return";
        }
    }

    private static class ErrorConfigurationBeanNotComponentAnnotation {
        @Bean
        public String exampleExecutionMethod(@Qualify(name = "nameBean") String nameBean){
            return "Example Return";
        }
    }
    @Configuration
    private static class ErrorConfigurationBeanNotBeanAnnotation{
        public String exampleExecutionMethod(@Qualify(name = "nameBean") String nameBean){
            return "Example Return";
        }
    }
    @Configuration
    private static class ErrorConfigurationBeanNotQualifyAnnotation{
        @Bean
        public String exampleExecutionMethod(String nameBean){
            return "Example Return";
        }
    }
    @Configuration
    private static class ErrorConfigurationWithMethodMultiplesParams {
        @Bean
        public String exampleExecutionMethod(@Qualify(name = "nameBean") String nameBean, @Qualify(name = "exampleSecondParam") String exampleSecondParam){
            return "Example Return";
        }
    }
}
