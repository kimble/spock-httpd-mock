package spock.extension.httpdmock.examples.features

import groovyx.net.http.HTTPBuilder
import spock.extension.httpdmock.EndpointRoute
import spock.extension.httpdmock.HttpServerCfg
import spock.extension.httpdmock.HttpTestServer
import spock.extension.httpdmock.RequestToContract
import spock.lang.Specification

class ResponseHeadersSpec extends Specification {
    
    @HttpServerCfg
    HttpTestServer server
    
    @RequestToContract(FeatureDemoRequestToContract)
    FeatureDemo featureDemo = Mock()

    def "Cache related headers should be set"() {
        given:
        HTTPBuilder http = new HTTPBuilder(server.baseUri)
        
        when:
        String expiresAt = http.get(path: "/content/cacheable") { resp -> resp.headers."Expires" }
        
        then:
        expiresAt == "Thu, 24 Dec 2030 16:00:00 GMT"
    }
}

class FeatureDemoRequestToContract {
    
    FeatureDemo contract

    @EndpointRoute("/content/cacheable")
    def findUserFollowers = {
        setHeaders {
            expires = "Thu, 24 Dec 2030 16:00:00 GMT"
        }
        
        plainResponse "this is cacheable"
    }
}

interface FeatureDemo {

    void eternallyCacheable();
    
}