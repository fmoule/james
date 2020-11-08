package org.laruche.james.agent.web;

import jade.content.AgentAction;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import org.eclipse.jetty.http.HttpMethod;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laruche.james.agent.behavior.AbstractHandlingMessageBehavior;
import org.laruche.james.bean.TestBean;
import org.laruche.james.bean.TestBeanOntology.AddTestBeanAction;
import org.laruche.james.bean.TestBeanOntology.DeleteTestBeanAction;
import org.laruche.james.test.AbstractWebAgentTestCase;

import javax.ws.rs.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static jade.core.AID.ISLOCALNAME;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.or;
import static java.lang.Thread.sleep;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.laruche.james.bean.TestBeanOntology.TEST_BEAN_ONTOLOGY;

public class RESTAgentTest extends AbstractWebAgentTestCase<String> {
    private static final int WAITING_TIME = 5500;
    public static final String DAO_AGENT_ID = "daoAgent";
    public static final String WEB_AGENT_ID = "webAgent";

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        super.tearDown();
        this.agentPlugin.clearAgents();
    }

    ///// Tests unitaires :

    @Test
    void shouldStartTheAgent() throws Exception {
        this.agentPlugin.addAgentToStart(WEB_AGENT_ID, new RESTAgent(8080, ""));
        this.agentPlugin.start();
        sleep(WAITING_TIME);
        assertThat(this.agentPlugin.isStarted()).isTrue();
    }

    @Test
    void shouldGetResponse() throws Exception {
        final RESTAgent RESTAgent = new RESTAgent(8080, "");
        RESTAgent.registerSimpleResource(new TestResource());
        this.agentPlugin.addAgentToStart(WEB_AGENT_ID, RESTAgent);
        this.agentPlugin.start();
        sleep(WAITING_TIME);
        assertThat(this.agentPlugin.isStarted()).isTrue();
        final HttpResponse contentResponse = this.sendRequest("http://localhost:8080/test", HttpMethod.GET, null);
        assertThat(contentResponse).isNotNull();
        assertThat(contentResponse.getStatus().isSuccess()).isTrue();
        final JSONObject jsonResponse = contentResponse.toJSON();
        assertThat(jsonResponse).isNotNull();
        assertThat(jsonResponse.getString("test")).isEqualTo("OK");
    }

    @Test
    public void shouldGetResponseWithBasePath() throws Exception {
        final RESTAgent RESTAgent = new RESTAgent(8080, "basePath");
        RESTAgent.registerSimpleResource(new TestResource());
        this.agentPlugin.addAgentToStart(WEB_AGENT_ID, RESTAgent);
        this.agentPlugin.start();
        sleep(WAITING_TIME);
        assertThat(this.agentPlugin.isStarted()).isTrue();
        final HttpResponse contentResponse = this.sendRequest("http://localhost:8080/basePath/test", HttpMethod.GET, null);
        assertThat(contentResponse).isNotNull();
        assertThat(contentResponse.getStatus().isSuccess()).isTrue();
        final JSONObject jsonResponse = contentResponse.toJSON();
        assertThat(jsonResponse).isNotNull();
        assertThat(jsonResponse.getString("test")).isEqualTo("OK");
    }

    @Test
    public void shouldGetResponseWithBasePathAndSlash() throws Exception {
        final RESTAgent RESTAgent = new RESTAgent(8080, "/basePath");
        RESTAgent.registerSimpleResource(new TestResource());
        this.agentPlugin.addAgentToStart(WEB_AGENT_ID, RESTAgent);
        this.agentPlugin.start();
        sleep(WAITING_TIME);
        assertThat(this.agentPlugin.isStarted()).isTrue();
        final HttpResponse contentResponse = this.sendRequest("http://localhost:8080/basePath/test", HttpMethod.GET, null);
        assertThat(contentResponse).isNotNull();
        assertThat(contentResponse.getStatus().isSuccess()).isTrue();
        final JSONObject jsonResponse = contentResponse.toJSON();
        assertThat(jsonResponse).isNotNull();
        assertThat(jsonResponse.getString("test")).isEqualTo("OK");
    }

    @Test
    void shouldGetResponseFromQueryParameters() throws Exception {
        final RESTAgent RESTAgent = new RESTAgent(8080, "");
        RESTAgent.registerSimpleResource(new TestGetParameterResource());
        this.agentPlugin.addAgentToStart(WEB_AGENT_ID, RESTAgent);
        this.agentPlugin.start();
        sleep(WAITING_TIME);
        assertThat(this.agentPlugin.isStarted()).isTrue();
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("param", "value");
        final HttpResponse contentResponse = this.sendRequest("http://localhost:8080/test", HttpMethod.GET, queryParams);
        assertThat(contentResponse).isNotNull();
        assertThat(contentResponse.getStatus().isSuccess()).isTrue();
        final JSONObject jsonResponse = contentResponse.toJSON();
        assertThat(jsonResponse.getString("param")).isEqualTo("value");
    }

    @Test
    public void shouldGetResponseFromPathParameters() throws Exception {
        final RESTAgent RESTAgent = new RESTAgent(8080, "");
        RESTAgent.registerSimpleResource(new TestGetPathParameterResource());
        this.agentPlugin.addAgentToStart(WEB_AGENT_ID, RESTAgent);
        this.agentPlugin.start();
        sleep(WAITING_TIME);
        assertThat(this.agentPlugin.isStarted()).isTrue();
        final HttpResponse contentResponse = this.sendRequest("http://localhost:8080/test/value", HttpMethod.GET, null);
        assertThat(contentResponse).isNotNull();
        assertThat(contentResponse.getStatus().isSuccess()).isTrue();
        final JSONObject jsonResponse = contentResponse.toJSON();
        assertThat(jsonResponse.getString("param")).isEqualTo("value");
    }

    @Test
    void shouldPutInDAOThroughAgent() throws Exception {
        final RESTAgent RESTAgent = new RESTAgent(8080, "");
        RESTAgent.registerSimpleResource(new TestPutInDAOResource());
        RESTAgent.setOntology(TEST_BEAN_ONTOLOGY);
        this.agentPlugin.addAgentToStart(WEB_AGENT_ID, RESTAgent);
        final TestPersonDaoAgent daoAgent = new TestPersonDaoAgent();
        daoAgent.setOntology(TEST_BEAN_ONTOLOGY);
        this.agentPlugin.addAgentToStart(DAO_AGENT_ID, daoAgent);
        this.agentPlugin.start();
        sleep(WAITING_TIME);
        final Map<String, String> params = new HashMap<>();
        params.put("firstName", "frederic");
        params.put("name", "moule");
        final HttpResponse httpResponse = this.sendRequest("http://localhost:8080/test/put", HttpMethod.PUT, params);
        sleep(WAITING_TIME);
        assertThat(httpResponse).isNotNull();
        assertThat(httpResponse.getStatus().isSuccess()).isTrue();
        assertThat(daoAgent.hasTestBean("frederic", "moule")).isTrue();
    }

    ////////////////////////
    ///// Classes Internes :
    ////////////////////////

    private static class TestPersonDaoAgent extends SimpleTestAgent {
        private Set<TestBean> testBeans = new HashSet<>();


        TestPersonDaoAgent() {
            super();
            this.setOntology(TEST_BEAN_ONTOLOGY);
            this.addBehaviour(new AddTestBeanBehavior());
            this.addBehaviour(new DeleteTestBeanBehavior());
        }

        public boolean hasTestBean(final String firstName, final String name) {
            return this.testBeans.contains(new TestBean(firstName, name));
        }

        //// Comportements :

        private class AddTestBeanBehavior extends AbstractHandlingMessageBehavior {

            AddTestBeanBehavior() {
                super(or(MatchPerformative(REQUEST), MatchPerformative(INFORM)));
            }

            @Override
            public void doAction(final ACLMessage message) {
                try {
                    final AgentAction agentAction = this.extractAgentActionFromMessage(message);
                    if (!(agentAction instanceof AddTestBeanAction)) {
                        return;
                    }
                    testBeans.add(((AddTestBeanAction) agentAction).getTestBean());
                    this.sendResponse(message, INFORM, "Ajout OK");
                } catch (final Exception exception) {
                    this.sendFailureMessage(message, exception);
                }
            }

        }

        private class DeleteTestBeanBehavior extends AbstractHandlingMessageBehavior {

            DeleteTestBeanBehavior() {
                super(or(MatchPerformative(REQUEST), MatchPerformative(INFORM)));
            }

            @Override
            public void doAction(final ACLMessage message) {
                try {
                    final AgentAction agentAction = this.extractAgentActionFromMessage(message);
                    if (!(agentAction instanceof DeleteTestBeanAction)) {
                        return;
                    }
                    final Predicate<TestBean> predicate = ((DeleteTestBeanAction) agentAction).getPredicate();
                    testBeans.removeAll(testBeans.stream().filter(predicate).collect(toList()));
                    this.sendResponse(message, INFORM, "Suppression Ok");
                } catch (final Exception e) {
                    this.sendFailureMessage(message, e);
                }
            }

        }
    }

    ///// Resources utilisées :

    @Path("test")
    public static class TestResource extends RESTAgent.AbstractWebAgentResource {

        @GET
        public String handleGET() {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("test", "OK");
            return jsonObject.toString();
        }
    }

    @Path("test")
    public static class TestGetParameterResource extends RESTAgent.AbstractWebAgentResource {

        @GET
        public String handleGET(@QueryParam("param") final String param) {
            final JSONObject jsonObject = new JSONObject();
            if (!isEmpty(param)) {
                jsonObject.put("param", param);
            }
            return jsonObject.toString();
        }

    }

    @Path("test")
    public static class TestGetPathParameterResource extends RESTAgent.AbstractWebAgentResource {

        @Path("/{param}")
        @GET
        public String handleGET(@PathParam("param") final String paramValue) {
            final JSONObject jsonObject = new JSONObject();
            if (!isEmpty(paramValue)) {
                jsonObject.put("param", paramValue);
            }
            return jsonObject.toString();
        }

    }

    @Path("test/put")
    public static class TestPutInDAOResource extends RESTAgent.AbstractWebAgentResource {

        @PUT
        public String putInDAO(@QueryParam("firstName") final String firstName,
                             @QueryParam("name") final String name) {
            final JSONObject response = new JSONObject();
            try {
                sendMessage(new AID(DAO_AGENT_ID, ISLOCALNAME), REQUEST, TEST_BEAN_ONTOLOGY, new AddTestBeanAction(firstName, name));
                response.put("isInError", false);
                response.put("message", "TestBean[" + firstName + "," + name + "] ajouté");
            } catch (final Exception e) {
                response.put("isInError", true);
                response.put("message", e.getMessage());
            }
            return response.toString();
        }

    }



}