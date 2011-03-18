package spock.extension.httpdmock

import groovy.util.slurpersupport.NodeChild
import groovyx.net.http.HTTPBuilder
import spock.extension.httpdmock.response.HttpResponseStub
import spock.extension.httpdmock.response.XmlHttpResponseStub
import spock.extension.httpdmock.service.simple.SimpleHttpRequestService
import spock.extension.httpdmock.service.simple.SimpleHttpService
import spock.lang.Specification

/**
 * 
 * @author Kim A. Betti
 */
class ExampleSpec extends Specification {

    @HttpServerCfg
    HttpTestServer server
    
    @HttpServiceMock(SimpleHttpRequestService)
    SimpleHttpService simpleHttpService = Mock()

    def "example for readme"() {
        given: "http builder client instead of acutal code under test"
        HTTPBuilder http = new HTTPBuilder(server.baseUri)

        when: "we execute a simple http get request with one parameter"
        String response = http.get(path: "/helloService", query: [ name: "Spock" ])

        then: "/helloService is requested on the server with name = spock"
        1 * simpleHttpService.request("/helloService", { it.params.name == "Spock" }) >> new HttpResponseStub(responseBody: "Hello Spock!")

        and: "we got the return value we specified on the mock"
        response == "Hello Spock!"
    }
    
    def "xml response"() {
        given: 
        HTTPBuilder http = new HTTPBuilder(server.baseUri)
        
        when: "we execute a http get request an xml rest service"
        NodeChild response = http.get(path: "/fraktguide/postalCode.xml", query: [ pnr: "7600" ])

        then: "we use Spocks interaction API and XmlBuilder to generate an xml response"
        1 * simpleHttpService.request("/fraktguide/postalCode.xml", { it.params.pnr == "7600" }) >> XmlHttpResponseStub.build {
            PostalCodeQueryResponse {
                Response(valid: true, "Levanger")
            }
        }

        and: "we can use Groovys slurper support to extract the data"
        response.Response.text() == "Levanger"
    }
    
}