# WebRetriever

The worked example for chapter 14 of
[Quality Code: Software Testing Principles, Practices, and Patterns](http://www.informit.com/store/product.aspx?isbn=9780321832986 "Quality Code").

## The Specification

### Features

* Takes options followed by one or more URLs
* Retrieves content from URLs and writes content to the terminal unless options say otherwise.
* Supports http, defaulting to GET
* Assumes http if not specified
* Supports -O to write the file to the same local name
* Supports -d to supply POST data
* Supports -T to upload files via PUT with '-' support
* Supports -u with specification of user and password or just user with password prompt
* Supports -X?
* Supports long options?
