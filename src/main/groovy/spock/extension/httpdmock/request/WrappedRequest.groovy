package spock.extension.httpdmock.request

import javax.servlet.http.HttpServletRequest;

/**
 * Convenience methods for making assertions 
 * against http servlet requests.
 * @author Kim A. Betti
 */
class WrappedRequest {
    
    HttpServletRequest request
    Map params = [:]
    
    public WrappedRequest(HttpServletRequest request) {
        createParamMap(request)
        this.request = request
    }
    
    Map createParamMap(HttpServletRequest request) {
        request.parameterMap.each { String name, def value ->
            params[name] = value.length == 1 ? value[0] : value
        }
    }
    
    boolean hasHeader(String headerName) {
        request.headerNames.any { String rhn ->
            rhn.compareToIgnoreCase(headerName)
        }
    }
    
    boolean hasParameter(String parameterName) {
        request.parameterNames.any { String rpn ->
            rpn.compareToIgnoreCase(parameterName)
        }
    }
    
    boolean hasParameter(String parameterName, String value) {
        request.getParameter(parameterName) == value
    }
    
}