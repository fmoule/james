package org.laruche.james.agent.behavior;

import jade.content.AgentAction;
import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.core.AID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laruche.james.bean.TestBeanOntology.AddTestBeanAction;
import org.laruche.james.test.AbstractAgentTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static jade.core.AID.ISLOCALNAME;
import static jade.lang.acl.ACLMessage.INFORM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.laruche.james.bean.TestBeanOntology.TEST_BEAN_ONTOLOGY;

public class AbstractBehaviorTest extends AbstractAgentTestCase<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBehaviorTest.class);
    private final SendingMessageAgent senderAgent = new SendingMessageAgent();

    @BeforeEach
    void setUp() {

        // Liste des agents à démarrer :
        this.agentPlugin.addAgentToStart("testManagerAgent", this.testManagerAgent);
        this.agentPlugin.addAgentToStart("senderAgent", senderAgent);

        // Initialisation de l'ontologie :
        this.testManagerAgent.setOntology(TEST_BEAN_ONTOLOGY);
    }

    @AfterEach
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    ///// Unit tests :

    @Test
    public void shouldSendMessage() throws Exception {
        senderAgent.addMessageToSend("testManagerAgent", INFORM, "Message envoyé");
        startAgentPlugin();
        final TestResult<String> testResult = this.getTestResult();
        assertThat(testResult).isNotNull();
        assertThat(testResult.getResponseMessage()).isEqualTo("Message envoyé");
    }


    @Test
    public void shouldSendMessageWithAction() throws Exception {
        senderAgent.addActionToSend("testManagerAgent", INFORM, TEST_BEAN_ONTOLOGY, new AddTestBeanAction("frederic", "moule"));
        startAgentPlugin();
        final TestResult<String> testResult = this.getTestResult();
        assertThat(testResult).isNotNull();
        assertThat(testResult.isInError()).isFalse();
        assertThat(testResult.getResponseMessage().contains("createTestBean")).isTrue();
    }

    ///// Classes internes /////

    private static class SendingMessageAgent extends SimpleTestAgent {

        public void addMessageToSend(final String receiver,
                                     final int performative,
                                     final String msgContent) {
            this.addBehaviour(new SendingMessageBehavior(receiver, performative, msgContent));
        }

        public void addActionToSend(final String receiver, final int performative, final BeanOntology ontology, final AgentAction action) {
            final SendingMessageBehavior behaviour = new SendingMessageBehavior(receiver, performative, action);
            behaviour.setOntology(ontology);
            this.addBehaviour(behaviour);
        }
    }

    private static class SendingMessageBehavior
            extends AbstractBehavior {
        private String receiverID;
        private int performative;
        private Object content;
        private Ontology ontology = null;

        public SendingMessageBehavior(final String receiverID, final int performative, final Object message) {
            this.receiverID = receiverID;
            this.performative = performative;
            this.content = message;
        }

        @Override
        public void action() {
            final AID receiver = new AID(receiverID, ISLOCALNAME);
            try {
                if (content instanceof String) {
                    this.sendMessage(receiver, performative, (String) content);
                } else if (content instanceof AgentAction) {
                    this.sendMessage(receiver, performative, this.ontology, (AgentAction) content);
                }
            } catch (final Exception e) {
                LOGGER.error(e.getMessage(), e);
                this.sendFailureMessage(receiver, "Erreur " + e.getMessage());
            }
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if ((obj == null) || (this.getClass() != obj.getClass())) {
                return false;
            }
            final SendingMessageBehavior that = (SendingMessageBehavior) obj;
            boolean areEquals = Objects.equals(receiverID, that.receiverID);
            areEquals = areEquals && (Objects.equals(performative, that.performative));
            areEquals = areEquals && (Objects.equals(content, that.content));
            return areEquals;
        }

        @Override
        public int hashCode() {
            final int prime = 17;
            int code = Objects.hashCode(this.receiverID);
            code = (prime * code) + (Objects.hashCode(this.performative));
            code = (prime * code) + (Objects.hashCode(this.content));
            return code;
        }

        public void setOntology(final Ontology ontology) {
            this.ontology = ontology;
        }
    }
}