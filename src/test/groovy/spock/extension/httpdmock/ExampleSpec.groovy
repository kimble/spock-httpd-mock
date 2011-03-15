package spock.extension.httpdmock

import groovyx.net.http.HTTPBuilder;
import spock.extension.httpdmock.HttpServer;
import spock.extension.httpdmock.response.HttpResponseStub;
import spock.lang.Specification;

/**
 * 
 * @author Kim A. Betti
 */
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