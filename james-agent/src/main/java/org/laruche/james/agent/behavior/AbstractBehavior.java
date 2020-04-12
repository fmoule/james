package org.laruche.james.agent.behavior;

import jade.core.behaviours.SimpleBehaviour;
import org.laruche.james.agent.JadeMessageHandler;

/**
 * <p>
 * Classe de base pour la définition des comportements des agents. <br />
 * </p>
 */
public abstract class AbstractBehavior extends SimpleBehaviour
        implements JadeMessageHandler {
    private boolean isDone = false;

    protected void finish() {
        this.isDone = true;
    }

    @Override
    public boolean done() {
        return isDone;
    }


}