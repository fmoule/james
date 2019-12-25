package org.laruche.james.test;

import jade.content.AgentAction;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import org.laruche.james.agent.AbstractAgent;
import org.laruche.james.agent.behavior.AbstractHandlingMessageBehavior;
import org.laruche.james.agent.behavior.SequentialHandlingMessageBehavior;
import org.laruche.james.message.MessageUtils;
import org.laruche.james.plugin.AgentPlugin;

import java.util.HashMap;
import java.util.Map;

import static jade.lang.acl.ACLMessage.*;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.or;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.laruche.james.test.AbstractAgentTestCase.TestResultStatus.ERROR;

public abstract class AbstractAgentTestCase<T> {
    protected AgentPlugin agentPlugin;
    protected TestManagerAgent<T> testManagerAgent;

    public AbstractAgentTestCase() {
        this.agentPlugin = new AgentPlugin("agentPlugin");
        this.testManagerAgent = new TestManagerAgent<>();
    }

    protected void tearDown() throws Exception {
        if (agentPlugin.isStarted()) {
            agentPlugin.close();
        }
    }

    ///// Méthodes générales :


    protected void sendMessage(final ACLMessage message) {
        testManagerAgent.send(message);
    }

    protected void sendMessage(final String receiverName,
                               final int performative,
                               final BeanOntology beanOntology,
                               final AgentAction agentAction)
            throws Codec.CodecException, OntologyException {
        this.sendMessage(this.createMessage(new AID(receiverName, false), performative, beanOntology, agentAction));
    }


    ////// Getters & Setters :

    protected TestResult<T> getTestResult() {
        return this.testManagerAgent.getTestResult();
    }

    protected void setTestResult(final TestResult<T> testResult) {
        this.testManagerAgent.setTestResult(testResult);
    }

    private AID getAID() {
        return this.testManagerAgent.getAID();
    }

    private ContentManager getContentManager() {
        return this.testManagerAgent.getContentManager();
    }

    private ACLMessage createMessage(final AID receiverAID,
                                     final int performative,
                                     final BeanOntology ontology,
                                     final AgentAction agentAction)
            throws Codec.CodecException, OntologyException {
        return MessageUtils.createMessage(this.getContentManager(),
                this.getAID(),
                receiverAID,
                performative,
                ontology,
                agentAction);
    }


    ////////////////////////
    ///// Classes Internes :
    ////////////////////////

    /**
     * Agent permettant d'interroger les autres agents lors des tests unitaires. <br />
     */
    public static class TestManagerAgent<U> extends AbstractAgent {
        private TestResult<U> testResult = new TestResult<>();
        private final Map<Integer, SequentialHandlingMessageBehavior> msgHandlingBehaviour = new HashMap<>();
        private Ontology ontology = null;

        ///// Constructors :

        public TestManagerAgent() {
            super();
            final GetResponseMessageBehavior<U> getResponseMessageBehavior = new GetResponseMessageBehavior<>(testResult);
            this.addHandlingMessageBehaviour(FAILURE, new ShowErrorBehavior<>(testResult));
            this.addHandlingMessageBehaviour(INFORM, getResponseMessageBehavior);
            this.addHandlingMessageBehaviour(REQUEST, getResponseMessageBehavior);
        }

        public TestManagerAgent(final Ontology ontology) {
            this();
            this.ontology = ontology;
        }

        ///// Méthodes générales

        public void addHandlingMessageBehaviour(final Integer performative, final AbstractHandlingMessageBehavior handlingMessageBehavior) {
            final SequentialHandlingMessageBehavior seqBehaviour;
            if (!msgHandlingBehaviour.containsKey(performative)) {
                seqBehaviour = new SequentialHandlingMessageBehavior(MatchPerformative(performative));
                this.addBehaviour(seqBehaviour);
                msgHandlingBehaviour.put(performative, seqBehaviour);
            } else {
                seqBehaviour = msgHandlingBehaviour.get(performative);
            }
            seqBehaviour.addSubBehaviour(handlingMessageBehavior);
        }

        @Override
        protected Ontology getOntologyInstance() {
            return ontology;
        }

        ///// Getters & Setters :

        TestResult<U> getTestResult() {
            return testResult;
        }

        void setTestResult(final TestResult<U> testResult) {
            this.testResult = testResult;
        }
    }

    /**
     * Agent permettant de tester.
     */
    public static class SimpleTestAgent extends AbstractAgent {
        private Ontology ontology = null;

        @Override
        protected Ontology getOntologyInstance() {
            return ontology;
        }

        public void setOntology(final Ontology ontology) {
            this.ontology = ontology;
        }
    }

    ///// Behavior :

    private static class ShowErrorBehavior<U> extends AbstractHandlingMessageBehavior {
        private final TestResult<U> testResult;

        ShowErrorBehavior(final TestResult<U> testResult) {
            super(MatchPerformative(FAILURE));
            this.testResult = testResult;
        }

        @Override
        public void doAction(final ACLMessage errorMessage) {
            if (errorMessage != null) {
                final String message = "ERROR : " + (errorMessage.getContent() == null ? "null" : errorMessage.getContent());
                System.out.println(message);
                this.testResult.setInError(message);
            }
        }
    }

    private static class GetResponseMessageBehavior<U> extends AbstractHandlingMessageBehavior {
        private final TestResult<U> testResult;

        private GetResponseMessageBehavior(final TestResult<U> testResult) {
            super(or(MatchPerformative(INFORM), MatchPerformative(REQUEST)));
            this.testResult = testResult;
        }

        @Override
        public void doAction(final ACLMessage respMessage) {
            if (respMessage != null) {
                this.testResult.setResponseMessage(respMessage.getContent());
            }
        }
    }

    /**
     * Objet JAVA représentant le résultat d'un test unitaire. <br />
     *
     * @param <V> : type de la valeur du test.
     */
    public static class TestResult<V> {
        private static final String EMPTY_STRING = "";
        private TestResultStatus status = TestResultStatus.OK;
        private String errorMessage = EMPTY_STRING;
        private String responseMessage = EMPTY_STRING;
        private V value;

        public TestResult(final V value) {
            this.value = value;
        }

        public TestResult() {
            this(null);
        }

        ///// Méthodes générales :

        public void setInError(final String errorMessage) {
            this.status = ERROR;
            this.errorMessage = errorMessage;
            this.responseMessage = EMPTY_STRING;
        }

        public void appendResponseMessage(final String message) {
            if (!isEmpty(message)) {
                this.responseMessage = this.responseMessage + message;
            }
        }

        ///// Getters & Setters :

        public V getValue() {
            return value;
        }

        public void setValue(final V value) {
            this.value = value;
        }

        public boolean isInError() {
            return this.status == ERROR;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

        public String getResponseMessage() {
            return responseMessage;
        }

        public void setResponseMessage(final String responseMessage) {
            this.responseMessage = responseMessage;
        }
    }

    /**
     * Statut du résultat du test. <br />
     */
    public enum TestResultStatus {
        OK,
        ERROR
    }


}
