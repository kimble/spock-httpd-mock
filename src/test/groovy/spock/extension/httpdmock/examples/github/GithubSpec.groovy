package spock.extension.httpdmock.examples.github


import groovy.util.slurpersupport.NodeChild
import groovyx.net.http.HTTPBuilder
import spock.extension.httpdmock.EndpointRoute
import spock.extension.httpdmock.HttpServerCfg
import spock.extension.httpdmock.HttpServiceEndpoint
import spock.extension.httpdmock.HttpTestServer
import spock.extension.httpdmock.response.XmlResponseWriter
import spock.lang.Specification

/**
 * Testing the service handler mechanism by implementing a service mock
 * for a simple rest service provided by the Norwegian postal service. 
 * @author Kim A. Betti
 */
class GithubSpec extends Specification {
  
    @HttpServerCfg
    HttpTestServer server
    
    @HttpServiceEndpoint(GitHubFollowersRequestToContract)
    GitHubFollowers githubFollowerService = Mock()

    def "Should be able to look up a users folloers"() {
        given: 
        HTTPBuilder http = new HTTPBuilder(server.baseUri)
        
        when:
        NodeChild xmlResponse = http.get(path: "/api/v2/xml/user/show/superman/followers")

        then:
        1 * githubFollowerService.followers("superman") >> [ "lex.luthor", "lana.lang" ]
        
        and:
        xmlResponse.user*.text() == [ "lex.luthor", "lana.lang" ]
    }
    
}

/**
 * Encapsulation of the Norwegian Postal Service REST service for looking up zip codes.
 * @see http://fraktguide.bring.no/fraktguide/postalCode.xml?pnr=7600
 * @author Kim A. Betti
 */
class GitHubFollowersRequestToContract {

    GitHubFollowers contract

    @EndpointRoute("/api/v2/xml/user/show/@username/followers")
    def findUserFollowers = {
        List followers = contract.followers(routeParams.username)
        
        xmlResponse {
            users(type: "array") {
                followers.each { follower ->
                    user(follower)
                }   
            }
        }
    } 

}

/**
 * This interface captures the essence, or contract if you will
 * of the http service end point without bothering with how 
 * the actual http requests look like. 
 * @author Kim A. Betti
 */
interface GitHubFollowers {
    
    List<String> followers(String username);
    
}