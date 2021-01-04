[![Build Status](https://img.shields.io/travis/com/concordion/concordion.svg)](https://travis-ci.com/concordion/concordion)
[![Maven Central](https://img.shields.io/maven-central/v/org.concordion/concordion.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.concordion%22%20AND%20a%3A%22concordion%22)
[![Apache License 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

[Concordion](http://www.concordion.org) is an open source framework for Java that lets you turn a plain English description of a requirement into an automated test.

Users should see the [Concordion](http://www.concordion.org) web site for details of how to download and use Concordion.

This README covers information for people wanting to work with the Concordion source code.

# Target Java version
Concordion currently targets Java 6.

# Building Concordion
Concordion uses [Gradle](http://www.gradle.org/) as a build tool. The code base includes the Gradle Wrapper, which will automatically download the correct version of Gradle.

From the command line, run `gradlew tasks` to show available tasks. 

Note: If the current directory is not on your path, you will need to use `./gradlew tasks` on Unix-based systems, or `.\gradlew tasks` on Windows.

## Compiling and Running the Tests

Run the following from the command line:

```gradlew clean test```
    
This will download the required dependencies, clean the existing project, recompile all source code and run all the tests. Concordion output is written to the `./build/reports/spec` folder.

## Creating a jar file

Run the following from the command line:

```gradlew clean jar```

The jar file is written to the `./build/libs` folder.

## Installing a jar file into your local Maven repository

Installing a Concordion jar file into your local Maven repository makes it available to other projects that are using Maven or Gradle to manage their dependencies.

The publishing tasks for Concordion have been split out into a separate `publish.gradle` build script, since these rely on Java 7 and the core build is based on Java 6.

Run the following from the command line:

```gradlew -b publish.gradle pTML```

(where pTML is short for publishToMavenLocal).

### Using the Concordion jar file from your local Maven repository in a Gradle project

In order to use the local Maven repository in a Gradle project, you must add `mavenLocal()` to your `repositories` block. You can add this to the project's build.gradle script, or set it globally by adding the following to your ~/.gradle/init.gradle script:

```
allprojects {
    repositories {
        mavenLocal()
    }
}
```

You will then need to ensure that your project's build.gradle script refers to the version you have in your local Maven repository, for example your dependencies might include:

    org.concordion:concordion:2.0.0-SNAPSHOT

or

    org.concordion:concordion:+

for the latest version.

# Importing the project into your IDE
----------------------------------------
Dependent on the version of your IDE, you may need to install a Gradle plugin to your IDE before importing the project. See [Gradle tooling](https://www.gradle.org/tooling) for details.

On importing the project to your IDE, the required dependencies will be downloaded.

Project History
=========
History prior to April 2013 is in Google code archive [code](https://code.google.com/archive/p/concordion/source/default/source) and [history](https://code.google.com/archive/p/concordion/source/default/commits).


