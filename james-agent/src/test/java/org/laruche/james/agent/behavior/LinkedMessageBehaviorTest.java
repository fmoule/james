package org.laruche.james.agent.behavior;

import jade.lang.acl.ACLMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.laruche.james.test.AbstractAgentTestCase;

import java.util.Objects;
import java.util.function.Predicate;

class LinkedMessageBehaviorTest extends AbstractAgentTestCase<String> {
    private LinkedMessageBehavior behavior;

    @BeforeEach
    void setUp() {
        behavior = new LinkedMessageBehavior();
        this.testManagerAgent.addBehaviour(behavior);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    ///// Tests unitaires :

    @Test
    void shouldProceed() throws Exception {
        behavior.addBehavior(new MessageContentPredicate("content1"), null);
    }


    private static class MessageContentPredicate implements Predicate<ACLMessage> {
        private final String messageContent;

        private MessageContentPredicate(final String messageContent) {
            this.messageContent = messageContent;
        }

        ///// Méthode de Predicate :

        @Override
        public boolean test(final ACLMessage message) {
            return this.messageContent.equals(message.getContent());
        }

        ///// Méthodes de la classe Object :


        @Override
        public int hashCode() {
            return Objects.hashCode(messageContent);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if ((obj == null) || (this.getClass() != obj.getClass())) {
                return false;
            }
            return Objects.equals(this.messageContent, ((MessageContentPredicate) obj).messageContent);
        }
    }
}