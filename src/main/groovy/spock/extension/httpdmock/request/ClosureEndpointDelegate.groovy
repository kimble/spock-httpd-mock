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

    public ClosureEndpointDelegate() {
        // for testing
    }
    
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

    public void xmlResponse(Closure xml) {
        response.contentType = "text/xml"
        MarkupBuilder xmlBuilder = new MarkupBuilder(response.writer)
        xml.delegate = xmlBuilder
        xml.resolveStrategy = Closure.DELEGATE_ONLY
        xml.call()
    }
    
    public void plainResponse(String responseText) {
        response.contentType = "text/plain"
        response.writer << responseText
    }

    public void setHeaders(Closure headerClosure) {
        Map headers = [:]
        headerClosure.delegate = headers
        headerClosure.resolveStrategy = Closure.DELEGATE_ONLY
        headerClosure.call()

        headers.each { headerPropertyName, value ->
            String headerName = propertyToHttpHeader(headerPropertyName)
            response.setHeader(headerName, value)
        }
    }
    
    protected String propertyToHttpHeader(String headerProperty) {
        StringBuilder asHttpHeaderName = new StringBuilder(headerProperty.length() + 4)
        for (int i=0; i<headerProperty.length(); i++) {
            char currentChar = headerProperty.charAt(i)
            if (i == 0) {
                asHttpHeaderName.append(Character.toUpperCase(currentChar))    
            } else {
                if (Character.isUpperCase(currentChar)) {
                    asHttpHeaderName.append('-')
                }
                
                asHttpHeaderName.append(currentChar)
            }
        }
        
        return asHttpHeaderName.toString()
    }
    
}