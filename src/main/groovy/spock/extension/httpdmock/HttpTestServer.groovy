package spock.extension.httpdmock;

import spock.extension.httpdmock.route.Route

/**
 * 
 * @author Kim A. Betti
 */
public interface HttpTestServer {

    String getBaseUri()
    Integer getPort()
    
    void start()
    void stop()
    
    void addRoute(Route route, Closure handler)
    
}