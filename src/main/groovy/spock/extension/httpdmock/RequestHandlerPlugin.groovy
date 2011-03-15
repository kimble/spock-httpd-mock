package spock.extension.httpdmock

import spock.extension.httpdmock.jetty.JettyHttpServer

/**
 * Just added the interface, we'll have to think about 
 * which methods a plugin will have to implement and perhaps
 * give it a better name. 
 * 
 * So far I think every plugin should / must have a standard
 * constructor so that the interceptor can create new instances.
 */
interface RequestHandlerPlugin {
    
    // TODO: Define me! 
    
    void beforeServerStartup(JettyHttpServer jettyServer, HttpServer serverMock);
    
}