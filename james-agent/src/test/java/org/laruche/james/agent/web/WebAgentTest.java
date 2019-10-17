package org.laruche.james.agent.web;

import org.eclipse.jetty.http.HttpMethod;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laruche.james.test.AbstractWebAgentTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public class WebAgentTest extends AbstractWebAgentTestCase<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebAgentTest.class);

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    ///// Tests unitaires :

    @Test
    void shouldStartTheAgent() throws Exception {
        this.agentPlugin.addAgentToStart("webAgent", new WebAgent(8080, ""));
        this.agentPlugin.start();
        sleep(3000);
        assertThat(this.agentPlugin.isStarted()).isTrue();
    }

    @Test
    void shouldGetResponseFromTheWebAgent() throws Exception {
        final WebAgent webAgent = new WebAgent(8080, "");
        webAgent.registerResource("test", TestResource.class);
        this.agentPlugin.addAgentToStart("webAgent", webAgent);
        this.agentPlugin.start();
        sleep(2000);
        assertThat(this.agentPlugin.isStarted()).isTrue();
        final HttpResponse contentResponse = this.sendRequest("http://localhost:8080/test", HttpMethod.GET, null);
        assertThat(contentResponse).isNotNull();
        assertThat(contentResponse.getStatus().isSuccess()).isTrue();
        final JSONObject jsonResponse = contentResponse.toJSON();
        assertThat(jsonResponse).isNotNull();
        assertThat(jsonResponse.getString("test")).isEqualTo("OK");
    }

    ///// Classes Internes :

    @Path("test")
    public static class TestResource {

        @GET
        @Produces("application/json")
        public String sayHello() {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("test", "OK");
            return jsonObject.toString();
        }
    }
}