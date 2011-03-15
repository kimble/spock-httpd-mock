package spock.extension.httpdmock

import java.util.Properties;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.http.client.HttpResponseException;

import groovyx.net.http.ContentType;
import groovyx.net.http.HTTPBuilder;
import spock.extension.httpdmock.HttpServer;
import spock.extension.httpdmock.response.HttpResponseStub;
import spock.lang.Specification;
import spock.lang.Timeout;

/**
 * 
 * @author Kim A. Betti
 */
class HttpServerExtensionSpec extends Specification {

    @HttpServerCfg(port=5000)
    HttpServer server = Mock()

    def client = new HTTPBuilder('http://localhost:5000')

    @Timeout(3)
    def "Simple http request"() {
        when: "Execute a http request using http builder"
        String response = client.get(path: '/hello-spock.html')

        then: "The server recieves a request to the expected uri"
        1 * server.request("/hello-spock.html", _) >> new HttpResponseStub(responseBody: "Hello Spock!")

        and: "The response body is the same as given to the mock"
        response == "Hello Spock!"
    }
    
    @Timeout(3)
    def "404 response"() {
        when: "Execute a http request"
        client.get(path: '/hello-spack.html')

        then: "/hello-spack.html should return a http not found response"
        1 * server.request("/hello-spack.html", _) >> new HttpResponseStub(statusCode: 404)

        and: "Http builder thrown an http 404 exception"
        HttpResponseException ex = thrown(HttpResponseException)
        ex.statusCode == 404
    }

    
    @Timeout(2)
    def "Unexpected request"() {
        when: "We request a resource not expected by the mock"
        client.get(path: '/haba-haba.html')

        then: "Http builder throws an http 404 exception"
        HttpResponseException ex = thrown(HttpResponseException)
        ex.statusCode == 404
    }

    @Timeout(2)
    def "Asserting parameters"() {

        when: "We request a resource not expected by the mock"
        String responseBody = client.get(path: '/service/adder', query: [ a: '2', b: '1' ])

        then: "Set up expectation"
        1 * server.request("/service/adder", { req -> 
            println "params.a: " + req.params.a + " / " + req.params.a.getClass() + " / " + req.params.a.class.isArray()
            req.params.a == "2" && req.params.b == "1" 
        }) >> new HttpResponseStub(responseBody: "3")

        and: "Correct result"
        responseBody != null
        Integer.parseInt(responseBody) == 3
    }

}