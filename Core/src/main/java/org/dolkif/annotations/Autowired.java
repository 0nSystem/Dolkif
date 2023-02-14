package org.dolkif.annotations;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD,ElementType.CONSTRUCTOR,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
}
