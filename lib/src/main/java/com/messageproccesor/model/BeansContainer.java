package com.messageproccesor.model;

import lombok.Getter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class BeansContainer {


    private final Map< Class < IServiceProccesor >, Set< Class < IRepositoryProcessor > >> allhandlerProcessorGroupingrepositories;
    private final Set< Class< ? > > otherObjectsClass;

    private final Set< IServiceProccesor > handlerSingleton = new HashSet<>();
    private final Set< IRepositoryProcessor > repositorySingleton = new HashSet<>();
    private final Set< Object > otherObjectSingleton = new HashSet<>();

    public BeansContainer(Map< Class < IServiceProccesor >, Set< Class < IRepositoryProcessor > > > allhandlerProcessorGroupingrepositories,
                          Set< Class < ? > > otherObjectsClass) {
        this.allhandlerProcessorGroupingrepositories = allhandlerProcessorGroupingrepositories;
        this.otherObjectsClass = otherObjectsClass;
    }

    public  Set< Class < ? > > allClassesFound (){

        Set< Class < ? > > allClassFound = new HashSet<>(this.otherObjectsClass);

        this.allhandlerProcessorGroupingrepositories.forEach((key, value) -> {
            allClassFound.add(key);
            allClassFound.addAll(value);
        });

        return allClassFound;
    }
}
