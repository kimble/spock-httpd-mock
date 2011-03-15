package kim.spock.httpmock

import java.util.Properties;

import org.apache.http.client.HttpResponseException;

import static TestHttpServer.*
import groovyx.net.http.ContentType;
import groovyx.net.http.HTTPBuilder;
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

    @Timeout(2)
    def "Simple http request"() {
        when: "Execute a http request using http builder"
        String response = client.get(path: '/hello-spock.html')

        then: "The server recieves a request to the expected uri"
        1 * server.request("get", "/hello-spock.html", _, _) >> "Hello Spock!"

        and: "The response body is the same as given to the mock"
        response == "Hello Spock!"
    }

    @Timeout(2)
    def "404 response"() {

        when: "Execute a http request"
        client.get(path: '/hello-spock.html')

        then: "/hello-spock.html should return a http not found response"
        1 * server.request("get", "/hello-spock.html", _, _) >> [ body: "Not found", status: TestHttpServer.HTTP_NOTFOUND ]

        and: "Http builder thrown an http 404 exception"
        HttpResponseException ex = thrown(HttpResponseException)
        ex.statusCode == 404
    }

    @Timeout(2)
    def "Unexpected request"() {

        when: "We request a resource not expected by the mock"
        client.get(path: '/haba-haba.html')

        then: "Http builder throws an http 500 exception"
        HttpResponseException ex = thrown(HttpResponseException)
        ex.statusCode == 500
    }

    @Timeout(2)
    def "Asserting parameters"() {

        when: "We request a resource not expected by the mock"
        String responseBody = client.get(path: '/service/adder', query: [ a: '2', b: '1' ])

        then: "Set up expectation"
        1 * server.request("get", "/service/adder", { it.a == "2" && it.b == "1" }, _) >> "3"

        and: "Correct result"
        Integer.parseInt(responseBody) == 3
    }
    
    @Timeout(2)
    def "Closure DSL"() {

        when: "We request a resource not expected by the mock"
        String responseBody = client.get(path: '/service/adder', query: [ a: '2', b: '1' ])

        then: "Set up expectation"
        1 * server.request("/service/adder", a: "1", b: "2") {
            status HTTP_OK
        } 

        and: "Correct result"
        responseBody == "halla"
        
    }
    
}