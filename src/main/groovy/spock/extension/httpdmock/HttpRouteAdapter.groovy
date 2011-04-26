package spock.extension.httpdmock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spock.extension.httpdmock.route.Route;

/**
 * 
 * @author Kim A. Betti
 */
public interface HttpRouteAdapter {

    void setRoute(Route route)
    void setHandler(Closure handler)
    
    void handle(String path, HttpServletRequest request, HttpServletResponse response)
    
}
