package org.laruche.james.agent.behavior;

import static java.lang.System.currentTimeMillis;

public abstract class AbstractTickingBehavior extends AbstractBehavior {
    private final long delay;
    private transient long lastTimestamp;

    public AbstractTickingBehavior(final long delay) {
        this.delay = delay;
        this.lastTimestamp = currentTimeMillis();
    }


    @Override
    public void action() {
        if (currentTimeMillis() - lastTimestamp >= delay) {
            this.doClickAction();
            this.lastTimestamp = currentTimeMillis();
        }
    }

    /**
     * Exécute l'action.
     */
    protected abstract void doClickAction();

}
