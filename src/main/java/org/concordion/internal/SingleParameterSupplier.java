package org.concordion.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Method;

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
    public Object getValueForParameter(Method method, Class<?> parameterClass, Annotation[] annotations) {
        if (annotations.length != 1) {
            throw new AnnotationFormatError("Error invoking " + method.getName() + ". Methods annotated with '" + methodAnnotationClass.getName() + "' can only have parameters annotated with '" + parameterAnnotation.getName() + "'.");
        }
        Annotation annotation = annotations[0];
        if (parameterAnnotation.equals(annotation.annotationType())) {
            if (!(parameterClass.equals(parameterValue.getClass()))) {
                throw new AnnotationFormatError("Error invoking '" + method + "'. Parameter with annotation '" + parameterAnnotation + "' is expected to be of type '" + parameterValue.getClass().getName() + "'");
            }
            return parameterValue; 
        }
        throw new AnnotationFormatError("Error invoking " + method.getName() + ". Methods annotated with '" + methodAnnotationClass.getName() + "' can only have parameters annotated with '" + parameterAnnotation.getName() + "'.");
    }
}