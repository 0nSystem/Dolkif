package org.dolkif.annotations;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.PARAMETER,ElementType.METHOD,ElementType.FIELD,ElementType.TYPE_PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualify {
    String name();
}
