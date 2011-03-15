package kim.spock.httpmock;

import org.spockframework.runtime.extension.IMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;
import org.spockframework.runtime.model.FieldInfo;

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
        int port = serverAnnotation.port();
        TestHttpServer httpServer = null;
        try {
            passServerMock(serverField, invocation.getTarget());
            httpServer = new TestHttpServer(port);
            invocation.proceed();
        } finally {
            TestHttpServer.mock = null;
            if (httpServer != null) {
                httpServer.stop();
            }
        }
    }

    /**
     * Passes the mock (must be created inside the spec) to the test http server
     * so it can translate http requests into method invocations on the mock.
     */
    private void passServerMock(FieldInfo serverField, Object target) {
        TestHttpServer.mock = (HttpServer) serverField.readValue(target);
    }

}