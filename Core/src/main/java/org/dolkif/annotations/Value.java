package org.dolkif.annotations;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.CONSTRUCTOR,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
    String value();
}
