package spock.extension.httpdmock;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.handler.AbstractHandler;
import org.spockframework.runtime.extension.IMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;
import org.spockframework.runtime.model.FieldInfo;

import spock.extension.httpdmock.jetty.JettyHttpServer;
import spock.extension.httpdmock.response.HttpResponseStub;


/**
 * Starts and stops a http test server before / after each feature method
 * invocation.
 * 
 * @author Kim A. Betti
 */
public class HttpServerInterceptor implements IMethodInterceptor {

    private HttpServerCfg serverAnnotation;
    private FieldInfo serverField;

    public HttpServerInterceptor(HttpServerCfg serverAnnotation, FieldInfo serverField) {
        this.serverAnnotation = serverAnnotation;
        this.serverField = serverField;
    }

    public void intercept(IMethodInvocation invocation) throws Throwable {
        int port = serverAnnotation.port()
        JettyHttpServer jetty
        
        try {
            def mock = getMockFromSpec(serverField, invocation.getTarget())
            jetty = new JettyHttpServer();
            jetty << createMockHandler(mock)
            
            jetty.start(port);
            invocation.proceed();
        } finally {
            jetty?.stop()
        }
    }

    protected getMockFromSpec(FieldInfo serverField, Object target) {
        def serverMock = serverField.readValue(target);
        if (!serverMock) {
            String exMsg = "The speck should contain a field annotated with something like @HttpServerCfg(port=1234)"
            throw new RuntimeException(exMsg)
        } else {
            return serverMock
        }
    }

    protected createMockHandler(def mock) {
        new MockHandler(mock: mock)
    }    
    
}

/**
 * Translates http requests into method invocations on the mock
 * @author Kim A. Betti
 */
class MockHandler extends AbstractHandler {

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

class WrappedRequest {
    
    HttpServletRequest request
    Map params = [:]
    
    public WrappedRequest(HttpServletRequest request) {
        createParamMap(request)
        this.request = request
    }
    
    Map createParamMap(HttpServletRequest request) {
        request.parameterMap.each { String name, def value ->
            params[name] = value.length == 1 ? value[0] : value
        }
    }
    
    boolean hasHeader(String headerName) {
        request.headerNames.any { String rhn ->
            rhn.compareToIgnoreCase(headerName)
        }
    }
    
    boolean hasParameter(String parameterName) {
        request.parameterNames.any { String rpn ->
            rpn.compareToIgnoreCase(parameterName)
        }
    }
    
    boolean hasParameter(String parameterName, String value) {
        request.getParameter(parameterName) == value
    }
    
}