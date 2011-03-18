package spock.extension.httpdmock.service.simple;

import spock.extension.httpdmock.request.WrappedRequest
import spock.extension.httpdmock.response.HttpResponseStub

/**
 * ...
 * @author Kim A. Betti
 */
public interface SimpleHttpService {

    HttpResponseStub request(String target, WrappedRequest request) 
    
}