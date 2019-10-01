package org.laruche.james.agent.behavior;

public abstract class AbstractOneShotBehavior extends AbstractBehavior {

    @Override
    public void action() {
        this.doOneShotAction();
        this.finish();
    }

    protected abstract void doOneShotAction();
}