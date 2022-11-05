package com.messageproccesor.annotations;

import com.messageproccesor.enums.PatternScope;

public @interface Scope {
    PatternScope pattern() default PatternScope.SINGLETON;
}
