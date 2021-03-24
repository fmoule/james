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

    /**
     * Méthode permettant de mettre fin à l'exécution du comportement. <br />
     */
    protected void finish() {
        this.isDone = true;
    }

    @Override
    public boolean done() {
        return isDone;
    }


}