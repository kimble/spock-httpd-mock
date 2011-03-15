package spock.extension.httpdmock.jetty

import spock.lang.Specification

/**
 * 
 * @author Kim A. Betti
 */
class JettyHttpServerSpec extends Specification {

    def "Should be able to start and stop Jetty"() {
        when: "we say start"
        JettyHttpServer jetty = new JettyHttpServer()
        jetty.start(15006)    
        
        then: "the server should be started by the time the method returns"
        jetty.server.isStarted()
        
        when: "we say stop"
        jetty.server.stop()
        
        then: "the server should be stopped when the method returns"
        jetty.server.isStopped()
    }
    
    protected String fetch(String url) {
        new URL(url).text
    }
    
}
