package kim.spock.httpmock;

import java.util.Map;
import java.util.Properties;
import java.io.IOException;

/**
 * Starts a very light weight http server on the given port. Once the server has
 * started its main task is to translate http requests into method invocations
 * on the mock.
 * 
 * @author Kim A. Betti
 */
public class TestHttpServer extends NanoHTTPD {

    public static HttpServer mock;
    public static final String DEFAULT_MIME_TYPE = "text/html; charset=UTF-8";

    public TestHttpServer(int port) throws IOException {
        super(port);
    }

    @Override
    public Response serve(String uri, String httpMethod, Properties headers, Properties params) {
        Response response = new Response(HTTP_OK, DEFAULT_MIME_TYPE, "");
        Object mockReturnValue = mock.request(httpMethod.toLowerCase(), uri, params, headers);

        if (mockReturnValue == null) {
            response.status = HTTP_INTERNALERROR;
            response.setData("Unexpected " + httpMethod + " request to " + uri + "\r\n");
        } else {
            updateResponse(response, mockReturnValue);
        }

        return response;
    }

    @SuppressWarnings("unchecked")
    private void updateResponse(Response response, Object mockReturnValue) {
        if (mockReturnValue instanceof String) {
            response.setData((String) mockReturnValue);
        } else if (mockReturnValue instanceof Map) {
            Map<String, ?> responseMap = (Map<String, ?>) mockReturnValue;
            updateResponseFromMap(response, responseMap);
        }
    }

    private void updateResponseFromMap(Response response, Map<String, ?> responseMap) {
        if (responseMap.containsKey("body")) {
            response.setData((String) responseMap.get("body"));
        }

        if (responseMap.containsKey("status")) {
            response.status = (String) responseMap.get("status");
        }

        if (responseMap.containsKey("mime")) {
            response.mimeType = (String) responseMap.get("mime");
        }
    }

}