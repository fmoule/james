package org.laruche.james.agent.behavior;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Ce comportement d'agent permet d'exécuter séquentiellement des taches avec un message reçu. <br />
 *
 * @see jade.core.behaviours.SequentialBehaviour
 * </p>
 */
public class SequentialHandlingMessageBehaviour extends AbstractHandlingMessageBehaviour {
    private final List<AbstractHandlingMessageBehaviour> subBehaviours = new ArrayList<>();

    public SequentialHandlingMessageBehaviour() {
        super();
    }

    public SequentialHandlingMessageBehaviour(final MessageTemplate msgPredicate) {
        super(msgPredicate);
    }

    @Override
    public void doAction(final ACLMessage message) {
        for (AbstractHandlingMessageBehaviour subBehaviour : subBehaviours) {
            subBehaviour.doAction(message);
        }
    }

    /**
     * Permet d'ajouter un comportement à exécuter avec le message. <br />
     *
     * @param handlingMessageBehavior : comportement
     */
    public void addSubBehaviour(final AbstractHandlingMessageBehaviour handlingMessageBehavior) {
        this.subBehaviours.add(handlingMessageBehavior);
    }
}
