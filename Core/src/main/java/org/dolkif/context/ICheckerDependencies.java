package org.dolkif.context;

import lombok.NonNull;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ICheckerDependencies {

    Optional<Map<Parameter,Bean.BeanBase<?>>> getAvailableParamsCheckingExecutable(final @NonNull Executable executable, final @NonNull List<Bean.BeanBase<?>> classList);

    boolean checkAvailableExecutable(final @NonNull Executable executable);

    boolean paramsIsAvailable(final @NonNull Parameter parameter,final @NonNull Bean.BeanBase<?> beanBase) throws UnsupportedOperationException;

    List<Bean.BeanReference<?>> getParamsRequired(final @NonNull Executable executable);
    List<Bean.BeanReference<?>> getFieldsRequired(final @NonNull Class<?> classType);

}
