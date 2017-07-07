package org.concordion.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

interface ParameterSupplier {
    Object getValueForParameter(Method method, Class<?> parameterClass, Annotation[] parameterAnnotations);
}