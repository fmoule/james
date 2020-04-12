package org.laruche.james.message;

import jade.content.AgentAction;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BeanOntology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class MessageUtils {
    public static final Codec DEFAULT_LANGUAGE = new SLCodec();

    private MessageUtils() {
        // EMPTY
    }

    ///// Gestion générale des message


    public static ACLMessage createMessage(final AID senderAID, final AID receiver, final int performative) {
        final ACLMessage message = new ACLMessage(performative);
        message.setLanguage(DEFAULT_LANGUAGE.getName());
        message.setSender(senderAID);
        message.addReceiver(receiver);
        return message;
    }

    /**
     * Methode permettant de créer un message. <br />
     *
     * @param contentManager : gestionnaire de contenu
     * @param senderAID      : AID de l'envoyeur
     * @param receiver       : AID du destinataire
     * @param performative   : Type du message
     * @param ontology       : Ontologie utilisée
     * @param agentAction    : Action de l'agent
     * @return message JADE
     * @throws CodecException
     * @throws OntologyException
     */
    public static ACLMessage createMessage(final ContentManager contentManager,
                                           final AID senderAID,
                                           final AID receiver,
                                           final int performative,
                                           final BeanOntology ontology,
                                           final AgentAction agentAction)
            throws CodecException, OntologyException {
        final ACLMessage message = createMessage(senderAID, receiver, performative);
        contentManager.registerOntology(ontology);
        message.setOntology(ontology.getName());
        contentManager.fillContent(message, new Action(senderAID, agentAction));
        return message;
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

}
