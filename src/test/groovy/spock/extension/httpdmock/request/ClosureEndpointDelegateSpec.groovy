package spock.extension.httpdmock.request

import spock.lang.Specification
import spock.lang.Unroll

class ClosureEndpointDelegateSpec extends Specification {

    @Unroll("Java property #propertyName formatted as http header should be #httpConvention")
    def "Should be able to convert headers formatted as Java propertis to http convention"() {
        given:
        ClosureEndpointDelegate endpointDelegate = new ClosureEndpointDelegate()
        
        expect:
        endpointDelegate.propertyToHttpHeader(propertyName) == httpConvention
        
        where:
        propertyName        | httpConvention
        "expires"           | "Expires"
        "contentType"       | "Content-Type"
    }
    
}
