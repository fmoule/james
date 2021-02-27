package org.laruche.james.concurrent;

import jade.core.AID;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.laruche.james.concurrent.beans.FutureAgent;
import org.laruche.james.plugin.AgentPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * <p>
 * Implémentation de l'interface ExecutorService utilisant l'architecture la plateforme JADE. <br />
 * </p>
 *
 * @see java.util.concurrent.ExecutorService
 */
public class AgentExecutorService implements ExecutorService, Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentExecutorService.class);
    private static final List<Runnable> EMPTY_RUNNABLE_LIST = new ArrayList<>();
    private final AgentPlugin agentPlugin;
    private final Set<FutureAgent<?>> futureAgents;
    private boolean isShutdown = false;

    public AgentExecutorService(final AgentPlugin agentPlugin) {
        this.agentPlugin = agentPlugin;
        this.futureAgents = new HashSet<>();
    }

    @Override
    public void shutdown() {
        AgentController agentController;
        AID aid;
        for (FutureAgent<?> futureAgent : futureAgents) {
            aid = futureAgent.getAID();
            if (aid == null) {
                continue;
            }
            agentController = this.agentPlugin.getAgentController(aid.getName(), true);
            try {
                LOGGER.debug("Arrêt de l'agent " + aid.getName());
                agentController.kill();
            } catch (final StaleProxyException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        this.isShutdown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        this.shutdown();
        return EMPTY_RUNNABLE_LIST;
    }

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    public boolean isTerminated() {
        for (FutureAgent<?> futureAgent : futureAgents) {
            if (!futureAgent.isDone()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("A Implémenter !!");
    }

    protected <T> FutureAgent<T> submitFutureAgent(final String agentName, final FutureAgent<T> agent) {
        this.agentPlugin.addAgentToStart(agentName, agent);
        return agent;
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return submitFutureAgent("task-" + task.toString(), new FutureAgent<T>(task));
    }

    @Override
    public <T> Future<T> submit(final Runnable task, final T result) {
        return this.submitFutureAgent("task-" + task.toString() + "-" + result.toString(),
                new FutureAgent<T>(task, result));
    }

    @Override
    public Future<?> submit(final Runnable task) {
        return this.submitFutureAgent("task-" + task.toString(),
                new FutureAgent<>(task));
    }

    @Override
    public void execute(final Runnable command) {
        this.submitFutureAgent("task-" + command.toString(),
                new FutureAgent<>(command));
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) {
        final ArrayList<Future<T>> futures = new ArrayList<>();
        for (Callable<T> callable : tasks) {
            if (callable == null) {
                continue;
            }
            futures.add(this.submitFutureAgent("task-" + callable.toString(), new FutureAgent<T>(callable)));
        }
        return futures;
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks,
                                         final long timeout,
                                         final TimeUnit unit) throws InterruptedException {
        final List<Future<T>> futures = this.invokeAll(tasks);
        sleep(unit.convert(timeout, MILLISECONDS));
        return futures;
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        for (Future<T> future : this.invokeAll(tasks)) {
            if (future.isDone()) {
                return future.get();
            }
        }
        throw new ExecutionException("No tasks finished", null);
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks,
                           final long timeout,
                           final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        final long timeOut = unit.convert(timeout, MILLISECONDS);
        final long waitingTime = timeOut / (tasks.size());
        final long additionnalTime = timeOut - (waitingTime * tasks.size());
        int cursor = 0;
        for (Future<T> future : this.invokeAll(tasks)) {
            if (cursor == 0) {
                sleep(waitingTime + additionnalTime);
            } else {
                sleep(waitingTime);
            }
            if (future.isDone()) {
                return future.get();
            }
            cursor++;
        }
        throw new TimeoutException("Time out :an y of the tasks succeeded");
    }

    @Override
    public void close() {
        if (!this.isShutdown) {
            this.shutdown();
        }
        this.futureAgents.clear();
    }
}
