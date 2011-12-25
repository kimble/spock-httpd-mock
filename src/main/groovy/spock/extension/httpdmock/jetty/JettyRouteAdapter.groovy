package spock.extension.httpdmock.jetty


import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        handle(target, request, response)
    }

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