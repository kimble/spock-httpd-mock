Spock httpd mock
================

My first attempt at writing a [Spock](http://www.spockframework.org/) extension. Jetty is used to set up a http server for unit testing. The first version of this plugin used NanoHTTPD as I thought that Jetty would be to slow for unit testing. It turns out that Jetty starts and stops a lot faster that I had thought! 

Example
-------

    class ExampleSpec extends Specification {

        @HttpServerCfg(port=5000)
        HttpServer server = Mock()

        def "example for readme"() {
            given: "http builder client instead of acutal code under test"
            HTTPBuilder http = new HTTPBuilder("http://localhost:5000")

            when: "we execute a simple http get request with one parameter"
            String response = http.get(path: '/helloService', query: [ name: "Spock" ])

            then: "/helloService is requested on the server with name = spock"
            1 * server.request("/helloService", { it.params.name == "Spock" }) >> new HttpResponseStub(responseBody: "Hello Spock!")

            and: "we got the return value we specified on the mock"
            response == "Hello Spock!"
        }
        
    }

    
The good, the bad and the ugly
------------------------------

### The good

1. It leverages the Spock's awesome interaction API. So you can write very expressive expectations. 

2. Very little setup and no tear down code necessary in the tests. 

### The bad

1. It's not even a little bit thread-safe. Kittens and unicorns **will** die if you use it in a multi-threaded fashion.

2. The syntax for defining constraints on parameters and headers are slightly messy.

3. Error messages could be a lot more helpful. 

### The ugly

Got rid of the most ugly part after advice from Peter Niederwieser. 


Roadmap / things to do 
-----------------------

1. Improve error messages. I'm sure it's possible to make them a lot more helpful. 
 
2. Add support for the typical rest convention, with json and xml builders for resposes. 

3. Support file uploading. 
 
Try it out
----------

Check out the sources and run `gradle test` (you obviously need Gradle). 

Feedback
---------------

This is my first attempt at writing a Spock extension. If you know the answer to some of the questions above, or know of better ways of doing things then please let me know! 
