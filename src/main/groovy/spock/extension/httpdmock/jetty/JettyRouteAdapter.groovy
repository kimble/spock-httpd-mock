package spock.extension.httpdmock.jetty

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.mortbay.jetty.Request
import org.mortbay.jetty.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spock.extension.httpdmock.HttpRouteAdapter
import spock.extension.httpdmock.request.ClosureEndpointDelegate
import spock.extension.httpdmock.route.Route

/**
 * Makes it easy to plug routes into Jetty.  
 * @author Kim A. Betti
 */
class JettyRouteAdapter extends AbstractHandler implements HttpRouteAdapter {

    Logger log = LoggerFactory.getLogger(JettyRouteAdapter)
    
    Route route
    Closure handler

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
        handle(target, request, response)
    }
    
    @Override
    public void handle(String path, HttpServletRequest request, HttpServletResponse response) {
        if (route.matches(path)) {
            invokeHandler(path, request, response)
            updateJettyFlags(request)
        }
    }

    private void updateJettyFlags(Request request) {
        log.debug("Setting handled = true")
        request.setHandled(true)
    }

    private void invokeHandler(String path, HttpServletRequest request, HttpServletResponse response) {
        Map routeParams = route.params(path)
        handler.delegate = new ClosureEndpointDelegate(request, response, routeParams)
        handler.resolveStrategy = Closure.DELEGATE_FIRST
        handler.call()
    }
}