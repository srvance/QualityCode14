# WebRetriever

The worked example for chapter 14 of
[Quality Code: Software Testing Principles, Practices, and Patterns](http://www.informit.com/store/product.aspx?isbn=9780321832986 "Quality Code").
This project is a test-driven Java application.

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
* Supports -d to supply POST data
* Supports -T to upload files via PUT with '-' support
* Supports -u with specification of user and password or just user with password prompt
* Supports -X?
* Supports long options?
