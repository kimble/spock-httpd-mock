package kim.spock.httpmock

import groovyx.net.http.HTTPBuilder;
import spock.lang.Specification;

class ExampleSpec extends Specification {

	@WithHttpServer(port=5000)
    def "example for readme"() {

        given: "http server mock"
            HttpServer server = Mock(HttpServer)
            TestHttpServer.mock = server // 1

        and: "http builder client"
            HTTPBuilder http = new HTTPBuilder('http://localhost:5000')
            
        when: "we execute a simple http get request"
            String response = http.get(path: '/hello-spock.html')

        then: "the server gets a get request for the expected uri"
            1 * server.request("get", "/hello-spock.html") >> "Hello Spock!"

        and: "we got the expected return value"
            response == "Hello Spock!"

    }
	
}