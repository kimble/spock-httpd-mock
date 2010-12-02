package kim.spock.httpmock;

import java.util.Map;
import java.util.Properties;
import java.io.IOException;

/**
 * 
 * @author Kim A. Betti
 */
public class HttpServerMock extends NanoHTTPD {
	
	public static HttpServer mock;

	public HttpServerMock(int port) throws IOException {
		super(port);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Response serve(String uri, String method, Properties headers, Properties params) {
		
		String status = HTTP_OK;
		String mimeType = "text/html";
		String responseBody = null;
		
		method = method.toLowerCase();
		Object response = mock.request(method, uri);
		if (response == null) 
			return new Response(HTTP_INTERNALERROR, "text/html", "Unexpected " + method + " request to " + uri);

		if (response instanceof String) {
			responseBody = (String) response;
		} else if (response instanceof Map) {
			Map<String, ?> responseMap = (Map<String, ?>) response;
			if (responseMap.containsKey("body"))
				responseBody = (String) responseMap.get("body");
			
			if (responseMap.containsKey("status"))
				status = (String) responseMap.get("status");
			
			if (responseMap.containsKey("mime"))
				mimeType = (String) responseMap.get("mime");
		}
		
		return new Response(status, mimeType, responseBody);
	}

}