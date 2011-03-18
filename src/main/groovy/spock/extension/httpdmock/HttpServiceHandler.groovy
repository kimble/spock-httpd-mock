package spock.extension.httpdmock;

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This is the main extension point (plugins).
 * @author Kim A. Betti
 */
public interface HttpServiceHandler {

    /**
     * Return true if the request looks like something the service handler
     * should be able to handle. The target will be something like /mathService/adder
     */
    boolean canHandle(String target, HttpServletRequest request) 
    
    /**
     * Invoked if the 'canHandle' method returns true. This is a good place to invoke methods
     * on the http service interface. Remember to set request.handled = true if you've handled
     * the response! 
     */
    void handleRequest(String target, HttpServletRequest request, HttpServletResponse response)
    
}