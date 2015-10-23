[![Build Status](https://img.shields.io/travis/concordion/concordion.svg)](https://travis-ci.org/concordion/concordion)
[![Maven Central](https://img.shields.io/maven-central/v/org.concordion/concordion.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.concordion%22%20AND%20a%3A%22concordion%22)
[![Latest Beta Release](https://img.shields.io/badge/latest-v2.0.0--SNAPSHOT-orange.svg)](https://github.com/concordion/concordion/releases/tag/2.0.0-SNAPSHOT)
[![Apache License 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

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
