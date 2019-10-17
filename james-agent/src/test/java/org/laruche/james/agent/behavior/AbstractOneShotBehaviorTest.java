package org.laruche.james.agent.behavior;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laruche.james.test.AbstractAgentTestCase;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public class AbstractOneShotBehaviorTest extends AbstractAgentTestCase<Integer> {

    @BeforeEach
    void setUp() {
        this.getTestResult().setValue(0);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    void shouldExecuteOneTime() throws Exception {
        assertThat(this.getTestResult()).isNotNull();
        assertThat(this.getTestResult().getValue()).isEqualTo(0);
        this.testManagerAgent.addBehaviour(new TestBehavior(this.getTestResult()));
        agentPlugin.addAgentToStart("testAgent", this.testManagerAgent);
        agentPlugin.start();
        sleep(1000);
        assertThat(this.getTestResult().getValue()).isEqualTo(1);
        sleep(1000);
        assertThat(this.getTestResult().getValue()).isEqualTo(1);
    }


    ///// Classes Internes :

    private static class TestBehavior extends AbstractOneShotBehavior {
        private final TestResult<Integer> testResult;

        private TestBehavior(final TestResult<Integer> testResult) {
            this.testResult = testResult;
        }

        @Override
        protected void doOneShotAction() {
            testResult.setValue(testResult.getValue() + 1);
        }
    }
}