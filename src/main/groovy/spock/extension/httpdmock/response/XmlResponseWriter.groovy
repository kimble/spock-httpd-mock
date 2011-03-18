package spock.extension.httpdmock.response

import groovy.xml.MarkupBuilder;

import javax.servlet.http.HttpServletResponse;

/**
 * Quick and convenient way of producing XML responses.
 * @author Kim A. Betti
 */
class XmlResponseWriter {

    static build(HttpServletResponse response, Closure xmlClosure) {
        response.contentType = "text/xml"
        MarkupBuilder xmlBuilder = new MarkupBuilder(response.writer)
        xmlClosure.delegate = xmlBuilder
        xmlClosure.resolveStrategy = Closure.DELEGATE_ONLY
        xmlClosure.call()
    }
    
}