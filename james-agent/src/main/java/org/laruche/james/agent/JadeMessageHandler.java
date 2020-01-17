package org.laruche.james.agent;

import jade.content.AgentAction;
import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import static jade.lang.acl.ACLMessage.FAILURE;
import static org.laruche.james.message.MessageUtils.createMessage;
import static org.laruche.james.message.MessageUtils.createResponse;

/**
 * Interface représentant tous les objets gérant des messages JADE. <br />
 *
 * @see jade.lang.acl.ACLMessage
 */
public interface JadeMessageHandler {

    /**
     * Méthode abstraite permettant de donner l'agent
     * sous-jacent. <br />
     *
     * @return agent
     */
    Agent getAgent();

    ///// Méthodes (par défaut) :

    /**
     * Méthode permettant de récupérer le gestionnaire de contenu.
     *
     * @return gestionnaire
     */
    default ContentManager getContentManager() {
        final Agent agent = this.getAgent();
        if (agent == null) {
            return null;
        }
        return agent.getContentManager();
    }

    default ACLMessage receiveMessage() {
        return this.getAgent().receive();
    }

    default ACLMessage receiveMessage(final MessageTemplate msgTemplate) {
        return this.getAgent().receive(msgTemplate);
    }

    /**
     * Méthode permettant d'envoyer un message JADE. <br />
     *
     * @param message : message à envoyer
     */
    default void sendMessage(final ACLMessage message) {
        if (message != null) {
            this.getAgent().send(message);
        }
    }

    default void sendMessage(final AID receiver,
                             final int performative,
                             final String message) {
        final ACLMessage msg = createMessage(this.getAgent().getAID(), receiver, performative);
        msg.setContent(message);
        this.sendMessage(msg);
    }

    default void sendMessage(final AID receiver,
                             final int performative,
                             final Ontology ontology,
                             final AgentAction agentAction)
            throws Codec.CodecException, OntologyException {
        final Agent behaviorAgent = this.getAgent();
        final ACLMessage message = createMessage(behaviorAgent.getAID(), receiver, performative);
        if (ontology != null) {
            message.setOntology(ontology.getName());
            this.getContentManager().registerOntology(ontology);
        }
        this.getContentManager().fillContent(message, new Action(behaviorAgent.getAID(), agentAction));
        behaviorAgent.send(message);
    }

    default void sendResponse(final ACLMessage message,
                              final int performative,
                              final String responseContent) {
        this.getAgent().send(createResponse(message, performative, responseContent));
    }

    default void sendFailureMessage(final ACLMessage message, final Exception e) {
        this.getAgent().send(createResponse(message, FAILURE, "ERROR : " + e.getMessage()));
    }

    default void sendFailureMessage(final AID receiver, final String errorMessage) {
        final Agent agent = this.getAgent();
        final ACLMessage message = createMessage(agent.getAID(), receiver, FAILURE);
        message.setContent(errorMessage);
        agent.send(message);
    }

    ///// Méthodes utilitaires :

    default <T extends AgentAction> T extractAgentActionFromMessage(final ACLMessage message)
            throws Codec.CodecException, OntologyException {
        final ContentElement contentElement = this.getContentManager().extractContent(message);
        if (!(contentElement instanceof Action)) {
            return null;
        }
        try {
            //noinspection unchecked
            return (T) ((Action) contentElement).getAction();
        } catch (final ClassCastException e) {
            throw new Codec.CodecException(e.getMessage(), e);
        }
    }
}
