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
public class SequentialHandlingMessageBehavior extends AbstractHandlingMessageBehavior {
    private final List<AbstractHandlingMessageBehavior> subBehaviours = new ArrayList<>();

    public SequentialHandlingMessageBehavior() {
        super();
    }

    public SequentialHandlingMessageBehavior(final MessageTemplate msgPredicate) {
        super(msgPredicate);
    }

    @Override
    public void doAction(final ACLMessage message) {
        for (AbstractHandlingMessageBehavior subBehaviour : subBehaviours) {
            subBehaviour.doAction(message);
        }
    }

    /**
     * Permet d'ajouter un comportement à exécuter avec le message. <br />
     *
     * @param handlingMessageBehavior : comportement
     */
    public void addSubBehaviour(final AbstractHandlingMessageBehavior handlingMessageBehavior) {
        this.subBehaviours.add(handlingMessageBehavior);
    }
}
