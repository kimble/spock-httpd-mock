package spock.extension.httpdmock.jetty

import java.io.IOException
import java.lang.reflect.Field;
import java.lang.reflect.Method
import java.util.Map;

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.mortbay.jetty.handler.AbstractHandler

import spock.extension.httpdmock.EndpointRoute;
import spock.extension.httpdmock.request.Route;
import spock.extension.httpdmock.response.XmlResponseWriter;

/**
 * 
 * @author Kim A. Betti
 */
class JettyHttpServiceHandler extends AbstractHandler {
    
    final Map routeHandlerMapping = [ : ]
    
    public JettyHttpServiceHandler(serviceHandler) {
        serviceHandler.class.declaredFields.each { Field field ->
            if (field.isAnnotationPresent(EndpointRoute)) {
                field.accessible = true
                EndpointRoute endpointRoute = field.getAnnotation(EndpointRoute)
                Route route = new Route(endpointRoute.value())
                routeHandlerMapping[route] = field.get(serviceHandler)
            }
        }
    }
    
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
        routeHandlerMapping.each { Route route, Closure handler ->
            if (route.matches(target)) {
                response.status = 200 // By default, can easily be overridden
                
                Map routeParams = route.params(target)
                def delegate = new ClosureEndpointDelegate(request, response, routeParams)
                Closure endpoint = handler.clone()
                endpoint.delegate = delegate
                endpoint.resolveStrategy = Closure.DELEGATE_FIRST
                endpoint.call()
            }
        }
    }

}

class ClosureEndpointDelegate {
    
    final HttpServletRequest request
    final HttpServletResponse response
    
    final Map routeParams
    final Map params = [:]
    
    public ClosureEndpointDelegate(HttpServletRequest request, HttpServletResponse response, Map routeParams) {
        this.request = request
        this.response = response
        this.routeParams = routeParams
        populateParameterMap(request)
    }
    
    private Map populateParameterMap(HttpServletRequest request) {
        request.parameterMap.each { String name, def value ->
            params[name] = value.length == 1 ? value[0] : value
        }
    }
    
    private void xmlResponse(Closure xml) {
        XmlResponseWriter.build(response, xml)
    }
    
}