package org.dolkif.annotations;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component{
    org.dolkif.context.Bean.ScopePattern scope() default org.dolkif.context.Bean.ScopePattern.SINGLETON;
    String nameBean() default "";
}
