# WebRetriever

The worked example for chapter 14 of
[Quality Code: Software Testing Principles, Practices, and Patterns](http://www.informit.com/store/product.aspx?isbn=9780321832986 "Quality Code").
This project is a test-driven Java application.

## Getting started

This project is set up as a [Maven](https://maven.apache.org/) project. To use it you have to have Maven installed or
use an IDE that has it bundled.

First, clone the repo

    git clone https://github.com/srvance/QualityCode14.git
    
### Using the command line

To run the tests, simply change to the project directory and run `mvn test`.

### Using an IDE

To use the project with an IDE such as [Eclipse](http://www.eclipse.org/) or [IntelliJ](https://www.jetbrains.com/idea/),
you need to import the project from existing sources as a Maven project.

The specific steps for IntelliJ are:

1. File->New…->Project from existing sources…
1. Choose the project root directory
1. Select “Import project from external model” and choose “Maven”
1. Other than switching to JDK 1.8 (I no longer have a 1.6 installed) I accept the defaults until finished
1. Open the “Maven Projects” panel to the right
1. Expand “webretriever”-> “Lifecycle”
1. Double click “test”
1. You should see the Run panel open with “BUILD SUCCESS” near the end

## The Specification

WebRetriever is a functional subset of the [curl(1)](http://curl.haxx.se/docs/manpage.html "curl manpage") command.
References to options in the below specification refer to the corresponding option in the curl documentation.

### Features

* Takes options followed by one or more URLs
* Retrieves content from URLs and writes content to the terminal unless options say otherwise.
* Multiple URLs' content to the same output will be separated by blank lines.
* Supports only http, defaulting to GET
* Assumes http if not specified
* Supports -O to write the file to the same local name
** Curl applies this option to the *next* URI. You need to put it before every URI if there are multiples you want handled this way
** Curl happily overwrites files if the file exists, including if there are multiple files with the same name in a single invocation
** Curl complains if there is no explicit file name as in the default index for a site
