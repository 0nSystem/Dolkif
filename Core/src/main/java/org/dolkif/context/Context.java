package org.dolkif.context;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.dolkif.utils.ApplicationProperties;

@AllArgsConstructor
public class Context {

    private final @NonNull ApplicationProperties applicationProperties;


}
