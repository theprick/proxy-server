package proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * https://spring.io/guides/gs/rest-service/
 * https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-developing-web-applications
 * https://www.baeldung.com/spring-response-header
 * https://mkyong.com/java/apache-httpclient-examples/
 * https://mkyong.com/java/java-11-httpclient-examples/
 * https://openjdk.java.net/groups/net/httpclient/recipes.html
 * https://spring.io/guides/tutorials/react-and-spring-data-rest/
 *
 * REST docs:
 * https://www.baeldung.com/spring-rest-docs
 * (*****)https://docs.spring.io/spring-restdocs/docs/2.0.4.RELEASE/reference/html5/#documenting-your-api-request-parts-payloads-body
 *
 * Spring OpenAPI Integration
 * https://www.baeldung.com/spring-rest-openapi-documentation
 *
 */
@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "http://localhost:4200")
public class ProxyController {

	@RequestMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@PutMapping(path="do")
	public ResponseEntity<String> callMethod(@RequestBody ProxyRequest request) {
        System.out.println(request);

        CloseableHttpClient httpClient = HttpClients.createDefault();
	    try {
            HttpResponse response;
            switch (request.getMethod()) {
                case "GET":
                    response = handleGet(httpClient, request);
                    break;
                case "POST":
                    response = handlePost(httpClient, request);
                    break;
                case "PUT":
                    response = handlePut(httpClient, request);
                    break;
                case "DELETE":
                    response = handleDelete(httpClient, request);
                    break;
                default:
                    // handle not supported
                    return ResponseEntity.badRequest().build();
            }
            HttpHeaders responseHeaders = new HttpHeaders();
            //responseHeaders.add("Access-Control-Allow-Origin", "http://jsonplaceholder.typicode.com/posts");
            Stream.of(response.getAllHeaders())
                    .forEach(header -> responseHeaders.add(header.getName(), header.getValue()));
            String responseBody = getResponseBody(response);

            return ResponseEntity
                    .status(response.getStatusLine().getStatusCode())
                    .headers(responseHeaders)
                    .body(responseBody);
        } catch(Exception ex) {
	        ex.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
	        try {
                httpClient.close();
            } catch (IOException ignored) {}
        }
	}

	private HttpResponse handleGet(HttpClient httpClient, ProxyRequest proxyRequest) throws IOException {
        HttpGet request = new HttpGet(proxyRequest.getUrl());
        addRequestHeaders(proxyRequest, request);
        return httpClient.execute(request);
	}

    private HttpResponse handlePost(HttpClient httpClient, ProxyRequest proxyRequest) throws IOException {
        HttpPost request = new HttpPost(proxyRequest.getUrl());
        ObjectMapper mapper = new ObjectMapper();
        request.setEntity(new StringEntity(mapper.writeValueAsString(proxyRequest.getRequestBody())));
        addRequestHeaders(proxyRequest, request);
        return httpClient.execute(request);
	}
	private HttpResponse handlePut(HttpClient httpClient, ProxyRequest proxyRequest) throws IOException {
        HttpPut request = new HttpPut(proxyRequest.getUrl());
        ObjectMapper mapper = new ObjectMapper();
        request.setEntity(new StringEntity(mapper.writeValueAsString(proxyRequest.getRequestBody())));
        addRequestHeaders(proxyRequest, request);
        return httpClient.execute(request);
	}

	private HttpResponse handleDelete(HttpClient httpClient, ProxyRequest proxyRequest) throws IOException {
        HttpDelete request = new HttpDelete(proxyRequest.getUrl());
        addRequestHeaders(proxyRequest, request);
        return httpClient.execute(request);
	}

    private String getResponseBody(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        StringBuffer result = new StringBuffer();
        byte[] buf = new byte[1024];
        BufferedInputStream input = new BufferedInputStream(entity.getContent());
        int len = 0;
        while(input.available() > 0) {
            len = input.read(buf);
            result.append(new String(buf, 0, len));
        }
        input.close();
        return result.toString();
    }

    private void addRequestHeaders(ProxyRequest proxyRequest, HttpUriRequest request) {
        if(proxyRequest.getHeaders() != null) {
            proxyRequest.getHeaders().forEach(request::addHeader);
        }
    }
}
