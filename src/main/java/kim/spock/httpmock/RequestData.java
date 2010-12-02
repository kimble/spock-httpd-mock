package kim.spock.httpmock;

import java.util.Properties;

/**
 * 
 * @author Kim A. Betti
 */
public class RequestData {

	private final String uri;
	private final String method;
	
	private final Properties params;
	private final Properties headers;
	
	public RequestData(String uri, String method, Properties params, Properties headers) {
		this.uri = uri;
		this.method = method;
		this.params = params;
		this.headers = headers;
	}

	public String getUri() {
		return uri;
	}

	public String getMethod() {
		return method;
	}

	public Properties getParams() {
		return params;
	}

	public Properties getHeaders() {
		return headers;
	}
	
}
