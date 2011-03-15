package spock.extension.httpdmock;

import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FieldInfo

import spock.extension.httpdmock.jetty.JettyHttpServer
import spock.extension.httpdmock.plugin.simple.SimpleRequestPlugin


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
            jetty = new JettyHttpServer();
            activatePlugins(jetty, serverMock)
            
            jetty.start(serverAnnotation.port());
            invocation.proceed();
        } finally {
            jetty?.stop()
        }
    }
    
    // TODO: We should probably keep a reference to the plugin instances
    // so that they can be notified when the server is about to shut down. 
    protected void activatePlugins(JettyHttpServer jetty, HttpServer serverMock) {
        createPluginInstances().each { SimpleRequestPlugin plugin ->
            println "Activating plugin: $plugin"
            plugin.beforeServerStartup(jetty, serverMock)
        }
    }
    
    protected List createPluginInstances() {
        serverAnnotation.plugins().collect { Class pluginClass ->
            assert SimpleRequestPlugin.isAssignableFrom(pluginClass)
            pluginClass.newInstance()
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
    
}
