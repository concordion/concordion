Parallel Runner
===============

This is an experimental fork of the [Concordion project](https://github.com/concordion/concordion) with a parallel Concordion runner. This may or may not make it into Concordion core, or it may be implemented as a Concordion extension. For now, it's not recommended for production usage.
The TODO.txt file contains some notes on actions needed to make it more production-worthy. Please post a comment if you want to be involved in moving this forward.

Overview
--------
There are 2 main ways to run Concordion in parallel:

1. Run all of the JUnit tests in parallel, for example using JUnit's ParallelComputer, features in your build tool (eg. Ant, Maven, Gradle), or by running multiple suites in your CI server. Should you wish to create Concordion index pages to collate the results, you will need to create these yourself (there is no support in Concordion currently to collate these into a summary page - though contributions are welcome!)
2. If using the concordion:run command to create a test suite of your Concordion specifications, this runner will dynamically allocate specifications to a thread pool and update the index pages with annotated (green/red/grey) results once the child specifications are complete.

Usage
-----
You will need to have your test suite structured to use the [concordion:run](http://concordion.org/Tutorial.html#concordion:run) command. This runner will submit a task to the thread pool to run each specification launched using concordion:run.

Set the system property `concordion.run.threadCount` to the maximum number of threads you want to run concurrently. If this property is not set, the specifications will be run sequentially. Suffixing this property value with `C` will multiply the value by the number of processors available to the JVM. For example, the value `2.5C` will set the thread count to 10 when run on a 4-core machine.

h3. Dependencies
This runner introduces 2 new dependencies that will need to be on your classpath:

  `com.google.guava:guava:17.0`
  `org.slf4j:slf4j-api:1.7.7`

Additionally, to see the logging output, you will need a [runtime binding](http://www.slf4j.org/manual.html#swapping) to a slf4j implementation.


Notes
-----
* This runner runs the tests within the same JVM process. To run your tests safely in parallel, your code must be thread-safe. In particular be wary of any shared state (including tests using the same data in a database) or shared resources (eg. static references to browser instances).
* Do not rely on the same threads being used across multiple tests. This runner needs to expand and shrink the thread pool dynamically so that specifications can wait for all the specifications they have launched (using concordion:run) to be complete. Tests will be allocated to the dynamically created threads. The runner does limit the number of running tests to the initial size of the thread pool.
* This runner will run your tests in a random order. Your tests must be able to run in any order.
* The "results generated" timings shown on the specifications will show the duration from which the task was parsed until it completed execution. This will include time that any specifications that it launches spent queued.


#Original Concordion README
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
History prior to April 2013 is in the [Subversion repository](http://concordion.googlecode.com/svn/).
