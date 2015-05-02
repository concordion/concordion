[![Build Status](https://travis-ci.org/concordion/concordion.svg?branch=parallel-run-strategy)](https://travis-ci.org/concordion/concordion)

**NOTE**: The parallel runner functionality has been moved into a new [ParallelRunExtension](https://github.com/concordion/concordion-parallel-run-extension). 

[Concordion](http://www.concordion.org) is an open source framework for Java that lets you turn a plain English description of a requirement into an automated test.

Concordion uses [Gradle](http://www.gradle.org/) as a build tool. The code base includes the Gradle Wrapper, which will automatically download the correct version of Gradle.

Build Tasks
=======
From the command line, run `gradlew tasks` to show available tasks. 

Note: If the current directory is not on your path, you will need to use `./gradlew tasks` on Unix-based systems, or `.\gradlew tasks` on Windows.

Compiling and Running the Tests
-------------------------------------------------
Run the following from the command line:

```gradlew clean test```
    
This will download the required dependencies, clean the existing project, recompile all source code and run all the tests. Concordion output is written to the `./build/reports/spec` folder.

Importing the project into your IDE
----------------------------------------
For Eclipse and NetBeans, you will need to install a Gradle plugin to your IDE before importing the project. See [Gradle tooling](https://www.gradle.org/tooling) for details.

On importing the project to your IDE, the required dependencies will be downloaded.

Project History
=========
History prior to April 2013 is in the [Subversion repository](http://concordion.googlecode.com/svn/tags/final-revision-before-github-migration/).
