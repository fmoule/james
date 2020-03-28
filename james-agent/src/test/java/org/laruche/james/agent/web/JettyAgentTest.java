package org.laruche.james.agent.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laruche.james.test.AbstractWebAgentTestCase;

import java.util.HashMap;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.jetty.http.HttpMethod.GET;
import static org.eclipse.jetty.http.HttpStatus.Code.OK;
import static org.laruche.james.agent.web.JettyAgent.createJettyAgentForWar;
import static org.laruche.james.agent.web.JettyAgent.createJettyAgentForWebApp;

class JettyAgentTest extends AbstractWebAgentTestCase<String> {

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void shouldStart() throws Exception {
        this.agentPlugin.addAgentToStart("jettyAgent", createJettyAgentForWebApp("/test", 8080, "/webapp"));
        this.agentPlugin.start();
        sleep(WAITING_TIME);
        final HttpResponse httpResponse = this.sendRequest("http://localhost:8080/test", GET, new HashMap<>());
        assertThat(httpResponse).isNotNull();
        assertThat(httpResponse.getStatus()).isEqualTo(OK);
        assertThat(httpResponse.getContentAsString()).isEqualTo("<!doctype html>\n" +
                "<html lang=\"fr\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Page de test</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<br>Test</br>\n" +
                "</body>\n" +
                "</html>");
    }

    @Test
    public void shouldStartWithWar() throws Exception {
        this.agentPlugin.addAgentToStart("jettyAgent", createJettyAgentForWar("/test", 8081, "/file.war"));
        this.agentPlugin.start();
        sleep(WAITING_TIME);
        final HttpResponse httpResponse = this.sendRequest("http://localhost:8081/test", GET, new HashMap<>());
        assertThat(httpResponse).isNotNull();
        assertThat(httpResponse.getStatus()).isEqualTo(OK);
        assertThat(httpResponse.getContentAsString()).isEqualTo("<!doctype html>\n" +
                "<html lang=\"fr\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Page de test</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<br>Test</br>\n" +
                "</body>\n" +
                "</html>");
    }

}