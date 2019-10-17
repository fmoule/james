package org.laruche.james.test;


import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus.Code;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

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
        Request request = httpClient
                .newRequest(uri)
                .method(httpMethod);
        if (params == null || params.isEmpty()) {
            return new HttpResponse(request.send());
        }
        for (String paramName : params.keySet()) {
            request = request.param(paramName, params.get(paramName));
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

        public Code getStatus() {
            return getCode(this.contentResponse.getStatus());
        }
    }
}
