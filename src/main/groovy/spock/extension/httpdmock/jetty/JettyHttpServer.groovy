package spock.extension.httpdmock.jetty

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.mortbay.jetty.*
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.servlet.*

import spock.extension.httpdmock.HttpServer;

/**
 * 
 * @author Kim A. Betti
 */
class JettyHttpServer {
    
    List requestHandlers = []
    Server server 
    
    public void leftShift(Handler handler) {
        requestHandlers << handler
    }
    
    public void start(int port) {
        createServer(port)
        server.start()
        waitFor(max: 3000, interval: 100) {
            server.isStarted()
        }
    }
    
    protected void createServer(int httpPort) {
        server = new Server(httpPort)
        requestHandlers.each { Handler handler ->
            server.addHandler(handler)   
        }
    }

    public void stop() {
        if (server != null) {
            server.stop()
            waitFor(max: 3000, interval: 100) {
                server.isStopped()
            }
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

