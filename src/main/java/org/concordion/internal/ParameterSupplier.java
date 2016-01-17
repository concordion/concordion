package org.concordion.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

interface ParameterSupplier {
    Object getValueForParameter(Method method, Parameter parameter);
}