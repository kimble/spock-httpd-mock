Spock httpd mock
================

My first attempt at writing a [Spock](http://www.spockframework.org/) extension. It uses [NanoHTTPD](http://elonen.iki.fi/code/nanohttpd/), to set up a http server for unit testing. NanoHTTPD is a tiny, one file Java server perfect for this purpose. 

First attempt
------------

    @WithHttpServer(port=5000)
    def "simple http request"() {

        given: "http server mock"
            HttpServer server = Mock(HttpServer)
            TestHttpServer.mock = server // hack!

        and: "http builder client"
            HTTPBuilder http = new HTTPBuilder('http://localhost:5000')
            
        when: "we execute a simple http get request"
            String response = http.get(path: '/hello-spock.html')

        then: "the server gets a get request for the expected uri"
            1 * server.request("get", "/hello-spock.html") >> "Hello Spock!"

        and: "we got the expected return value"
            response == "Hello Spock!"

    }

    
The good, the bad and the ugly
------------------------------

### The good

It leverages the Spock's awesome interaction API. So you can write things like 
`2 * server.request("get", "/index.html") >> "It works!` 
if you expect two requests to be made for index.html

### The bad

It's not even a little bit thread-safe. Kittens and unicorns **will** die if you use it in a multi-thread fashion. 

### The ugly

You must remember to pass the mock into the test http server. This allows the test server to translate http requests into method calls on the HttpServer mock. This is what allows the use of Spock's interaction API.


Roadmap / things to do 
-----------------------
 
1. Implement a more fluent API allowing assertions to be made against parameters and headers. The current implementation in `TestHttpServer` is a very "hackish" / proof of concept implementation. 

2. Let it select a random available port. I'm not sure how one would pass the selected port into the feature though. 

Feedback
---------------

This is my first attempt at writing a Spock extension. If you know the answer to some of the questions above, or know of better ways of doing things then please let me know! 
 
Try it out
----------

Check out the sources and run `gradle test` (you obviously need Gradle). 