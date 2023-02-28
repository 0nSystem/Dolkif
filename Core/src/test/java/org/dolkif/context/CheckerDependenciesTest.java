package org.dolkif.context;

import lombok.val;
import org.dolkif.annotations.*;
import org.dolkif.annotations.Bean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class CheckerDependenciesTest {


    @Test
    public void testCheckAvalibleExecutable(){
        ICheckerDependencies injector = new CheckerDependencies();

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
        ICheckerDependencies injector = new CheckerDependencies();
        val beanType = new org.dolkif.context.Bean.Type<>(
                new org.dolkif.context.Bean.Configuration("nameBean", org.dolkif.context.Bean.ScopePattern.SINGLETON),
                String.class,
                null
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
        ICheckerDependencies injector = new CheckerDependencies();
        List<org.dolkif.context.Bean.BeanBase<?>> beanBases = List.of(
                new org.dolkif.context.Bean.Type<>(
                        new org.dolkif.context.Bean.Configuration("nameBean", org.dolkif.context.Bean.ScopePattern.SINGLETON),
                        String.class,
                        null
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

    @Test
    public void testGetParamsRequired(){
        ICheckerDependencies checkerDependencies = new CheckerDependencies();
        val classTypeReferenceConstructor = checkerDependencies.getParamsRequired(ConfigurationBean.class.getConstructors()[0]);
        val classTypeReferenceMethod = checkerDependencies.getParamsRequired(ConfigurationBean.class.getDeclaredMethods()[0]);

        Assertions.assertAll(() -> {
            Assertions.assertTrue(classTypeReferenceConstructor.isEmpty());
            Assertions.assertTrue(classTypeReferenceMethod.stream().findFirst().isPresent());

            Assertions.assertEquals(String.class,classTypeReferenceMethod.stream().findFirst().get().getClassType());
            Assertions.assertEquals(org.dolkif.context.Bean.TypeReference.PARAMS_EXECUTABLE,classTypeReferenceMethod.stream().findFirst().get().getTypeReference());
            Assertions.assertEquals(Qualify.class,classTypeReferenceMethod.stream().findFirst().get().getAnnotationsLoaded()[0].annotationType());
            Assertions.assertEquals("nameBean",((Qualify)classTypeReferenceMethod.stream().findFirst().get().getAnnotationsLoaded()[0]).name());
        });
    }

    @Test
    public void testGetFieldsRequired(){
        ICheckerDependencies checkerDependencies = new CheckerDependencies();
        val fieldToScan = checkerDependencies.getFieldsRequired(ConfigurationBean.class).stream().findFirst();

        Assertions.assertAll(() -> {
            Assertions.assertTrue(fieldToScan.isPresent());
            Assertions.assertEquals(Integer.class,fieldToScan.get().getClassType());
        });
    }
    @Test
    public void testConvertResourceToClass(){
        ICheckerDependencies checkerDependencies = new CheckerDependencies();
        try {
            checkerDependencies.convertResourceInClass(new ReaderClass.Resource(String.class.getTypeName()));
        }catch (Exception e){
            Assertions.fail(e.getMessage());
        }
    }
    @Test
    public void testFilterValidAndConvertResourceToClass(){
        ICheckerDependencies checkerDependencies = new CheckerDependencies();
        try {
            val classTypesAvailable = checkerDependencies.filterResourceAvailableToInject(List.of(
                    new ReaderClass.Resource(ConfigurationBean.class.getTypeName()),
                    new ReaderClass.Resource(ComponentConfiguration.class.getTypeName()),
                    new ReaderClass.Resource(String.class.getTypeName())
            ));
            Assertions.assertAll(() -> {
                Assertions.assertEquals(2, classTypesAvailable.size());
            });
        }catch (Exception e){
            Assertions.fail(e.getMessage());
        }
    }


    @Configuration
    private static class ConfigurationBean {

        @Autowired
        private Integer numberToScan;
        public ConfigurationBean(){

        }
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

    @Component
    private static class ComponentConfiguration{}
}
