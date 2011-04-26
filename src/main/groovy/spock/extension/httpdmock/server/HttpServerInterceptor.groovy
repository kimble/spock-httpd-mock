package spock.extension.httpdmock.server;

import java.lang.reflect.Field

import org.codehaus.groovy.runtime.InvokerHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FieldInfo

import spock.extension.httpdmock.EndpointRoute
import spock.extension.httpdmock.HttpServerCfg
import spock.extension.httpdmock.RequestToContract
import spock.extension.httpdmock.HttpTestServer
import spock.extension.httpdmock.route.Route
import spock.lang.Specification

/**
 * Starts and stops a http test server before / after each feature method
 * invocation. It will also scan the specification for fields annotated
 * with @HttpServiceMock and make sure that they get configured. 
 * 
 * @author Kim A. Betti
 */
public class HttpServerInterceptor implements IMethodInterceptor {
    
    Logger log = LoggerFactory.getLogger(HttpServerInterceptor)

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
            log.debug("Starting http test server")
            httpTestServer.start()
            invocation.proceed()
        } finally {
            log.debug("Stopping http test server")
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
        List requestToContractInstances = getRequestToContractInstancesFromSpec(target)
        requestToContractInstances.each { requestToContractInstance ->
            addRequestAdaptersToServer(httpTestServer, requestToContractInstance)
        }
    }
    
    protected List getRequestToContractInstancesFromSpec(Specification target) {
        List serviceFields = getServiceFields(target.getClass())
        return serviceFields.collect { Field serviceField ->
            def requestToContractInstance = instantiateRequestToContractFromField(serviceField)
            requestToContractInstance.contract = getMockFromSpec(serviceField, target)
            return requestToContractInstance
        }
    }
    
    protected List getServiceFields(Class targetClass) {
        targetClass.declaredFields.findAll { Field field ->
            field.isAnnotationPresent(RequestToContract)
        }
    }

    protected def instantiateRequestToContractFromField(Field requestToContractField) {
        RequestToContract serviceAnnotation = requestToContractField.getAnnotation(RequestToContract)
        Class requestToContractClass = serviceAnnotation.value()
        return requestToContractClass.newInstance()
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
    
    protected void addRequestAdaptersToServer(HttpTestServer httpTestServer, def requestToContractInstance) {
        requestToContractInstance.class.declaredFields.each { Field field ->
            if (field.isAnnotationPresent(EndpointRoute)) {
                field.accessible = true
                EndpointRoute endpointRoute = field.getAnnotation(EndpointRoute)
                Route route = new Route(endpointRoute.value())
                Closure handler = field.get(requestToContractInstance)
                httpTestServer.addRoute(route, handler)
            }
        }
    }

}
