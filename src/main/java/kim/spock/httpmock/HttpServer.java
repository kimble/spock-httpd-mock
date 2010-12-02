package kim.spock.httpmock;

/**
 * 
 * @author Kim A. Betti
 */
public interface HttpServer {

	Object request(String method, String uri);
	
}