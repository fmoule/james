package org.laruche.james.agent.web;

import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laruche.james.test.AbstractWebAgentTestCase;

import java.io.File;
import java.util.HashMap;

import static java.lang.Thread.sleep;
import static java.util.Collections.singleton;
import static org.apache.commons.io.FileUtils.writeLines;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.jetty.http.HttpMethod.GET;
import static org.eclipse.jetty.http.HttpStatus.Code.OK;
import static org.laruche.james.agent.web.JettyAgent.*;

class JettyAgentTest extends AbstractWebAgentTestCase<String> {
    private static final String BASE_PATH = JettyAgentTest.class.getResource(".").getPath();

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
    public void shouldStartWithWebappDir() throws Exception {
        final File webappDir = new File(JettyAgentTest.class.getResource("/").getPath(), "webapp");
        assertThat(webappDir.exists()).isTrue();
        assertThat(webappDir.isDirectory()).isTrue();
        this.agentPlugin.addAgentToStart("jettyAgent", createJettyAgentForWebApp("/test", 8080, webappDir.getAbsolutePath()));
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
        final File warFile = new File(JettyAgentTest.class.getResource("/").getPath(), "file.war");
        assertThat(warFile.exists()).isTrue();
        assertThat(warFile.isFile()).isTrue();
        this.agentPlugin.addAgentToStart("jettyAgent", createJettyAgentForWar("/test", 8081, warFile.getAbsolutePath()));
        this.agentPlugin.start();
        sleep(WAITING_TIME);
        final HttpResponse httpResponse = this.sendRequest("http://localhost:8081/test", GET, null);
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
    public void shouldSetWarPathForWebAppContext() throws Exception {
        final File file = new File(BASE_PATH, "testFile.txt");
        writeLines(file, singleton("test"));
        assertThat(file).exists();
        final WebAppContext webAppContext = new WebAppContext();
        setWarPath(webAppContext, file.getAbsolutePath());
        assertThat(webAppContext.getWar()).isEqualTo(file.getAbsolutePath());
    }

    @Test
    public void shouldSetWebbAppPathForWebAppContext() {
        final File webappDir = new File(JettyAgentTest.class.getResource("/").getPath(), "webapp");
        assertThat(webappDir).exists();
        assertThat(webappDir.isDirectory()).isTrue();
        final WebAppContext webAppContext = new WebAppContext();
        setWebAppPath(webAppContext, webappDir.getAbsolutePath());
        assertThat(webAppContext.getResourceBase()).isEqualTo("file://" + webappDir.getAbsolutePath() + "/");
    }

}