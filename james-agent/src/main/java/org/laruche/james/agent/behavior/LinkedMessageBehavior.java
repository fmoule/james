package org.laruche.james.agent.behavior;

import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * <p>
 * Comportement
 * </p>
 */
public class LinkedMessageBehavior extends AbstractHandlingMessageBehaviour {
    private final Map<Predicate<ACLMessage>, AbstractHandlingMessageBehaviour> behaviors = new HashMap<>();

    ///// Méthode de la classe AbstractHandlingMessageBehavior :

    @Override
    public void doAction(final ACLMessage message) {
        for (Predicate<ACLMessage> predicate : behaviors.keySet()) {
            if (predicate.test(message)) {
                behaviors.get(predicate).doAction(message);
                return;
            }
        }
    }

    /**
     * Ajout d'un prédicat avec le comportement associé. <br />
     *
     * @param predicate : prédicat associé
     * @param behavior  : comportement
     */
    public void addBehavior(final Predicate<ACLMessage> predicate, final AbstractHandlingMessageBehaviour behavior) {
        this.behaviors.put(predicate, behavior);
    }


}
