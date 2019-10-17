package org.laruche.james.agent.behavior;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laruche.james.test.AbstractAgentTestCase;

import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public class SequentialHandlingMessageBehaviourTest extends AbstractAgentTestCase<String> {
    private final SimpleTestAgent testingAgent = new SimpleTestAgent();

    @BeforeEach
    void setUp() {
        this.agentPlugin.addAgentToStart("managerAgent", this.testManagerAgent);
        this.agentPlugin.addAgentToStart("testingAgent", testingAgent);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private static ACLMessage createSimpleMessage(final AID receiver, final int performative, final String content) {
        final ACLMessage message = new ACLMessage(performative);
        message.addReceiver(receiver);
        message.setContent(content);
        return message;
    }

    ///// Tests unitaires :

    @Test
    void shouldExecuteAllTasks() throws Exception {
        final SequentialHandlingMessageBehaviour seqBehaviour = new SequentialHandlingMessageBehaviour();
        final TestResult<String> testResult = this.getTestResult();
        seqBehaviour.addSubBehaviour(new FirstTestBehaviour(testResult));
        seqBehaviour.addSubBehaviour(new SecundTestBehaviour(testResult));
        this.testingAgent.addBehaviour(seqBehaviour);
        this.agentPlugin.start();
        sleep(2000);
        this.sendMessage(createSimpleMessage(testingAgent.getAID(), REQUEST, "message"));
        sleep(1000);
        assertThat(testResult.getResponseMessage()).isEqualTo(";FirstBehaviour => message;SecundBehaviour => message");
    }


    ///// Classes internes :

    private static class FirstTestBehaviour extends AbstractHandlingMessageBehaviour {
        private final TestResult<String> testResult;

        FirstTestBehaviour(final TestResult<String> testResult) {
            super(MatchPerformative(REQUEST));
            this.testResult = testResult;
        }

        @Override
        public void doAction(final ACLMessage message) {
            testResult.appendResponseMessage(";FirstBehaviour => " + message.getContent());
        }
    }

    private static class SecundTestBehaviour extends AbstractHandlingMessageBehaviour {
        private final TestResult<String> testResult;

        SecundTestBehaviour(final TestResult<String> testResult) {
            super(MatchPerformative(REQUEST));
            this.testResult = testResult;
        }

        @Override
        public void doAction(final ACLMessage message) {
            testResult.appendResponseMessage(";SecundBehaviour => " + message.getContent());
        }
    }


}