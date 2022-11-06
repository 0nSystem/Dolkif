package com.messageproccesor.annotations;

import com.messageproccesor.enums.PatternScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Scope {
    PatternScope pattern() default PatternScope.SINGLETON;
}
