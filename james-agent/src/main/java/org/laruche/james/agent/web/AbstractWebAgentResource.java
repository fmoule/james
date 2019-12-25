package org.laruche.james.agent.web;

import jade.content.AgentAction;
import jade.content.lang.Codec;
import jade.content.onto.BeanOntology;
import jade.content.onto.OntologyException;
import jade.core.AID;

import static org.laruche.james.message.MessageUtils.createMessage;

/**
 * <p>
 * Classe abstraite représentant les ressources Jersey susceptibles d'etre gérées
 * par les agents de type WebAgent
 * </p>
 *
 * @see WebAgent
 */
public abstract class AbstractWebAgentResource {
    private WebAgent webAgent;

    public WebAgent getWebAgent() {
        return webAgent;
    }

    public void setWebAgent(final WebAgent webAgent) {
        this.webAgent = webAgent;
    }

    protected void sendMessage(final AID receiverAID,
                               final int performative,
                               final BeanOntology ontology,
                               final AgentAction agentAction)
            throws Codec.CodecException, OntologyException {
        final WebAgent webAgent = this.getWebAgent();
        webAgent.send(createMessage(webAgent.getContentManager(),
                webAgent.getAID(),
                receiverAID,
                performative,
                ontology,
                agentAction));
    }
}
