package com.messageproccesor.proccesor;

import com.messageproccesor.enums.PatternScope;

import java.util.Set;

public interface IBeansContainer {

    < T > boolean insertBean(Class < T > beanClass, PatternScope scope);
    < T > boolean insertBeanSingletonInstance( T bean );

    Set < Class < ? > > allClassesFound();

    < T > Set < Class < T > > findClassByClassType( Class < T > type );
    < T > Set< Class < T > > findClassByClassType( Class < T > type, PatternScope scope);

}
