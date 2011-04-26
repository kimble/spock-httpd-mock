package spock.extension.httpdmock.request

import groovy.lang.Closure
import groovy.xml.MarkupBuilder

import java.util.Map

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 
 * @author Kim A. Betti
 */
class ClosureEndpointDelegate {
    
    final HttpServletRequest request
    final HttpServletResponse response
    
    final Map route
    final Map params = [:]
    
    public ClosureEndpointDelegate(HttpServletRequest request, HttpServletResponse response, Map routeParams) {
        this.request = request
        this.response = response
        this.route = routeParams
        populateParameterMap(request)
    }
    
    private Map populateParameterMap(HttpServletRequest request) {
        request.parameterMap.each { String name, def value ->
            params[name] = value.length == 1 ? value[0] : value
        }
    }
    
    private void xmlResponse(Closure xml) {
        response.contentType = "text/xml"
        MarkupBuilder xmlBuilder = new MarkupBuilder(response.writer)
        xml.delegate = xmlBuilder
        xml.resolveStrategy = Closure.DELEGATE_ONLY
        xml.call()
    }
    
}