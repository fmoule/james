package org.laruche.james.plugin;

import jade.content.onto.Ontology;
import jade.wrapper.AgentController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laruche.james.agent.AbstractAgent;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

class AgentPluginTest {
    private final AgentPlugin agentPlugin = new AgentPlugin("testAgentPlugin");

    @BeforeEach
    void setUp() throws Exception {
        if (agentPlugin.isStarted()) {
            agentPlugin.stop();
        }
    }

    @Test
    void shouldStartTheAgents() throws Exception {
        agentPlugin.start();
        agentPlugin.addAgentToStart("testAgent1", TestAgent.class);
        agentPlugin.addAgentToStart("testAgent2", TestAgent.class);
        assertThat(agentPlugin.isStarted()).isTrue();
        AgentController agentController = agentPlugin.getAgentController("testAgent1");
        assertThat(agentController).isNotNull();
        agentController = agentPlugin.getAgentController("notExisting");
        assertThat(agentController).isNull();
    }

    @Test
    void shouldStartAgentsByInstance() throws Exception {
        final TestAgent testAgent = new TestAgent();
        agentPlugin.addAgentToStart("testAgent", testAgent);
        agentPlugin.start();
        sleep(1000);
        assertThat(agentPlugin.isStarted()).isTrue();
        assertThat(testAgent.isStarted()).isTrue();
        assertThat(agentPlugin.getAgentController("testAgent")).isNotNull();
    }

    ///// Classe(s) Interne(s) :

    public static class TestAgent extends AbstractAgent {

        public TestAgent() {
            super();
        }

        @Override
        protected void doSetUp() throws Exception {
            super.doSetUp();
        }

        @Override
        protected void doTakeDown() throws Exception {
            super.doTakeDown();
        }

        @Override
        protected Ontology getOntology() {
            return null;
        }
    }
}