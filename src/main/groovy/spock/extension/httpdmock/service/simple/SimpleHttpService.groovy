package spock.extension.httpdmock.service.simple;

import spock.extension.httpdmock.request.WrappedRequest
import spock.extension.httpdmock.response.HttpResponseStub

/**
 * Specifications will create a mock of this interface and use
 * the mock to make assertions against future http requests.
 * @author Kim A. Betti
 */
public interface SimpleHttpService {

    HttpResponseStub request(String target, WrappedRequest request) 
    
}