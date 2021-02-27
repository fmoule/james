package org.laruche.james.concurrent.beans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laruche.james.test.AbstractAgentTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class FutureAgentTest extends AbstractAgentTestCase<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FutureAgentTest.class);
    private AtomicInteger cursor;

    @BeforeEach
    void setUp() {
        cursor = new AtomicInteger(0);
    }

    ///// Unit tests :

    @Test
    public void shouldExecuteRunnable() throws Exception {
        final FutureAgent<Integer> futureAgent = new FutureAgent<>(() -> {
            try {
                sleep(200);
                cursor.incrementAndGet();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
                fail("Erreur : " + e.getMessage());
            }
        });
        this.agentPlugin.addAgentToStart("runnableAgent", futureAgent);
        this.startAgentPlugin();
        sleep(1000);
        assertThat(futureAgent.isDone()).isTrue();
        assertThat(cursor.intValue()).isEqualTo(1);
    }

    @Test
    public void shouldExecuteCallable() throws Exception {
        final AtomicInteger value = new AtomicInteger(0);
        final FutureAgent<Integer> futureAgent = new FutureAgent<>(() -> value.addAndGet(5));
        this.agentPlugin.addAgentToStart("callableAgent", futureAgent);
        this.startAgentPlugin();
        sleep(1000);
        assertThat(futureAgent.isDone()).isTrue();
        assertThat(futureAgent.get()).isEqualTo(5);
    }

    @Test
    public void shouldExecuteRunnableWithResult() throws Exception {
        final StringBuilder buffer = new StringBuilder();
        final FutureAgent<String> futureAgent = new FutureAgent<>(() -> buffer.append("1"), "OK");
        this.agentPlugin.addAgentToStart("testAgent", futureAgent);
        this.startAgentPlugin();
        sleep(500);
        assertThat(futureAgent.isDone()).isTrue();
        assertThat(futureAgent.get()).isEqualTo("OK");
        assertThat(buffer.toString()).isEqualTo("1");
    }

    @Test
    public void shouldCancel() throws Exception {
        final FutureAgent<Integer> futureAgent = new FutureAgent<>(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    sleep(1000);
                    cursor.incrementAndGet();
                }
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
                fail("Erreur : " + e.getMessage());
            }
        });
        this.agentPlugin.addAgentToStart("runnableAgent", futureAgent);
        this.startAgentPlugin();
        sleep(1000);
        futureAgent.cancel(true);
        sleep(1000);
        assertThat(futureAgent.isCancelled()).isTrue();
        assertThat(cursor.intValue()).isLessThan(10);
    }

}