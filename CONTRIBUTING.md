How to contribute
================
We welcome contributions to Concordion.

Note that you will be asked to agree to the [contributor license agreement](https://gist.github.com/nigelcharman/c7cb396e2f48989be04d1e5afb89ca96) when raising a pull request.

This document lists the guidelines for contributing.

Proposing a change
-----------------------------
Firstly, you should create an issue for your enhancement request. You may also want to discuss the change with the [concordion-dev list](https://groups.google.com/forum/#!forum/concordion-dev) before implementation.

New feature proposals should be described with acceptance tests. For features not yet implemented, please add the `@Unimplemented` annotation.

Please note that, in order to keep Concordion clean and minimal, we consider all enhancement requests carefully. Should your enhancement not be appropriate for Concordion core, you may wish to package it as a Concordion extension.

Implementing a change
----------------------------------
Concordion uses a "Fork & Pull" model for collaborative development. If you have changes that you would like us to consider for introduction to Concordion, you will need to:
 1. [fork](https://help.github.com/articles/fork-a-repo) the repository, 
 1. commit and push your changes to your forked project, and 
 1. send us a [pull request](https://help.github.com/articles/using-pull-requests) referencing the URL of the issue that you created.
 1. if this is your first pull request, you will be asked to sign the [contributor license agreement](https://gist.github.com/nigelcharman/c7cb396e2f48989be04d1e5afb89ca96).

Development Rules
-----------------
* Don't break existing behaviour. Backward compatibility is extremely important.
* Follow the style and conventions of the existing code (basically Sun's conventions). In particular:
  - Use 4 spaces (not tabs) 
  - Always use braces after "if" statements.
* All code changes should have automated tests of some sort.
* Never check a failing test into the repository. (Though you can check-in unimplemented acceptance test HTML for new feature ideas). 

Building
--------
Concordion ships with the Gradle wrapper.

Useful tasks include:

`./gradlew clean test`
    - to compile all and run all tests (Concordion output is written to ./build/reports/spec)

`./gradlew clean distZip`
    - to create the distribution zip file

`./gradlew install`
    - to build the jar file and install to the local Maven repository. (The concordion-screenshot-extension-demo project incudes a `dev.gradle` file with instructions on including the local Maven repository when finding the dependencies).

`./gradlew tasks`
    - to list all tasks

Docs
----
 - See the [wiki](https://code.google.com/p/concordion/w/list) for docs on making a release.
 - There are some sequence diagrams in the `devdocs` folder.
