package spock.extension.httpdmock.jetty

import org.mortbay.jetty.*
import org.mortbay.jetty.servlet.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import spock.extension.httpdmock.HttpTestServer
import spock.extension.httpdmock.route.Route

/**
 * Jetty rocks! 
 * @author Kim A. Betti
 */
class JettyHttpServer implements HttpTestServer {
    
    Logger log = LoggerFactory.getLogger(JettyHttpServer);

    List<Handler> jettyHandlers = []
    Server server 
    
    Integer port
    
    void addRoute(Route route, Closure handler) {
        log.info("Adding route {} to Jetty", route.pattern)
        def adapter = new JettyRouteAdapter(route: route, handler: handler)
        jettyHandlers << adapter
    }
    
    public void start() {
        createServer(port)
        server.start()
        waitFor(max: 3000, interval: 100, server.&isStarted)
    }
    
    protected void createServer(int httpPort) {
        server = new Server(httpPort)
        jettyHandlers.each { Handler handler ->
            server.addHandler(handler)   
        }
    }

    public String getBaseUri() {
        "http://localhost:$port/"
    }

    public void stop() {
        if (server != null) {
            server.stop()
            waitFor(max: 3000, interval: 100, server.&isStopped)
        }
    }

    protected void waitFor(Map args, Closure condition) {
        while (args.max > 0) {
            if (condition.call() != true) {
                args.max -= args.interval
                Thread.sleep(args.interval)
            } else {
                return
            }
        }
        
        throw new RuntimeException("Wait for expired")
    }
    
}

