package spock.extension.httpdmock.route

import spock.lang.Specification;
import spock.lang.Unroll;

/**
 * 
 * @author Kim A. Betti
 */
class RouteSpec extends Specification {

    @Unroll
    def "#pattern should match #path? #shouldMatch"() {
        expect:
        Route rp = new Route(pattern)
        rp.matches(url) == shouldMatch
        
        where:
        pattern                 | url                     | shouldMatch
        "/api/user/@username"   | "/api/user/kim"         | true       
        "/api/@api/@username"   | "/api/user/kim"         | true       
        "/api/user/@username"   | "/api/user"             | false      
    }
    
    @Unroll
    def "Should be able to extract parameter values #params from #path using #routePattern"() {
        expect:
        Route rp = new Route(routePattern)
        rp.params(url) == params
        
        where:
        routePattern                | url               | params
        "/api/user/@username"       | "/api/user/kim"   | [ username: "kim" ]
        "/api/@apiname/@username"   | "/api/user/kim"   | [ apiname: "user", username: "kim" ]
        "/api/user/@username"       | "/api/user"       | [ : ]
    }
    
    @Unroll
    def "Should extract parameter names #paramNames from #routePattern"() {
        expect:
        Route route = new Route(routePattern)
        route.paramNames == paramNames
        
        where:
        routePattern               | paramNames
        "/api/user/kim"            | [ ]
        "/api/user/@username"      | [ "username" ]
        "/api/@apiname/@param"     | [ "apiname", "param" ]
    }
    
    
}
