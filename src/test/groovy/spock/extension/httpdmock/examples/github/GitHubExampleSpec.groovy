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
 * Example using parts of the GitHub API
 * @see http://develop.github.com
 * @author Kim A. Betti
 */
class GitHubExampleSpec extends Specification {
  
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
 * Implements a request-to-contract translator for 
 * parts of the GitHub REST API. 
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
 * Declares a input - output contract for parts
 * of the GitHub followers REST API. 
 * @author Kim A. Betti
 */
interface GitHubFollowers {
    
    List<String> followers(String username);
    
}