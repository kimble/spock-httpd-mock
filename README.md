Spock httpd mock
================

My first attempt at writing a [Spock](http://www.spockframework.org/) extension. Jetty is used to set up a http server for unit testing. The first version of this plugin used NanoHTTPD as I thought that Jetty would be to slow for unit testing. It turns out that Jetty starts and stops a lot faster that I had thought!

Important: This is by no way stable yet. The code is still buggy as hell and important features are missing. At this point it's all about playing with Spock and trying to come up with the best possible API for testing HTTP / REST services.

Example
-------

See `src/test/groovy/spock/extension/httpdmock/examples` for examples. 

Roadmap / things to do 
-----------------------

1. Improve error messages. I'm sure it's possible to make them a lot more helpful. 
 
2. Add support for the typical rest convention

3. Support file uploading. 

4. Write a Grails plugin
 
Try it out
----------

Check out the sources and run `gradle test` (you obviously need Gradle). 

Feedback
---------------

This is my first attempt at writing a Spock extension. If you know the answer to some of the questions above, or know of better ways of doing things then please let me know! 
