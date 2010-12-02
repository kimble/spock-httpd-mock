package kim.spock.httpmock;

import java.util.Properties;


/**
 * 
 * @author Kim A. Betti
 */
public interface HttpServer {

	Object request(String method, String uri);

	Object request(String method, String uri, Properties params);
	
	Object request(String method, String uri, Properties params, Properties headers);
	
}