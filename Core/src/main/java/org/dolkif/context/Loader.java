package org.dolkif.context;


import lombok.Getter;
import lombok.NonNull;

import java.util.Set;


public class Loader implements ILoader{
    @Getter
    private final @NonNull IBeansContainer beansContainer;
    private final @NonNull ICheckerDependencies checkerDependencies;

    public Loader(final @NonNull IBeansContainer beansContainer, final @NonNull ICheckerDependencies checkerDependencies, final @NonNull Set<ReaderClass.Resource> resources){
        this.beansContainer = beansContainer;
        this.checkerDependencies = checkerDependencies;


    }


    @Override
    public <T> T newInstance(final @NonNull Class<T> classType) {
        return null;
    }


}
