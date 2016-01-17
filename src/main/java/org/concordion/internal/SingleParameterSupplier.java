package org.concordion.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

class SingleParameterSupplier implements ParameterSupplier {
    private Class<? extends Annotation> methodAnnotationClass;
    private Class<? extends Annotation> parameterAnnotation;
    private Object parameterValue;

    public SingleParameterSupplier(Class<? extends Annotation> methodAnnotationClass, Class<? extends Annotation> parameterAnnotationClass, Object parameterValue) {
        this.methodAnnotationClass = methodAnnotationClass;
        this.parameterAnnotation = parameterAnnotationClass;
        this.parameterValue = parameterValue;
    }

    @Override
    public Object getValueForParameter(Method method, Parameter parameter) {
        Annotation[] annotations = parameter.getDeclaredAnnotations();
        if (annotations.length != 1) {
            throw new AnnotationFormatError("Error invoking " + parameter.getDeclaringExecutable() + ". Methods annotated with '" + methodAnnotationClass.getName() + "' can only have parameters annotated with '" + parameterAnnotation.getName() + "'.");
        }
        Annotation annotation = annotations[0];
        if (parameterAnnotation.equals(annotation.annotationType())) {
            if (!(parameter.getParameterizedType().equals(parameterValue.getClass()))) {
                throw new AnnotationFormatError("Error invoking '" + method + "'. Parameter '" + parameter.getName() + "' is expected to be of type '" + parameterValue.getClass().getName() + "'");
            }
            return parameterValue; 
        }
        throw new AnnotationFormatError("Error invoking " + parameter.getDeclaringExecutable() + ". Methods annotated with '" + methodAnnotationClass.getName() + "' can only have parameters annotated with '" + parameterAnnotation.getName() + "'.");
    }
}