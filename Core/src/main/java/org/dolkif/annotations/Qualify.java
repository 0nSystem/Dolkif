package org.dolkif.annotations;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.PARAMETER,ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualify {
    String name();
}
