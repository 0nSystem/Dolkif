package com.messageproccesor.annotations;

import com.messageproccesor.model.IServiceProccesor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Use this annotation in implementation {@link com.messageproccesor.model.IRepositoryProcessor} to create specific relation with implementation {@link IServiceProccesor}
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Qualify{
    Class<?> name();
}
