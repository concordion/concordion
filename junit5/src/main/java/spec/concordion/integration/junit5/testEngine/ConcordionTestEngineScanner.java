package spec.concordion.integration.junit5.testEngine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.platform.commons.util.ReflectionUtils;
import spec.concordion.integration.junit5.ConcordionJUnit5;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

/**
 * Created by tim on 12/07/17.
 */
public class ConcordionTestEngineScanner {
    public Collection<Class<?>> scan(String packageName) {
        Predicate<Class<?>> allClasses = clas -> {
            if (!hasConcordionAnnotation(clas)) {
                return false;
            }

            if (hasMethodWithAnnotation(clas, Test.class)) {
                return false;
            }

            if (hasMethodWithAnnotation(clas, TestFactory.class)) {
                return false;
            }

            return true;
        };
        Predicate<String> allNames = clasName -> true;
        return ReflectionUtils.findAllClassesInPackage(packageName, allClasses, allNames);
    }

    private static boolean hasMethodWithAnnotation(Class<?> potentialMatch, Class<? extends Annotation> annotation) {
        return checkMethodListForAnnotation(potentialMatch, potentialMatch.getDeclaredMethods(), annotation) ||
                checkMethodListForAnnotation(potentialMatch, potentialMatch.getMethods(), annotation);
    }

    private static boolean checkMethodListForAnnotation(Class<?> potentialMatch, Method[] methods, Class<? extends Annotation> annotation) {
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotation)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasConcordionAnnotation(Class<?> clas) {
        return clas.getAnnotation(ConcordionJUnit5.class) != null;
    }
}
