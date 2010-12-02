package kim.spock.httpmock

import org.apache.http.client.HttpResponseException;

import groovyx.net.http.ContentType;
import groovyx.net.http.HTTPBuilder;
import spock.lang.Specification;
import spock.lang.Timeout;


@WithHttpServer(port=5000)
class HttpServerExtensionSpec extends Specification {
	
	HTTPBuilder http = new HTTPBuilder('http://localhost:5000')
	HttpServer server
	
	@Timeout(2)
	def "Simple http request"() {
		setup:
		HttpServer server = Mock(HttpServer)
		HttpServerMock.mock = server
		
		when: String response = http.get(path: '/hello-spock.html')
		then: server.request("get", "/hello-spock.html") >> "Hello Spock!"
		and: response == "Hello Spock!"
	}
	
	@Timeout(2)
	def "404 response"() {
		setup:
		HttpServer server = Mock(HttpServer)
		HttpServerMock.mock = server
		
		when: "Execute a http request"
		http.get(path: '/hello-spock.html', contentType: ContentType.TEXT)
		
		then: "/hello-spock.html should return a http not found response" 
		1 * server.request("get", "/hello-spock.html") >> [ body: "Not found", status: HttpServerMock.HTTP_NOTFOUND ]
		
		and: "Http builder thrown an http 404 exception" 
		HttpResponseException ex = thrown(HttpResponseException)
		ex.statusCode == 404 
	}
	
	@Timeout(2)
	def "Unexpected request"() {
		setup:
		HttpServer server = Mock(HttpServer)
		HttpServerMock.mock = server
		
		when: "We request a resource not expected by the mock" 
		http.get(path: '/haba-haba.html')
		
		then: "Http builder throws an http 500 exception"
		HttpResponseException ex = thrown(HttpResponseException)
		ex.statusCode == 500
	}
	
}