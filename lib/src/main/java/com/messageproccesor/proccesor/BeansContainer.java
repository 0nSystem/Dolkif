package com.messageproccesor.proccesor;

import com.messageproccesor.enums.PatternScope;
import com.messageproccesor.model.IRepositoryProcessor;
import com.messageproccesor.model.IServiceProccesor;
import com.messageproccesor.proccesor.Filters.FilterAnnotation;
import com.messageproccesor.utils.Logger;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

@Getter
@NoArgsConstructor
public class BeansContainer implements IBeansContainer{


    private Map< Class <IServiceProccesor>, Set< Class <IRepositoryProcessor> > > allhandlerProcessorGroupingrepositories = new HashMap<>();
    private Set< Class< ? > > otherObjectsClass = new HashSet<>();

    private final Set< IServiceProccesor > handlerSingleton = new HashSet<>();
    private final Set< IRepositoryProcessor > repositorySingleton = new HashSet<>();
    private final Set< Object > otherObjectSingleton = new HashSet<>();

    public BeansContainer(Map< Class < IServiceProccesor >, Set< Class < IRepositoryProcessor > > > allhandlerProcessorGroupingrepositories,
                          Set< Class < ? > > otherObjectsClass) {
        this.allhandlerProcessorGroupingrepositories = allhandlerProcessorGroupingrepositories;
        this.otherObjectsClass = otherObjectsClass;
    }

    @Override
    public <T> boolean insertBean(Class<T> beanClass, PatternScope scope) {
        try{
            if( IServiceProccesor.class.equals(beanClass) ){
                if( PatternScope.SINGLETON == scope ){

                } else {

                }
            }
            if( IRepositoryProcessor.class.equals(beanClass) ){
                if( PatternScope.SINGLETON == scope ){

                }else{

                }
            }
            else{

            }
        }catch (Exception e){
            Logger.getLogger().log(Level.INFO,getClass().getName()+" Error cant instance bean");
            return false;
        }

        return true;
    }

    @Override
    public <T> boolean insertBeanSingletonInstance(T bean) {
        return false;
    }

    @Override
    public Set<Class<?>> allClassesFound() {
        Set< Class < ? > > allClassFound = new HashSet<>(this.otherObjectsClass);

        this.allhandlerProcessorGroupingrepositories.forEach((key, value) -> {
            allClassFound.add(key);
            allClassFound.addAll(value);
        });

        return allClassFound;
    }


    @Override
    public <T> Set<Class<T>> findClassByClassType(Class<T> type) {
        HashSet< Class < T > > hashSet = new HashSet();
        //FilterAnnotation.filterByPatternScopeAnnotationImpl(type,Sin)
        return hashSet;
    }

    @Override
    public <T> Set<Class<T>> findClassByClassType(Class<T> type, PatternScope scope) {
        return new HashSet<>();
    }


}
