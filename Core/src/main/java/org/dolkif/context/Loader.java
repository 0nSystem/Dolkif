package org.dolkif.context;


import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.dolkif.annotations.Component;
import org.dolkif.annotations.Configuration;
import org.dolkif.utils.ApplicationProperties;
import org.dolkif.utils.DolkifFactory;
import org.dolkif.utils.beans.AnnotationUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;


public class Loader implements ILoader{
    @Getter
    private final @NonNull IBeansContainer beansContainer;
    private final @NonNull ICheckerDependencies checkerDependencies;

    public Loader(final @NonNull IBeansContainer beansContainer, final @NonNull ICheckerDependencies checkerDependencies, final @NonNull Set<ReaderClass.Resource> resources) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        this.beansContainer = beansContainer;
        this.checkerDependencies = checkerDependencies;
        loadBeanContainer(resources);
    }
    public Loader(final @NonNull IBeansContainer beansContainer, final @NonNull ICheckerDependencies checkerDependencies){
        this.beansContainer = beansContainer;
        this.checkerDependencies = checkerDependencies;
    }

    @Override
    public <T> T getInstance(final @NonNull Class<T> classType) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        val optionalFindBeanByType = this.beansContainer.findBean(classType);
        if(optionalFindBeanByType.isPresent()){
            if(optionalFindBeanByType.get() instanceof Bean.Instance<?>)
                return ((Bean.Instance<T>)optionalFindBeanByType.get()).getValue();
            else if(optionalFindBeanByType.get() instanceof Bean.Type<?>)
                //TODO Peding double operation in instance
                return loadBeanContainerWithComponentAnnotation(classType).getValue();
        }
        return loadBeanContainerWithComponentAnnotation(classType).getValue();
    }
    private void loadBeanContainer(final @NonNull Set<ReaderClass.Resource> resources) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        List<Class<?>> classTypes = null;
        try {
            classTypes = this.checkerDependencies.filterResourceAvailableToInject(resources.stream().toList()); //TODO cant remove this methods
        }catch (Exception e){
            DolkifFactory.getLogger().log(Level.WARNING,"Error loadBeanContainer parser resources to Class", e);
            return;
        }
        for (val classType : classTypes) {
            val optionalComponentAnnotation = Optional.ofNullable(classType.getAnnotation(Component.class));
            if(optionalComponentAnnotation.isPresent()){
                loadBeanContainerWithComponentAnnotation(classType);
                continue;
            }
            val optionalConfigurationAnnotation = Optional.ofNullable(classType.getAnnotation(Configuration.class));
            if(optionalConfigurationAnnotation.isPresent()){
                loadBeanContainerWithConfigurationAnnotation(classType);
            }
        }

    }
    //TODO pending order first execution ConfigurationAnnotation
    //TODO Add Annotation Primary Constructor to specify constructor in multiples options
    private <T> Bean.BeanBase<T> loadBeanContainerWithComponentAnnotation(final @NonNull Class<T> classType) throws InvocationTargetException, IllegalAccessException, InstantiationException,RuntimeException {
        val optionalComponentAnnotation = Optional.ofNullable(classType.getAnnotation(Component.class));
        val scopePattern = optionalComponentAnnotation
                .map(Component::scope)
                .orElse(ApplicationProperties.DEFAULT_SCOPE_PATTERN);
        val nameBean = optionalComponentAnnotation
                .filter(component -> !component.nameBean().isBlank())
                .map(Component::nameBean)
                .orElse(classType.getTypeName());

        switch (scopePattern){
            case PROTOTYPE -> {
                return (Bean.BeanBase<T>) Bean.BeanBase.of(new Bean.Configuration(nameBean,scopePattern),classType,classType.getConstructors());
            }
            case SINGLETON -> {
                //TODO REQUIRED REFACTOR FUNCTION TO FUNCTION
                val optionalEmptyEntryConstructor = Arrays.stream(classType.getDeclaredConstructors())
                        .filter(c -> this.checkerDependencies.executableIsEmptyParams(c)) //
                        .findFirst()
                        .map(constructor -> new AbstractMap.SimpleEntry<>(constructor, Map.of()));
                T beanInstance = null;
                if(optionalEmptyEntryConstructor.isPresent())
                    beanInstance = instance(classType.getDeclaredConstructors()[0], Map.of());
                else{
                    val paramsDependenciesToLoad = Arrays.stream(classType.getConstructors())
                            .map(constructor -> new AbstractMap.SimpleEntry<>(constructor,this.checkerDependencies.getAvailableParamsCheckingExecutable(constructor,this.beansContainer.getAllBeans())))
                            .filter(constructorOptionalSimpleEntry -> constructorOptionalSimpleEntry.getValue().isPresent())
                            .findFirst()
                            .map(constructorOptionalSimpleEntry -> new AbstractMap.SimpleEntry<>(constructorOptionalSimpleEntry.getKey(),constructorOptionalSimpleEntry.getValue().get()))
                            .orElseThrow(() -> new RuntimeException("Error finding constructor available"));
                    beanInstance = instance(paramsDependenciesToLoad.getKey(),paramsDependenciesToLoad.getValue());
                }
                loadFieldsRequirements(beanInstance);
                return Bean.BeanBase.of(new Bean.Configuration(nameBean,scopePattern),beanInstance,null);
            }
        }
        throw new UnsupportedOperationException("Error not process in scope pattern");
    }

    private <T> List<Bean.BeanBase<T>> loadBeanContainerWithConfigurationAnnotation(final @NonNull Class<T> classType) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        //TODO require load fields requirements before load or inject in methods pending revision
        final List<Bean.BeanBase<T>>listBeanInstance = new ArrayList<>();
        for (val method: AnnotationUtils.getMethodsWithAnnotation(classType,org.dolkif.annotations.Bean.class)) {
            val beanAnnotation = Optional.ofNullable(method.getAnnotation(org.dolkif.annotations.Bean.class)).orElseThrow(() -> new RuntimeException("Error not found Bean Annotation"));
            val nameBean = beanAnnotation.nameBean().isBlank()?method.getName():beanAnnotation.nameBean();
            final Map<Parameter, Bean.BeanBase<?>> paramsExecutable = this.checkerDependencies.executableIsEmptyParams(method)
                        ?   Map.of()
                        :  this.checkerDependencies.getAvailableParamsCheckingExecutable(method, this.beansContainer.getAllBeans())
                            .orElseThrow(() -> new RuntimeException(String.format("Error haven't available params to execute method %s", method)));

            final T beanInstance = instance(method, paramsExecutable);
            loadFieldsRequirements(beanInstance);
            listBeanInstance.add(Bean.BeanBase.of(new Bean.Configuration(nameBean,beanAnnotation.scope()),beanInstance,null));
        }
        return listBeanInstance;
    }

    private <T> T instance(final @NonNull Executable executable, final @NonNull Map<Parameter, Bean.BeanBase<?>> mapParamsWithBeanType) throws InvocationTargetException, IllegalAccessException, UnsupportedOperationException, InstantiationException {
        //TODO Required instace if is Bean.Type
        List<Object> objectsParameters = new ArrayList<>();
        Object instanceFatherMethod = null;
        if(executable instanceof Method)
            instanceFatherMethod = getInstance(executable.getDeclaringClass());
        for (val bean:
             mapParamsWithBeanType.values()) {
            if(bean instanceof Bean.Instance<?>)
                objectsParameters.add(((Bean.Instance<?>) bean).getValue());
            else if(bean instanceof Bean.Type<?>)
                objectsParameters.add(this.getInstance(((Bean.Type<?>) bean).getValue()));
        }
        if(executable instanceof Method){
            return (T)((Method) executable).invoke(instanceFatherMethod,objectsParameters);
        }else if(executable instanceof Constructor<?>){
            return objectsParameters.isEmpty()
                    ? ((Constructor<T>) executable).newInstance()
                    : ((Constructor<T>) executable).newInstance(objectsParameters);
        }
        throw new UnsupportedOperationException(String.format("Error cant instance executable: %s",executable));
    }

    private <T> void loadFieldsRequirements(T object){
        //TODO
    }

}
