package kim.spock.httpmock;

public interface HttpServer {

	Object request(String method, String uri);
	
}