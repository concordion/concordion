This is an experimental fork of the [Concordion project](https://github.com/concordion/concordion) with a parallel Concordion runner. This may or may not make it into Concordion core at some stage. For now, it's not recommended for production usage and is not supported.

The TODO.txt file contains some notes on actions needed to make it more production-worthy. Let me know if you want to be involved in moving this forward.


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