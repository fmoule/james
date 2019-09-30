package org.laruche.james.message;

import jade.content.AgentAction;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;

import static jade.lang.acl.ACLMessage.FAILURE;

public class MessageUtils {
    public static final Codec DEFAULT_LANGUAGE = new SLCodec();

    private MessageUtils() {
        // EMPTY
    }

    ///// Gestion des réponses :

    public static ACLMessage createResponse(final ACLMessage message,
                                            final int performative,
                                            final String content) {
        final ACLMessage response = message.createReply();
        response.setPerformative(performative);
        response.setContent(content);
        return response;
    }

    public static ACLMessage createResponse(final ACLMessage message,
                                            final int performative,
                                            final Ontology ontology) {
        final ACLMessage response = message.createReply();
        response.setPerformative(performative);
        response.setLanguage(DEFAULT_LANGUAGE.getName());
        response.setOntology(ontology.getName());
        return response;
    }

    /**
     * Création d'une réponse concernant une erreur
     *
     * @param message      : message d'entrée
     * @param errorMessage : contenu du message d'erreur
     * @return réponse
     */
    public static ACLMessage createFailureResponse(final ACLMessage message, final String errorMessage) {
        return createResponse(message, FAILURE, errorMessage);
    }

    public static ACLMessage createMessage(final ContentManager contentManager,
                                           final AID senderAID,
                                           final AID receiver,
                                           final int performative,
                                           final BeanOntology ontology,
                                           final AgentAction agentAction)
            throws Codec.CodecException, OntologyException {
        contentManager.registerOntology(ontology);
        final ACLMessage message = new ACLMessage(performative);
        message.setLanguage(DEFAULT_LANGUAGE.getName());
        message.setOntology(ontology.getName());
        message.addReceiver(receiver);
        fillMessageWithAction(contentManager, senderAID, message, agentAction);
        return message;
    }

    public static void fillMessageWithAction(final ContentManager contentManager,
                                             final AID senderAID,
                                             final ACLMessage message,
                                             final AgentAction agentAction)
            throws Codec.CodecException, OntologyException {
        message.setLanguage(DEFAULT_LANGUAGE.getName());
        contentManager.fillContent(message, new Action(senderAID, agentAction));
    }

    public static void fillMessageWithBean(final ACLMessage message,
                                           final Serializable javaBean) throws IOException {
        if (message == null || javaBean == null) {
            return;
        }
        message.setContentObject(javaBean);
    }
}
