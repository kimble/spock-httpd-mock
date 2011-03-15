package spock.extension.httpdmock.plugin.simple

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.mortbay.jetty.handler.AbstractHandler

import spock.extension.httpdmock.HttpServer
import spock.extension.httpdmock.RequestHandlerPlugin
import spock.extension.httpdmock.jetty.JettyHttpServer
import spock.extension.httpdmock.request.WrappedRequest
import spock.extension.httpdmock.response.HttpResponseStub

/**
 * 
 * @author Kim A. Betti
 */
class SimpleRequestPlugin implements RequestHandlerPlugin {

    public void beforeServerStartup(JettyHttpServer jetty, HttpServer serverMock) {
        def requestHandler = new SimpleRequestHandler(mock: serverMock)
        jetty << requestHandler
    }

}

/**
* Translates http requests into method invocations on the mock
* @author Kim A. Betti
*/
class SimpleRequestHandler extends AbstractHandler {

   def mock
   def defaultResponseStub = new HttpResponseStub(statusCode: 404, responseBody: "Not found")
   
   @Override
   public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
       WrappedRequest wrappedRequest = new WrappedRequest(request)
       HttpResponseStub mr = mock.request(target, wrappedRequest) ?: defaultResponseStub
       
       response.with {
           contentType = mr.contentType
           status = mr.statusCode
           writer.print(mr.responseBody)
       }
       
       request.handled = true
   }
   
}