package spock.extension.httpdmock;

import spock.extension.httpdmock.request.WrappedRequest
import spock.extension.httpdmock.response.HttpResponseStub

/**
 * 
 * @author Kim A. Betti
 */
public interface HttpTestServer {

    // status?
    
    String getBaseUri()
    
    Integer getPort()
    
    void start()
    
    void stop()
    
}