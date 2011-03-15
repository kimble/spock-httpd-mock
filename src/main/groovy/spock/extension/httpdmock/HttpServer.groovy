package spock.extension.httpdmock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import spock.extension.httpdmock.response.HttpResponseStub

/**
 * 
 * @author Kim A. Betti
 */
public interface HttpServer {

    HttpResponseStub request(String target, WrappedRequest request) 

}