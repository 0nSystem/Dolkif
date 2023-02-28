package org.dolkif.context;


import lombok.NonNull;
import lombok.val;
import org.dolkif.annotations.Autowired;
import org.dolkif.annotations.Component;
import org.dolkif.annotations.Configuration;
import org.dolkif.utils.beans.AnnotationUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Bean.TypeReference typeReference;
        if(executable instanceof Constructor<?>)
            typeReference = Bean.TypeReference.PARAMS_EXECUTABLE_CONSTRUCTOR;
        else if(executable instanceof  Method)
            typeReference = Bean.TypeReference.PARAMS_EXECUTABLE_METHOD;
        else {
            typeReference = Bean.TypeReference.NULL;
        }
        return Arrays.stream(executable.getParameters())
                .map(param->new Bean.BeanReference<>(typeReference,param.getType(),param.getAnnotations()))
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

    /**
     *
     * @param classTypes
     * @return this key component father bean and values is a dependence to instance bean
     */
    //TODO PENDING TO TEST
    @Override
    public Map<Bean.BeanReference<?>,List<Bean.BeanReference<?>>> parserClassToBeanReference(@NonNull List<Class<?>> classTypes) {
        Function<Class<?>, Map.Entry<Bean.BeanReference<?>,List<Bean.BeanReference<?>>>> functionParserClassTypesToBeanReference = classType -> {

            val beanBasePrincipal = new Bean.BeanReference(Bean.TypeReference.CLASS,classType, classType.getAnnotations());
            List<Bean.BeanReference<?>> beanReferencesChildren = getFieldsRequired(classType);
            val annotationComponent = classType.getAnnotation(Component.class);
            if(annotationComponent != null){
                for (val executable:
                     AnnotationUtils.getMethodsWithAnnotation(classType, org.dolkif.annotations.Bean.class)) {
                    beanReferencesChildren.addAll(this.getParamsRequired(executable));
                }
                return Map.entry(beanBasePrincipal,beanReferencesChildren);
            }
            val annotationConfiguration = classType.getAnnotation(Configuration.class);
            if(annotationConfiguration != null){
                for (val executable:
                        classType.getConstructors()) {
                    beanReferencesChildren.addAll(this.getParamsRequired(executable));
                }
                return Map.entry(beanBasePrincipal,beanReferencesChildren);
            }

            throw new UnsupportedOperationException("Error operation not contains required annotations ");
        };
        Map<Bean.BeanReference<?>,List<Bean.BeanReference<?>>> mapRelationshipFatherWithChildrenDependencies = new HashMap<>();
        classTypes.forEach(classType -> {
            val entry = functionParserClassTypesToBeanReference.apply(classType);
            mapRelationshipFatherWithChildrenDependencies.putIfAbsent(entry.getKey(),entry.getValue());
        });
        return mapRelationshipFatherWithChildrenDependencies;
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
