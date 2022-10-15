package com.messageproccesor.annotations;

import com.messageproccesor.model.RepositoryProcessor;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Proccesor {
}
