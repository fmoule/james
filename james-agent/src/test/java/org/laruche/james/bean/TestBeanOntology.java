package org.laruche.james.bean;

import jade.content.AgentAction;
import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;
import jade.content.onto.annotations.SuppressSlot;

import java.io.Serializable;
import java.util.function.Predicate;

import static org.apache.commons.lang3.SerializationUtils.deserialize;
import static org.apache.commons.lang3.SerializationUtils.serialize;

public class TestBeanOntology extends BeanOntology {
    private static final String TEST_BEAN_ONTOLOGY_NAME = "personOntology";
    public static final TestBeanOntology TEST_BEAN_ONTOLOGY;

    static {
        try {
            TEST_BEAN_ONTOLOGY = new TestBeanOntology();
        } catch (final BeanOntologyException e) {
            throw new RuntimeException("Impossible de créer l'ontologie pour la classe de test Person", e);
        }
    }

    private TestBeanOntology() throws BeanOntologyException {
        super(TEST_BEAN_ONTOLOGY_NAME);
        this.add(AddTestBeanAction.class);
        this.add(DeleteTestBeanAction.class);
    }

    ///// Classes internes :

    @SuppressWarnings("unused")
    @Element(name = "createTestBean")
    public static class AddTestBeanAction implements AgentAction {
        private byte[] personBytes = new byte[0];

        public AddTestBeanAction() {
            // EMPTY
        }

        public AddTestBeanAction(final String firstName, final String name) {
            this.setTestBean(new TestBean(firstName, name));
        }

        @Slot(name = "person")
        public byte[] getTestBeanBytes() {
            return personBytes;
        }

        public void setTestBeanBytes(final byte[] bytes) {
            this.personBytes = (bytes == null ? new byte[0] : bytes);
        }

        @SuppressSlot
        public TestBean getTestBean() {
            return deserialize(this.personBytes);
        }

        public void setTestBean(final TestBean testBean) {
            this.personBytes = (testBean == null ? new byte[0] : serialize(testBean));
        }
    }

    @SuppressWarnings("unused")
    @Element(name = "deleteTestBean")
    public static class DeleteTestBeanAction implements AgentAction {
        private byte[] predicateBytes = new byte[0];

        @Slot(name = "predicate")
        public byte[] getPredicateAsBytes() {
            return (predicateBytes == null ? new byte[0] : this.predicateBytes);
        }

        public void setPredicateAsBytes(final byte[] predicateBytes) {
            this.predicateBytes = predicateBytes;
        }

        public <T extends Predicate<TestBean> & Serializable> T getPredicate() {
            return deserialize(this.predicateBytes);
        }

        public <T extends Predicate<TestBean> & Serializable> void setPredicate(final T predicate) {
            this.predicateBytes = (predicate == null ? new byte[0] : serialize(predicate));
        }
    }

}
