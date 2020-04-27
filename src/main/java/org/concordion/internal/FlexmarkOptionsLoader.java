package org.concordion.internal;

import com.vladsch.flexmark.util.data.DataSet;
import org.concordion.api.Fixture;
import org.concordion.api.option.FlexmarkOptions;
import org.concordion.internal.parser.ParserInitialisationException;

import java.lang.reflect.Field;
import java.util.List;

public class FlexmarkOptionsLoader {
    public DataSet getFlexmarkOptionsForFixture(Fixture fixture) {
        DataSet flexmarkOptions = null;

        List<Class<?>> classes = fixture.getFixtureType().getClassHierarchyParentFirst();
        for (Class<?> class1 : classes) {
            Field[] declaredFields = class1.getDeclaredFields();
            for (Field field : declaredFields) {
                if (field.isAnnotationPresent(FlexmarkOptions.class)) {
                    if (flexmarkOptions != null) {
                        throw new ParserInitialisationException("Duplicate @FlexmarkOptions annotation found for field '" + field.getName() + "'. There must only be one field annotated with @FlexmarkOptions.");
                    }
                    field.setAccessible(true);
                    flexmarkOptions = getFlexmarkOptions(fixture, field);
                    validateNonNull(field, flexmarkOptions);
                }
            }
        }

        return flexmarkOptions;
    }

    private DataSet getFlexmarkOptions(Fixture fixture, Field field) {
        try {
            return (DataSet) field.get(fixture.getFixtureObject());
        } catch (ClassCastException e) {
            throw new ParserInitialisationException("Field '" + field.getName() + "' must implement com.vladsch.flexmark.util.data.DataSet");
        } catch (IllegalArgumentException e) {
            throw new ParserInitialisationException("Defect - this exception should not occur. Please report to Concordion issues list.", e);
        } catch (IllegalAccessException e) {
            throw new ParserInitialisationException("Defect - this exception should not occur. Please report to Concordion issues list.", e);
        }
    }

    private void validateNonNull(Field field, DataSet options) {
        if (options == null) {
            throw new ParserInitialisationException("Field '" + field.getName() + "' must be non-null");
        }
    }
}
