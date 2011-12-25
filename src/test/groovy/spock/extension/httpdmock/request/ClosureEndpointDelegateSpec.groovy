package spock.extension.httpdmock.request

import spock.lang.Specification
import spock.lang.Unroll

class ClosureEndpointDelegateSpec extends Specification {

    @Unroll
    def "Java property #propertyName formatted as http header should be #httpConvention"() {
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
