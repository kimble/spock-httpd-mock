package spock.extension.httpdmock.server;

import java.lang.reflect.Field

import org.codehaus.groovy.runtime.InvokerHelper
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FieldInfo

import spock.extension.httpdmock.HttpServerCfg
import spock.extension.httpdmock.HttpServiceEndpoint
import spock.extension.httpdmock.HttpTestServer
import spock.extension.httpdmock.jetty.JettyHttpServiceHandler
import spock.lang.Specification

/**
 * Starts and stops a http test server before / after each feature method
 * invocation. It will also scan the specification for fields annotated
 * with @HttpServiceMock and make sure that they get configured. 
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
        HttpTestServer httpTestServer = createHttpTestServer(serverAnnotation.port())
        injectTestServerIntoSpec(httpTestServer, serverField, invocation.target)
        activateServices(httpTestServer, invocation.target)
        
        try {
            httpTestServer.start()
            invocation.proceed()
        } finally {
            httpTestServer?.stop()
        }
    }
    
    protected HttpTestServer createHttpTestServer(int port) {
        HttpTestServer server = serverAnnotation.serverClass().newInstance()
        server.port = port
        return server
    }

    protected void injectTestServerIntoSpec(HttpTestServer testServer, FieldInfo serverField, Specification spec) {
        InvokerHelper.setProperty(spec, serverField.name, testServer)
    }
    
    protected void activateServices(HttpTestServer httpTestServer, Object target) {
        List serviceHandlers = getServiceHandlersFromSpec(target)
        serviceHandlers.each { serviceHandler ->
            def wrappedHandler = new JettyHttpServiceHandler(serviceHandler)
            httpTestServer << wrappedHandler
        }
    }
    
    protected List getServiceHandlersFromSpec(Specification target) {
        List serviceFields = getServiceFields(target.getClass())
        return serviceFields.collect { Field serviceField ->
            def serviceHandler = createServiceHandler(serviceField)
            serviceHandler.contract = getMockFromSpec(serviceField, target)
            return serviceHandler
        }
    }
    
    protected List getServiceFields(Class targetClass) {
        targetClass.declaredFields.findAll { Field field ->
            field.isAnnotationPresent(HttpServiceEndpoint)
        }
    }
    
    protected def createServiceHandler(Field serviceField) {
        HttpServiceEndpoint serviceAnnotation = serviceField.getAnnotation(HttpServiceEndpoint)
        Class serviceClass = serviceAnnotation.value()
        return serviceClass.newInstance()
    }
    
    protected def getMockFromSpec(Field serviceField, Specification spec) {
        def mock = spec.getProperty(serviceField.name)
        if (mock == null) {
            String specName = spec.getClass().simpleName
            String exMsg = "Field ${serviceField.name} in ${specName} should be initated to Mock()"
            throw new RuntimeException(exMsg)    
        }
        
        return mock
    }

}
