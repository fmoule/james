package org.laruche.james.agent.behavior;

import jade.content.AgentAction;
import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import static jade.lang.acl.ACLMessage.FAILURE;
import static org.laruche.james.message.MessageUtils.createMessage;
import static org.laruche.james.message.MessageUtils.createResponse;

/**
 * <p>
 * Classe de base pour la définition des comportements des agents. <br />
 * </p>
 */
public abstract class AbstractBehavior extends SimpleBehaviour {
    private boolean isDone = false;

    protected void finish() {
        this.isDone = true;
    }

    @Override
    public boolean done() {
        return isDone;
    }

    /**
     * Méthode permettant de récupérer le gestionnaire de contenu.
     *
     * @return gestionnaire
     */
    protected ContentManager getContentManager() {
        final Agent agent = this.getAgent();
        if (agent == null) {
            return null;
        }
        return agent.getContentManager();
    }

    ///// Gestion des messages :

    protected ACLMessage receiveMessage() {
        return this.getAgent().receive();
    }

    protected ACLMessage receiveMessage(final MessageTemplate msgTemplate) {
        return this.getAgent().receive(msgTemplate);
    }

    ///// Envoi des messages :

    protected void sendMessage(final AID receiver, final int performative, final String message) {
        final ACLMessage msg = createMessage(this.getAgent().getAID(), receiver, performative);
        msg.setContent(message);
        this.getAgent().send(msg);
    }

    protected void sendAgentAction(final AID receiver,
                                   final int performative,
                                   final Ontology ontology,
                                   final AgentAction agentAction)
            throws CodecException, OntologyException {
        final Agent behaviorAgent = this.getAgent();
        final ACLMessage message = createMessage(behaviorAgent.getAID(), receiver, performative);
        if (ontology != null) {
            message.setOntology(ontology.getName());
            this.getContentManager().registerOntology(ontology);
        }
        this.getContentManager().fillContent(message, agentAction);
        behaviorAgent.send(message);
    }

    protected void sendResponse(final ACLMessage message,
                                final int performative,
                                final String responseContent) {
        this.getAgent().send(createResponse(message, performative, responseContent));
    }

    protected void sendFailureMessage(final ACLMessage message, final Exception e) {
        this.getAgent().send(createResponse(message, FAILURE, "ERROR : " + e.getMessage()));
    }

    protected void sendFailureMessage(final AID receiver, final String errorMessage) {
        final Agent agent = this.getAgent();
        final ACLMessage message = createMessage(agent.getAID(), receiver, FAILURE);
        message.setContent(errorMessage);
        agent.send(message);
    }

    ///// Actions diverses :

    protected <T extends AgentAction> T extractAgentActionFromMessage(final ACLMessage message) throws CodecException, OntologyException {
        final ContentElement contentElement = this.getContentManager().extractContent(message);
        if (!(contentElement instanceof Action)) {
            return null;
        }
        try {
            //noinspection unchecked
            return (T) ((Action) contentElement).getAction();
        } catch (final ClassCastException e) {
            throw new CodecException(e.getMessage(), e);
        }
    }


}