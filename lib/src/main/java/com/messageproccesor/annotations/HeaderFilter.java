package com.messageproccesor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use into implementation {@link com.messageproccesor.model.IRepositoryProcessor} point header in {@link com.messageproccesor.model.IObjetToProcessed}
 * @see com.messageproccesor.model.IRepositoryProcessor
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface HeaderFilter {
    String header();
}
