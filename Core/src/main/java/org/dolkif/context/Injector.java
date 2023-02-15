package org.dolkif.context;


import lombok.NonNull;
import lombok.val;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Injector implements IInjector{

    @Override
    public Optional<Map<Parameter,Bean.BeanBase<?>>> getAvailableParamsCheckingExecutable(final @NonNull Executable executable, final @NonNull List<Bean.BeanBase<?>> classList){
        if(!checkAvailableExecutable(executable))
            return Optional.empty();

        return cleanParametersOptionals(getAvailableTypesToParameter(executable,classList));
    }
    @Override
    public boolean checkAvailableExecutable(final @NonNull Executable executable){
        if(!Modifier.isPublic(executable.getModifiers()))
            return false;

        if(executable instanceof Method){
            return Optional.ofNullable(executable.getAnnotation(org.dolkif.annotations.Bean.class)).isPresent()
                    && Optional.ofNullable(executable.getDeclaringClass().getDeclaredAnnotation(org.dolkif.annotations.Configuration.class)).isPresent();
        }else if(executable instanceof Constructor<?>){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean paramsIsAvailable(final @NonNull Parameter parameter,final @NonNull Bean.BeanBase<?> beanBase) throws UnsupportedOperationException{
        if (beanBase instanceof Bean.Instance<?>) {
            return paramsIsEqualAtType(parameter,((Bean.Instance<?>) beanBase).getValue().getClass())
                    && paramIsAvailableWithBeanConfiguration(parameter,beanBase.getConfiguration());
        } else if (beanBase instanceof Bean.Type<?>) {
            return paramsIsEqualAtType(parameter,((Bean.Type<?>) beanBase).getValue())
                    && paramIsAvailableWithBeanConfiguration(parameter,beanBase.getConfiguration());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private Optional<Map<Parameter,Bean.BeanBase<?>>> cleanParametersOptionals(final @NonNull Map<Parameter,Optional<Bean.BeanBase<?>>> mapParamsWithBeanBase) throws UnsupportedOperationException {
        val mapsAvailableParams = mapParamsWithBeanBase.entrySet().stream().filter(mapEntry -> mapEntry.getValue().isPresent());

        if (mapsAvailableParams.toList().size() != mapParamsWithBeanBase.entrySet().size())
            return Optional.empty();

        return Optional.of(mapParamsWithBeanBase.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().orElseThrow(() -> new UnsupportedOperationException("Argument empty can get() in Optional"))
                )
        ));
    }

    private Map<Parameter,Optional<Bean.BeanBase<?>>> getAvailableTypesToParameter(final @NonNull Executable executable, final @NonNull List<Bean.BeanBase<?>> classList){
        return Arrays.stream(executable.getParameters())
                .collect(Collectors.toMap(
                        parameter -> parameter,
                        parameter -> classList.stream().filter(beanBase -> paramsIsAvailable(parameter,beanBase)).findFirst()
                ));
    }

    private boolean paramIsAvailableWithBeanConfiguration(final @NonNull Parameter parameter,final @NonNull Bean.Configuration configurationBean){
        return Optional.ofNullable(parameter.getAnnotation(org.dolkif.annotations.Qualify.class))
                .map(qualify -> qualify.name().equals(configurationBean.getQualifierName()))
                .isPresent();
    }
    private boolean paramsIsEqualAtType(final @NonNull Parameter parameter, Class<?> classType){
        return parameter.getType().getTypeName().equals(classType.getTypeName());
    }

}
