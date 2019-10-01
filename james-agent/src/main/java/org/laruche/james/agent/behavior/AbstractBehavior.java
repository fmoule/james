package org.laruche.james.agent.behavior;

import jade.content.AgentAction;
import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.laruche.james.message.MessageUtils;

import java.io.Serializable;

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


    /**
     * @param message     : message à compléter
     * @param agentAction : action d'agent
     * @throws Codec.CodecException    : En cas d'exception du au codec
     * @throws OntologyException : En cas d'exception avec l'ontologie
     */
    protected void fillMessageWithAction(final ACLMessage message,
                                         final AgentAction agentAction) throws Codec.CodecException, OntologyException {
        MessageUtils.fillMessageWithAction(this.getContentManager(), getCurrentAID(), message, agentAction);
    }

    protected <T extends AgentAction> T extractAgentActionFromMessage(final ACLMessage message) throws Codec.CodecException, OntologyException {
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

    protected <T extends Serializable> T extractJavaBeanFromMessage(final ACLMessage message) throws Exception {
        if (message == null) {
            return null;
        }
        try {
            //noinspection unchecked
            return (T) message.getContentObject();
        } catch (final ClassCastException classCastException) {
            throw new Exception(classCastException.getMessage(), classCastException);
        }
    }

    protected void sendMessage(final ACLMessage message) {
        this.getAgent().send(message);
    }

    ///// Getters & Setters :

    /**
     * Retourne l'identifiant de l'agent du comportement; <br />
     *
     * @return identifiant
     */
    protected AID getCurrentAID() {
        return this.getAgent().getAID();
    }
}