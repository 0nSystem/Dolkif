package org.dolkif.annotations;

import org.dolkif.ScopePattern;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
    ScopePattern scope();
    String nameBean();
}
