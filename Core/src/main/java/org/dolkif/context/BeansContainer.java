package org.dolkif.context;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.dolkif.annotations.Qualify;
import org.dolkif.utils.beans.ClassUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@AllArgsConstructor
public final class BeansContainer implements IBeansContainer {

    private final Set<Bean.Instance<?>> singletonInstances; //TODO provisional
    private final Set<Bean.Type<?>> prototypeTypes; //TODO provisional

    public BeansContainer(){
        singletonInstances = new HashSet<>();
        prototypeTypes = new HashSet<>();
    }

    @Override
    public boolean addBean(final @NonNull Bean.BeanBase<?> beanBase) {
        //TODO Revision requirements BeanBaseConfiguration Pattern Scope
        if(beanBase instanceof Bean.Instance<?>)
            return singletonInstances.add((Bean.Instance<?>) beanBase);
        else if(beanBase instanceof Bean.Type<?>)
            return prototypeTypes.add((Bean.Type<?>) beanBase);
        else
            return false;
    }

    @Override
    public List<Bean.BeanBase<?>> getAllBeans() {
        return Stream.concat(singletonInstances.stream(),prototypeTypes.stream()).toList();
    }

    @Override
    public <T> List<Bean.BeanBase<?>> filterBean(Bean.BeanReference<T> beanReference) {
        val optionalAnnotationQualify = (Qualify) Arrays.stream(beanReference.getAnnotationsLoaded())
                .filter(annotation -> annotation instanceof Qualify)
                .findFirst()
                .orElse(null);

        return getAllBeans()
                .stream()
                .filter(beans -> isTypeAvailable(beanReference.getClassType(),optionalAnnotationQualify).test(beans))
                .toList();
    }

    @Override
    public <T> Optional<Bean.BeanBase<?>> findBean(Bean.BeanReference<T> beanReference) {
        return filterBean(beanReference).stream().findFirst();
    }

    private Predicate<Bean.BeanBase<?>> isTypeAvailable(final @NonNull Class<?> classTypeFind, final Qualify qualifyClassTypeFind){
        Predicate<Bean.BeanBase<?>> beanBasePredicate = beanBase -> {
            if(beanBase instanceof Bean.Type<?>)
                return ClassUtils.findClassInheritanceAndInterfaceImplementations(classTypeFind,((Bean.Type<?>) beanBase).getValue()).isPresent();
            else if(beanBase instanceof Bean.Instance<?>)
                return ClassUtils.findClassInheritanceAndInterfaceImplementations(classTypeFind,((Bean.Instance<?>)beanBase).getValue().getClass()).isPresent();
            else
                return false;
        };

        return qualifyClassTypeFind == null
                ? beanBasePredicate
                : beanBasePredicate.and(beanBase -> beanBase.getConfiguration().getQualifierName() != null
                    && beanBase.getConfiguration().getQualifierName().equals(qualifyClassTypeFind.name()));

    }
}
