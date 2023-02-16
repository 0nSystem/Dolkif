package org.dolkif.annotations;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
    org.dolkif.context.Bean.ScopePattern scope() default org.dolkif.context.Bean.ScopePattern.PROTOTYPE;
    String nameBean() default "";
}
