package org.concordion.api;

import java.io.File;
import java.util.List;

/**
 * An instance of a fixture class.
 * 
 * @since 2.0.0
 */
public interface Fixture extends FixtureDeclarations {

    /**
     * @return the fixture instance.
     */
    Object getFixtureObject();

    /**
     * @return the class of the fixture instance.
     */
    Class<?> getFixtureClass(); 

    /**
     * @return a list of the absolute paths on the class path. 
     */
    List<File> getClassPathRoots();
    
    /**
     * @return the fixture class and all of its superclasses, excluding java.lang.Object,
     * ordered from the most super class to the fixture class. 
     */
    List<Class<?>> getClassHierarchyParentFirst();
    
    /**
     * @return a description of the specification containing the fixture name.
     */
    String getSpecificationDescription();

    /**
     * @return the absolute path to the fixture, omitting the suffix.
     */
    String getFixturePathWithoutSuffix();

    /**
     * Setup the fixture for the next run, hence the need to pass in a fixture object.
     * @param fixtureObject the fixture about to be run
     */
    void setupForRun(Object fixtureObject);

    /**
     * Call methods annotated with <code>@BeforeSuite</code>.
     */
    void beforeSuite();

    /**
     * Call methods annotated with <code>@AfterSuite</code>.
     */
    void afterSuite();

    /**
     * Save the specification scoped fields and call methods annotated with <code>@BeforeSpecification</code>.  
     */
    void beforeSpecification();

    /**
     * Call methods annotated with <code>@AfterSpecification</code> and destroy specification scoped fields.   
     */
    void afterSpecification();

    /**
     * Not currently used.
     * @param exampleName the name of the example being invoked
     */
    void beforeProcessExample(String exampleName);

    /**
     * Call methods annotated with <code>@BeforeExample</code>.
     * @param exampleName the name of the example being invoked  
     */
    void beforeExample(String exampleName);
    
    /**
     * Call methods annotated with <code>@AfterExample</code>.   
     * @param exampleName the name of the example being invoked  
     */
    void afterExample(String exampleName);
    
    /**
     * Destroy example scoped fields.
     * @param exampleName the name of the example being invoked
     */
    void afterProcessExample(String exampleName);
}