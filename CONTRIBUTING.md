Development Rules
-----------------

* Don't break existing behaviour. Backward compatibility is extremely important.

* New feature proposals should be described with acceptance tests and discussed with the [concordion-dev list](https://groups.google.com/forum/#!forum/concordion-dev) before implementation.

* Follow the style and conventions of the existing code (basically Sun's conventions). In particular:
  - Use 4 spaces (not tabs) 
  - Line width of 120 characters
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
