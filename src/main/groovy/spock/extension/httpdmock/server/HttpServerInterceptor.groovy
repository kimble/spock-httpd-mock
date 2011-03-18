package spock.extension.httpdmock.server;

import java.lang.reflect.Field

import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FieldInfo

import spock.extension.httpdmock.HttpServer
import spock.extension.httpdmock.HttpServerCfg
import spock.extension.httpdmock.HttpServiceHandler
import spock.extension.httpdmock.HttpServiceMock
import spock.extension.httpdmock.httpservice.DefaultHttpServiceHandler
import spock.extension.httpdmock.jetty.JettyHttpServer


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
        HttpServer serverMock = getMockFromSpec(serverField, invocation.getTarget())
        JettyHttpServer jetty
        
        try {
            jetty = new JettyHttpServer()
            activateServices(jetty, invocation.target)
                
            jetty.start(serverAnnotation.port())
            invocation.proceed()
        } finally {
            jetty?.stop()
        }
    }

    protected getMockFromSpec(FieldInfo serverField, Object target) {
        def serverMock = serverField.readValue(target);
        if (!serverMock) {
            String specName = target.getClass().simpleName
            String exMsg = "${specName} should contain a field annotated with something like @HttpServerCfg(port=1234)"
            throw new RuntimeException(exMsg)
        }
        
        return serverMock
    }
    
    protected void activateServices(JettyHttpServer jetty, Object target) {
        List serviceHandlers = getServiceHandlersFromSpec(target)
        serviceHandlers.each { HttpServiceHandler serviceHandler ->
            println "Activating service: " + serviceHandler
            def wrappedHandler = new DefaultHttpServiceHandler(serviceHandler: serviceHandler)
            jetty << wrappedHandler
        }
    }
    
    protected List getServiceHandlersFromSpec(Object target) {
        List serviceFields = getServiceFields(target.getClass())
        return serviceFields.collect { Field serviceField ->
            HttpServiceMock serviceAnnotation = serviceField.getAnnotation(HttpServiceMock)
            Class serviceClass = serviceAnnotation.value()
            
            HttpServiceHandler serviceHandler = serviceClass.newInstance()
            serviceHandler.mock = readFieldValue(serviceField, target)
            return serviceHandler
        }
    }
    
    protected Object readFieldValue(Field field, Object target) {
        field.accessible = true
        return field.get(target)
    }
    
    protected List getServiceFields(Class targetClass) {
        targetClass.declaredFields.findAll { Field field ->
            field.isAnnotationPresent(HttpServiceMock)
        }
    }
    
}
