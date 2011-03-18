package spock.extension.httpdmock.service.simple

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.mortbay.jetty.handler.AbstractHandler

import spock.extension.httpdmock.HttpServer
import spock.extension.httpdmock.HttpServiceHandler;
import spock.extension.httpdmock.jetty.JettyHttpServer
import spock.extension.httpdmock.request.WrappedRequest
import spock.extension.httpdmock.response.HttpResponseStub

/**
 * 
 * @author Kim A. Betti
 */
class SimpleHttpRequestService implements HttpServiceHandler {
    
    SimpleHttpService mock

    public boolean canHandle(String target, HttpServletRequest request) {
        return true;
    }

    public void handleRequest(String target, HttpServletRequest request, HttpServletResponse response) {
        WrappedRequest wrappedRequest = new WrappedRequest(request)
        HttpResponseStub mockResponse = mock.request(target, wrappedRequest)

        if (mockResponse != null) {
            response.with {
                contentType = mockResponse.contentType
                status = mockResponse.statusCode
                writer.print(mockResponse.responseBody)
            }

            request.handled = true
        }
    }
    
}