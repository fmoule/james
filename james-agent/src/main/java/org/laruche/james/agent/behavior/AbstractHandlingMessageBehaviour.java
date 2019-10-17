package org.laruche.james.agent.behavior;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public abstract class AbstractHandlingMessageBehaviour extends AbstractBehavior {
    private MessageTemplate msgPredicate;

    protected AbstractHandlingMessageBehaviour(final MessageTemplate msgPredicate) {
        this.msgPredicate = msgPredicate;
    }

    protected AbstractHandlingMessageBehaviour() {
        this.msgPredicate = null;
    }

    @Override
    public void action() {
        final ACLMessage message;
        if (msgPredicate == null) {
            message = receiveMessage();
        } else {
            message = receiveMessage(msgPredicate);
        }
        if (message != null) {
            doAction(message);
            this.block();
        } else {
            this.block();
        }
    }

    /**
     * Méthode définissant l'action de l'agent en fonction du message. <br />
     *
     * @param message : message à traiter
     */
    public abstract void doAction(final ACLMessage message);

}
