package org.concordion.internal.runner;

import org.concordion.internal.runner.rules.FaultyOrderTest;
import org.concordion.internal.runner.rules.WorkingOrderTest;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by tim on 23/06/15.
 */
public class DefaultConcordionRunnerTest {

    @Test
    public void testGetMethodsWithAnnotation() throws Exception {
        List<Method> methods = DefaultConcordionRunner.getMethodsWithAnnotation(AnnotatedClass.class, TestAnnotation.class);

        assertThat(methods.size(), is(equalTo(8)));

        System.err.println("Found Methods");
        for (Method method: methods) {
            System.err.println("   " + method.getName());
        }
    }

    @Test
    public void testGetFieldsWithAnnotation() throws Exception {
        List<Field> fields = DefaultConcordionRunner.getFieldsWithAnnotation(AnnotatedClass.class, TestAnnotation.class);
        assertThat(fields.size(), is(equalTo(8)));

        System.err.println("Found Fields:");
        for (Field field: fields) {
            System.err.println("   " + field.getName());
        }
    }

    @Test
    public void testWorkingOrderTest() throws Exception {
        List<Field> fields = DefaultConcordionRunner.getFieldsWithAnnotation(WorkingOrderTest.class, Rule.class);
        assertThat(fields.size(), is(equalTo(3)));

        System.err.println("Found Fields:");
        for (Field field: fields) {
            System.err.println("   " + field.getName());
        }

    }

    @Test
    public void testNotWorkingOrderTest() throws Exception {
        List<Field> fields = DefaultConcordionRunner.getFieldsWithAnnotation(FaultyOrderTest.class, Rule.class);
        assertThat(fields.size(), is(equalTo(4)));

        System.err.println("Found Fields:");
        for (Field field: fields) {
            System.err.println("   " + field.getName());
        }

    }
}