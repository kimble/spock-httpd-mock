package spock.extension.httpdmock.response

import groovy.xml.MarkupBuilder;

/**
 * 
 * @author Kim A. Betti
 */
class XmlHttpResponseStub extends HttpResponseStub {

    static XmlHttpResponseStub build(Closure closure) {
        def builder = new XmlHttpResponseStub(contentType: "application/xml")
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        
        closure.delegate = xml
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        
        builder.responseBody = writer.toString()
        return builder
    }
    
}
