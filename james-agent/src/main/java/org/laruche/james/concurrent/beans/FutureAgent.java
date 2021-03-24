package org.laruche.james.concurrent.beans;

import jade.content.onto.Ontology;
import org.laruche.james.agent.AbstractAgent;
import org.laruche.james.agent.behavior.AbstractBehavior;
import org.laruche.james.agent.behavior.AbstractOneShotBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.laruche.james.concurrent.ontology.ConcurrentOntology.CONCURRENT_ONTOLOGY;

/**
 * Implémention de Future utilisée par les objets du 'package' concurrent. <br />
 *
 * @param <V> : type du retour
 * @see java.util.concurrent.Future
 */
public class FutureAgent<V> extends AbstractAgent implements Future<V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FutureAgent.class);
    private final AbstractBehavior behavior;
    private V result = null;
    private boolean isCancelled = false;
    private boolean isInError = false;
    private Exception exception = null;

    public FutureAgent(final Runnable runnable) {
        this.behavior = new RunnableBehaviour(runnable);
        this.addBehaviour(this.behavior);
    }

    public FutureAgent(final Runnable runnable, final V result) {
        this.behavior = new CallableBehaviour(() -> {
            runnable.run();
            return result;
        });
        this.addBehaviour(this.behavior);
    }

    public FutureAgent(final Callable<V> callable) {
        this.behavior = new CallableBehaviour(callable);
        this.addBehaviour(this.behavior);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        this.takeDown();
        this.isCancelled = true;
        return true;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public boolean isDone() {
        return behavior.done();
    }

    @Override
    public V get() {
        return result;
    }

    @Override
    public V get(final long timeout, final TimeUnit unit) throws InterruptedException, TimeoutException {
        Thread.sleep(unit.convert(timeout, MILLISECONDS));
        if (isDone()) {
            return result;
        } else {
            throw new TimeoutException("Time out for executing the task");
        }
    }

    ///// Getters & Setters :

    @Override
    protected Ontology getOntology() {
        return CONCURRENT_ONTOLOGY;
    }

    public boolean isInError() {
        return isInError;
    }

    public Exception getException() {
        return exception;
    }

    ///// Classes internes :

    private static class RunnableBehaviour extends AbstractOneShotBehavior {
        private final Runnable runnable;

        private RunnableBehaviour(final Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        protected void doOneShotAction() {
            runnable.run();
        }
    }

    private class CallableBehaviour extends AbstractOneShotBehavior {
        private final Callable<V> callable;

        private CallableBehaviour(final Callable<V> callable) {
            this.callable = callable;
        }

        @Override
        protected void doOneShotAction() {
            try {
                FutureAgent.this.result = callable.call();
            } catch (final Exception e) {
                LOGGER.error(e.getMessage(), e);
                FutureAgent.this.isInError = true;
                FutureAgent.this.exception = e;
            }
        }
    }

}
