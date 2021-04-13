package org.laruche.james.test;


import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus.Code;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.eclipse.jetty.http.HttpHeader.CONTENT_TYPE;
import static org.eclipse.jetty.http.HttpStatus.getCode;

/**
 * <p>
 * Classe abstraite de base utilisée pour tester les agents "web" du framework. <br />
 * </p>
 *
 * @param <T>
 */
public abstract class AbstractWebAgentTestCase<T> extends AbstractAgentTestCase<T> {
    private HttpClient httpClient;

    protected void setUp() throws Exception {
        httpClient = new HttpClient();
        httpClient.start();
    }

    @Override
    protected void tearDown() throws Exception {
        if (httpClient.isStarted()) {
            httpClient.stop();
        }
        super.tearDown();
    }

    protected HttpResponse sendRequest(final String uri,
                                       final HttpMethod httpMethod,
                                       final Map<String, String> params) throws Exception {
        return this.sendRequest(uri, httpMethod, params, (JSONObject) null);
    }

    protected HttpResponse sendRequest(final String uri,
                                       final HttpMethod httpMethod,
                                       final Map<String, String> params,
                                       final JSONObject body) throws Exception {
        Request request = httpClient
                .newRequest(uri)
                .method(httpMethod)
                .header(CONTENT_TYPE, "application/json");
        if (params != null && !params.isEmpty()) {
            for (String paramName : params.keySet()) {
                request = request.param(paramName, params.get(paramName));
            }
        }
        if (body != null) {
            request = request.content(new StringContentProvider(body.toString()));
        }
        return new HttpResponse(request.send());
    }

    protected HttpResponse sendRequest(final String uri,
                                       final HttpMethod httpMethod,
                                       final Map<String, String> params,
                                       final String jsonBody) throws Exception {
        Request request = httpClient
                .newRequest(uri)
                .method(httpMethod)
                .header(CONTENT_TYPE, "application/json");
        if (params != null && !params.isEmpty()) {
            for (String paramName : params.keySet()) {
                request = request.param(paramName, params.get(paramName));
            }
        }
        if (!isBlank(jsonBody)) {
            request = request.content(new StringContentProvider(jsonBody));
        }
        return new HttpResponse(request.send());
    }

    ///// Classes Internes :

    /**
     * <p>
     * Réponse HTTP d'une requete. <br />
     * </p>
     */
    public static class HttpResponse {
        private final ContentResponse contentResponse;

        HttpResponse(final ContentResponse contentResponse) {
            this.contentResponse = contentResponse;
        }

        public JSONObject toJSON() {
            return new JSONObject(contentResponse.getContentAsString());
        }

        public JSONArray toJSONArray() {
            return new JSONArray(contentResponse.getContentAsString());
        }

        ///// Getters & Setters

        public Code getStatus() {
            return getCode(this.contentResponse.getStatus());
        }

        public String getContentAsString() {
            return (contentResponse == null ? "" : this.contentResponse.getContentAsString());
        }

        public Map<String, String> getHeaders() {
            final Map<String, String> httpFields = new HashMap<>();
            if (this.contentResponse == null) {
                return httpFields;
            }
            for (HttpField field : this.contentResponse.getHeaders()) {
                httpFields.put(field.getName(), field.getValue());
            }
            return httpFields;
        }
    }
}
