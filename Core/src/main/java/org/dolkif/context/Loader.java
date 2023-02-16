package org.dolkif.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
public class Loader implements ILoader{
    @Getter
    private final @NonNull IBeansContainer beansContainer;
    private final @NonNull ICheckerDependencies checkerDependencies;


    @Override
    public <T> T newInstance(Class<T> classType) {
        return null;
    }

}
