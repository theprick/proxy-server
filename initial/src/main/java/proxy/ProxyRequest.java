package proxy;

import java.util.Map;

public class ProxyRequest {

    private String method;
    private Map<String, String> headers;
    private Object requestBody;
    private String url;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "ProxyRequest{" +
                "method='" + method + '\'' +
                ", headers=" + headers +
                ", requestBody=" + requestBody +
                ", url='" + url + '\'' +
                '}';
    }
}
