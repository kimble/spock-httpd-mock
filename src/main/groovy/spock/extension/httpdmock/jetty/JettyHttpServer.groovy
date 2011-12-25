package spock.extension.httpdmock.jetty

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import spock.extension.httpdmock.HttpTestServer
import spock.extension.httpdmock.route.Route
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.Server

/**
 * Jetty rocks! 
 * @author Kim A. Betti
 */
class JettyHttpServer implements HttpTestServer {
    
    Logger log = LoggerFactory.getLogger(JettyHttpServer);

    HandlerList handlerList = new HandlerList()
    Server server
    
    Integer port
    
    void addRoute(Route route, Closure handler) {
        log.info("Adding route {} to Jetty", route.pattern)
        def jettyHandler = new JettyRouteAdapter(route: route, handler: handler)
        handlerList.addHandler(jettyHandler)
    }
    
    public void start() {
        createServer(port)
        server.start()
        waitFor(max: 3000, interval: 100, server.&isStarted)
    }
    
    protected void createServer(int httpPort) {
        server = new Server(httpPort)
        server.setHandler(handlerList)
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

