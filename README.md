Parallel Runner
===============

This is an experimental fork of the [Concordion project](https://github.com/concordion/concordion) with a parallel Concordion runner. This may or may not make it into Concordion core, or it may be implemented as a Concordion extension. 

Overview
--------
There are 2 ways to run Concordion in parallel:

1. Run all of the JUnit tests in parallel, for example using JUnit's ParallelComputer, features in your build tool (eg. Ant, Maven, Gradle), or by running multiple suites in your CI server. Should you wish to create Concordion index pages to collate the results, you will need to create these yourself (there is no support in Concordion currently to collate these into a summary page - though contributions are welcome!)
2. Use this runner, which will submit a task to a configurable thread pool to run each specification launched using [concordion:run](http://concordion.org/Tutorial.html#concordion:run) and update the index pages with annotated (green/red/grey) results once the tasks have completed.

Usage
-----
You will need to have your test suite structured to use the [concordion:run](http://concordion.org/Tutorial.html#concordion:run) command. This runner will submit a task to the thread pool to run each specification launched using concordion:run.

Set the system property `concordion.run.threadCount` to the maximum number of threads you want to run concurrently. If this property is not set, the specifications will be run sequentially. Suffixing this property value with `C` will multiply the value by the number of processors available to the JVM. For example, the value `2.5C` will set the thread count to 10 when run on a 4-core machine.

### Dependencies
This runner introduces 2 new dependencies that will need to be on your classpath:

 * `com.google.guava:guava:17.0`
 * `org.slf4j:slf4j-api:1.7.7`

Additionally, to see the logging output, you will need a [runtime binding](http://www.slf4j.org/manual.html#swapping) to a slf4j implementation.

### Installation
This has been published to the Sonatype snapshot repository (https://oss.sonatype.org/content/repositories/snapshots/), as version `1.5.0-SNAPSHOT`. To use this from Maven/Gradle/Ivy, you will need to add this snapshot repository to your list of repositories. An example Gradle build script is:

    repositories {
        mavenCentral()
        maven {
            url 'https://oss.sonatype.org/content/repositories/snapshots/'
        }
    }

    dependencies {
        testCompile 'org.concordion:concordion:1.5.0-SNAPSHOT'
        runtime 'ch.qos.logback:logback-classic:1.1.2'
    }

    test {
        include '**/TestSuite.*'
        systemProperties['concordion.output.dir'] = "$reporting.baseDir/spec"
        systemProperties['concordion.run.threadCount'] = "2.5C"                    // 2.5 * number of cores
    }

    test.dependsOn cleanTest

Alternatively you can build from source.

Notes
-----
* This runner runs the tests within the same JVM process. To run your tests safely in parallel, your code must be thread-safe. In particular be wary of any shared state (including tests using the same data in a database) or shared resources (eg. static references to browser instances).
* Do not rely on the same threads being used across multiple tests. This runner needs to expand and shrink the thread pool dynamically so that specifications can wait for all the specifications they have launched (using concordion:run) to be complete. Tests will be allocated to the dynamically created threads.
* This runner will run your tests in a random order. Your tests must be able to run in any order.
* The "results generated" timings shown on the specifications will show the duration from which the task was parsed until it completed execution. This will include time that any specifications that it launches spent queued.
* When this runner is launched, a message will be printed to standard output to let you know how many threads have been configured in the thread pool. If you do not see this message, then your runner is not configured properly and will fall back to the standard sequential runner.

API changes
-----------
There are a number of breaking API changes in this version:

 * The `org.concordion.api.ResultRecorder` interface has one additional method - `setSpecificationDescription(String description)` to set the description of the specification for which the results are being recorded. (_In order to support this, `org.concordion.internal.command.SpecificationCommand` contains a new method `setSpecificationDescriber()` which in turn sets the specification description on the ResultRecorder_).
 * The unused class `org.concordion.api.Context` is removed.




Original Concordion README
===================

[Concordion](http://www.concordion.org) is an open source framework for Java that lets you turn a plain English description of a requirement into an automated test.

Concordion uses [Gradle](http://www.gradle.org/) as a build tool. To build the source code, run the tests or create an Eclipse project, you will need to [download](http://www.gradle.org/downloads.html) and [install](http://www.gradle.org/installation.html) Gradle. It has been tested with Gradle 1.5.

Build Tasks
=======
From the command line, run `gradle tasks` to show available tasks.

Compiling and Running the Tests
-------------------------------------------------
After installing Gradle, run the following from the command line:

> gradle clean test
    
This will download the required dependencies, clean the existing project, recompile all source code and run all the tests. Concordion output is written to the `./build/reports/spec` folder.

Creating an Eclipse project
----------------------------------------
After installing Gradle, run the following from the command line:

> gradle cleanEclipse eclipse

This will download the required dependencies and create an Eclipse project. From Eclipse, the project can be imported from the `File` > `Import...` menu by selecting the import source `General` > `Existing Projects into Workspace`.

Project History
=========
History prior to April 2013 is in the [Subversion repository](http://concordion.googlecode.com/svn/tags/final-revision-before-github-migration/).
