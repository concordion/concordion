Development Rules
-----------------

* Don't break existing behaviour. Backward compatibility is 
  extremely important.

* New feature proposals should be described with acceptance tests 
  and ideally discussed with the group before implementation.

* Follow the style and conventions of the existing code (basically
  Sun's conventions). In particular:
  - Use 4 spaces (not tabs) 
  - Always use braces after "if" statements.

* All code changes should have automated tests of some sort.

* Never check a failing test into the repository. (Though you can
  check-in unimplemented acceptance test HTML for new feature ideas). 



Building
--------
This requires Gradle 1.5 or later.

Useful tasks include:

gradle clean test
    - to compile all and run all tests (Concordion output is written to ./build/reports/spec)

gradle clean distZip
    - to create the distribution zip file

gradle cleanEclipse eclipse
    - to build an Eclipse project for concordion