package spock.extension.httpdmock.examples.github

import groovy.util.slurpersupport.NodeChild
import groovyx.net.http.HTTPBuilder
import spock.extension.httpdmock.EndpointRoute
import spock.extension.httpdmock.HttpServerCfg
import spock.extension.httpdmock.RequestToContract
import spock.extension.httpdmock.HttpTestServer
import spock.lang.Specification

/**
 * Example using parts of the GitHub API
 * @see http://develop.github.com
 * @author Kim A. Betti
 */
class GitHubExampleSpec extends Specification {
  
    // Test server configuration is injected (port / host)
    @HttpServerCfg
    HttpTestServer server 
    
    // The Spock extension will find this field and wire up 
    // the end-point as a Jetty handler in the test server.
    @RequestToContract(GitHubFollowersRequestToContract)
    FollowerService githubFollowerService = Mock()

    def "Should be able to look up a users followers"() {
        given: "a http client capable of parsing xml responses"
        HTTPBuilder http = new HTTPBuilder(server.baseUri)
        
        when: "we invoke the http service using the http client"
        NodeChild xmlResponse = http.get(path: "/api/v2/xml/user/show/superman/followers")

        then: "the http request is translated into a method call on our Spock mock"
        1 * githubFollowerService.followers("superman") >> [ "lex.luthor", "lana.lang" ]
        
        and: "the xml response is as expected"
        xmlResponse.user*.text() == [ "lex.luthor", "lana.lang" ]
    }
    
}

/**
 * Implements a request-to-contract translator for 
 * parts of the GitHub REST API. 
 * @author Kim A. Betti
 */
class GitHubFollowersRequestToContract {

    FollowerService contract

    @EndpointRoute("/api/v2/xml/user/show/@username/followers")
    def findUserFollowers = { 
        List followers = contract.followers(route.username)
        
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
interface FollowerService {
    
    List<String> followers(String username);
    
}