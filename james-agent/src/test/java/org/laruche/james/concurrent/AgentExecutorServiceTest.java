package org.laruche.james.concurrent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laruche.james.test.AbstractAgentTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class AgentExecutorServiceTest extends AbstractAgentTestCase<String> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AgentExecutorServiceTest.class);
    private AgentExecutorService executorService;

    @BeforeEach
    void setUp() {
        this.executorService = new AgentExecutorService(this.agentPlugin);
    }

    ///// Unit tests :

    @Test
    public void shouldDivideLongValues() throws Exception {
        assertThat(300L / 100L).isEqualTo(3L);
        assertThat(320L / 100L).isEqualTo(3L);
    }

    @Test
    public void shouldExecuteARunnable() throws Exception {
        final StringBuilder buffer = new StringBuilder();
        final Future<?> future = this.executorService.submit(() -> {
            for (int i = 0; i < 5; i++) {
                buffer.append(i);
            }
        });
        this.startAgentPlugin();
        sleep(500);
        assertThat(future.isDone()).isTrue();
        assertThat(buffer.toString()).isEqualTo("01234");
    }

    @Test
    public void shouldExecuteACallable() throws Exception {
        this.startAgentPlugin();
        final Future<String> future = this.executorService.submit(() -> {
            final StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < 7; i++) {
                buffer.append(i);
            }
            return buffer.toString();
        });
        sleep(700);
        assertThat(future.isDone()).isTrue();
        assertThat(future.get()).isEqualTo("0123456");
    }

    @Test
    public void shouldShutdown() throws Exception {
        final StringBuffer buffer = new StringBuffer("");
        executorService.submit(() -> {
            try {
                Thread.sleep(10000);
                buffer.append("task1");
            } catch (final InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
                fail(e.getMessage());
            }
        });
        executorService.submit(() -> {
            try {
                Thread.sleep(12000);
                buffer.append("task2");
            } catch (final InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
                fail(e.getMessage());
            }
        });
        this.startAgentPlugin();
        this.executorService.shutdown();
        Thread.sleep(1500);
        assertThat(executorService.isShutdown()).isTrue();
        Thread.sleep(3500);
        assertThat(buffer.toString()).isEmpty();
    }



}