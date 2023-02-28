package org.dolkif.context;


import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.dolkif.utils.ApplicationProperties;
import org.dolkif.utils.DolkifFactory;

import java.util.*;
import java.util.logging.Level;


public class Loader implements ILoader{
    @Getter
    private final @NonNull IBeansContainer beansContainer;
    private final @NonNull ICheckerDependencies checkerDependencies;

    public Loader(final @NonNull IBeansContainer beansContainer, final @NonNull ICheckerDependencies checkerDependencies, final @NonNull Set<ReaderClass.Resource> resources){
        this.beansContainer = beansContainer;
        this.checkerDependencies = checkerDependencies;

    }
    public Loader(final @NonNull IBeansContainer beansContainer, final @NonNull ICheckerDependencies checkerDependencies){
        this.beansContainer = beansContainer;
        this.checkerDependencies = checkerDependencies;
    }

    @Override
    public <T> T newInstance(final @NonNull Bean.BeanReference<T> beanReference) {
        return null;
    }

    //TODO REQUIRED TESTING
    private void initBeansContainer(final @NonNull Set<ReaderClass.Resource> resources){
        Map<Bean.BeanReference<?>,List<Bean.BeanReference<?>>> mapRelationalShipFatherWithChildrenDependencies = null;
        try {
            //TODO Revision throw Exception
            mapRelationalShipFatherWithChildrenDependencies = checkerDependencies.parserClassToBeanReference(
                    checkerDependencies.filterResourceAvailableToInject(resources.stream().toList())
            );
        }catch (Exception e){
            DolkifFactory.getLogger().log(Level.WARNING,"Error initBeansContainer",e);
            return;
        }
        final Map<Bean.BeanReference<?>, List<Bean.BeanReference<?>>> singletons = new HashMap<>();
        final Map<Bean.BeanReference<?>, List<Bean.BeanReference<?>>> prototypes = new HashMap<>();
        orderMapsByScopePatter(mapRelationalShipFatherWithChildrenDependencies,singletons,prototypes);

    }

    private void orderMapsByScopePatter(final @NonNull Map<Bean.BeanReference<?>,List<Bean.BeanReference<?>>> allReferences,
                                        final @NonNull Map<Bean.BeanReference<?>, List<Bean.BeanReference<?>>> singletons,
                                        final @NonNull Map<Bean.BeanReference<?>, List<Bean.BeanReference<?>>> prototypes){
        for (val entryRelations:
                allReferences.entrySet()) {
            switch (getTypeScopePatternInBeanReference(entryRelations.getKey())){
                case PROTOTYPE -> prototypes.put(entryRelations.getKey(),entryRelations.getValue());
                case SINGLETON -> singletons.put(entryRelations.getKey(),entryRelations.getValue());
            }
        }
    }

    private Bean.ScopePattern getTypeScopePatternInBeanReference(final @NonNull Bean.BeanReference<?> beanReference){
       return Arrays.stream(beanReference.getAnnotationsLoaded())
                .filter(annotation -> annotation instanceof org.dolkif.annotations.Bean
                        || annotation instanceof org.dolkif.annotations.Component
                ).findFirst().map(annotation -> {
                    if(annotation instanceof org.dolkif.annotations.Bean)
                        return ((org.dolkif.annotations.Bean)annotation).scope();
                    else
                        return ((org.dolkif.annotations.Component)annotation).scope();
                }).orElse(ApplicationProperties.DEFAULT_SCOPE_PATTERN);
    }


}
