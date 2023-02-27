package org.dolkif.context;


import lombok.NonNull;
import lombok.val;
import org.dolkif.annotations.Autowired;
import org.dolkif.annotations.Component;
import org.dolkif.annotations.Configuration;
import org.dolkif.utils.beans.AnnotationUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class CheckerDependencies implements ICheckerDependencies {

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
                    && paramIsAvailableEqualsQualifierBeanConfiguration(parameter,beanBase.getConfiguration());
        } else if (beanBase instanceof Bean.Type<?>) {
            return paramsIsEqualAtType(parameter,((Bean.Type<?>) beanBase).getValue())
                    && paramIsAvailableEqualsQualifierBeanConfiguration(parameter,beanBase.getConfiguration());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public List<Bean.BeanReference<?>> getParamsRequired(@NonNull Executable executable) {
        return Arrays.stream(executable.getParameters())
                .map(param->new Bean.BeanReference<>(Bean.TypeReference.PARAMS_EXECUTABLE,param.getType(),param.getAnnotations()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Bean.BeanReference<?>> getFieldsRequired(@NonNull Class<?> classType) {
        return AnnotationUtils.getFieldsWithAnnotation(classType, Autowired.class)
                .stream()
                .map(field -> new Bean.BeanReference<>(Bean.TypeReference.FIELDS_CLASS,field.getType(),field.getAnnotations()))
                .collect(Collectors.toList());
    }

    @Override
    public Class<?> convertResourceInClass(ReaderClass.Resource resource) throws ClassNotFoundException {//TODO
        return ClassLoader.getSystemClassLoader().loadClass(resource.getResoucePath());
    }

    @Override
    public List<Class<?>> filterResourceAvailableToInject(List<ReaderClass.Resource> resources) throws ClassNotFoundException{ //TODO
        final List<Class<?>> classTypes = new ArrayList<>();
        for (ReaderClass.Resource resource: resources) {
            final Class<?> typeConverted = convertResourceInClass(resource);
            if(typeConverted.getAnnotation(Configuration.class) != null || typeConverted.getAnnotation(Component.class) != null)
                classTypes.add(typeConverted);
        }
        return classTypes;
    }



    private Optional<Map<Parameter,Bean.BeanBase<?>>> cleanParametersOptionals(final @NonNull Map<Parameter,Optional<Bean.BeanBase<?>>> mapParamsWithBeanBase) {
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

    private boolean paramIsAvailableEqualsQualifierBeanConfiguration(final @NonNull Parameter parameter, final @NonNull Bean.Configuration configurationBean){

        return Optional.ofNullable(parameter.getAnnotation(org.dolkif.annotations.Qualify.class))
                .map(qualify -> qualify.name().equals(configurationBean.getQualifierName()))
                .orElse(false);
    }
    private boolean paramsIsEqualAtType(final @NonNull Parameter parameter,final @NonNull Class<?> classType){
        boolean isEqualsTypeName = parameter.getType().getTypeName().equals(classType.getTypeName());

        if(!isEqualsTypeName && classType.getSuperclass() != null)
            isEqualsTypeName = paramsIsEqualAtType(parameter, classType.getSuperclass());

        return isEqualsTypeName;
    }
}
