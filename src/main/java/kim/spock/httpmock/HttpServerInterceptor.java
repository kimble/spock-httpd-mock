package kim.spock.httpmock;

import org.spockframework.runtime.extension.IMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;

/**
 * 
 * @author Kim A. Betti
 */
public class HttpServerInterceptor implements IMethodInterceptor {

	private WithHttpServer serverAnnotation;
	
	public HttpServerInterceptor(WithHttpServer serverAnnotation) {
		this.serverAnnotation = serverAnnotation;
	}

	public void intercept(IMethodInvocation invocation) throws Throwable {
		int port = serverAnnotation.port();
		TestHttpServer httpServer = null;
		try {
			httpServer = new TestHttpServer(port);
			invocation.proceed();
		} finally {
			if (httpServer != null) {
				httpServer.stop();
			}
		}
	}
	
}