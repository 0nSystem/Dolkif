package org.dolkif.context;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class Loader implements ILoader{
    private final @NonNull IBeansContainer beansContainer;
    private final @NonNull IInjector injector;


    @Override
    public <T> T newInstance(Class<T> classType) {
        return null;
    }

    @Override
    public IBeansContainer getBeansContainer() {
        return beansContainer;
    }
}
