package org.laruche.james.agent.behavior;

import jade.content.onto.Ontology;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laruche.james.agent.AbstractAgent;
import org.laruche.james.test.AbstractAgentTestCase;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractTickingBehaviorTest extends AbstractAgentTestCase<Integer> {

    @BeforeEach
    void setUp() {
        this.setTestResult(new TestResult<>(0));
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    ///// Tests unitaires :

    @Test
    void shouldExecuteTask() throws Exception {
        this.agentPlugin.addAgentToStart("testAgent", new TickingTestAgent(this.getTestResult()));
        this.agentPlugin.start();
        Thread.sleep(3000);
        assertThat(this.agentPlugin.isStarted()).isTrue();
        assertThat(this.getTestResult().getValue()).isGreaterThan(0);
    }

    ///// Classe(s) Interne(s)

    private static class TickingTestAgent extends AbstractAgent {

        private TickingTestAgent(final TestResult<Integer> testResult) {
            this.addBehaviour(new TestTicketingBehavior(2000, testResult));
        }

        @Override
        protected Ontology getOntologyInstance() {
            return null;
        }
    }

    private static class TestTicketingBehavior extends AbstractTickingBehavior {
        private final TestResult<Integer> testResult;

        private TestTicketingBehavior(final long delay,
                                      final TestResult<Integer> testResult) {
            super(delay);
            this.testResult = testResult;
        }

        @Override
        protected void doClickAction() {
            testResult.setValue(this.testResult.getValue() + 1);
        }
    }
}