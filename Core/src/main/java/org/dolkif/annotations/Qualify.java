package org.dolkif.annotations;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.CONSTRUCTOR,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualify {
    String name = null;
}
