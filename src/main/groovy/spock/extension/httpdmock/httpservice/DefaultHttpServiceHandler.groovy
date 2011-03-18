package spock.extension.httpdmock.httpservice

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.handler.AbstractHandler;

import spock.extension.httpdmock.HttpServiceHandler;

/**
 * 
 * @author Kim A. Betti
 */
class DefaultHttpServiceHandler extends AbstractHandler {
    
    HttpServiceHandler serviceHandler
    
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
        if (serviceHandler.canHandle(target, request)) {
            response.status = 200 // can be overridden
            serviceHandler.handleRequest(target, request, response)
            request.handled = true    
        }
    }

}