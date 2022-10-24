package com.messageproccesor.annotations;

import com.messageproccesor.HandlerTest;
import com.messageproccesor.model.IHandlerProcessor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Use this annotation in implementation {@link com.messageproccesor.model.IRepositoryProcessor} to create specific relation with implementation {@link IHandlerProcessor}
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Qualify{
    Class<?> name();
}
