package org.laruche.james.concurrent.ontology;

import jade.content.AgentAction;
import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;
import jade.content.onto.annotations.SuppressSlot;

import java.io.*;

import static org.apache.commons.io.IOUtils.closeQuietly;

/**
 * <p>
 * Ontologie utilisée par les agents concernant la concurrence. <br />
 *
 * @see BeanOntology
 * </p>
 */
public class ConcurrentOntology extends BeanOntology {
    public static final ConcurrentOntology CONCURRENT_ONTOLOGY;

    static {
        try {
            CONCURRENT_ONTOLOGY = new ConcurrentOntology();
        } catch (final BeanOntologyException beanOntologyException) {
            throw new RuntimeException("Impossible de créer l'ontologie ConcurrentOntology", beanOntologyException);
        }
    }

    private ConcurrentOntology() throws BeanOntologyException {
        super("concurrentOnlogy");
        this.add(ReturnValueAction.class);
    }

    ///// Méthodes privées et/ou statiques :

    static byte[] serialize(final Object obj) {
        if (obj == null) {
            return new byte[0];
        }
        ByteArrayOutputStream out = null;
        ObjectOutputStream os = null;
        try {
            out = new ByteArrayOutputStream();
            os = new ObjectOutputStream(out);
            os.writeObject(obj);
            return out.toByteArray();
        } catch (final IOException e) {
            throw new RuntimeException("Impossible de sérialiser l'objet : " + e.getMessage());
        } finally {
            closeQuietly(os, ioException -> {
                throw new RuntimeException("Impossible de sérialiser l'objet : " + ioException);
            });
            closeQuietly(out, ioException -> {
                throw new RuntimeException("Impossible de sérialiser l'objet : " + ioException);
            });
        }
    }

    static <T> T deserialize(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayInputStream in = null;
        ObjectInputStream is = null;
        try {
            in = new ByteArrayInputStream(bytes);
            is = new ObjectInputStream(in);
            //noinspection unchecked
            return (T) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Impossible de désérialiser :" + e.getMessage());
        } finally {
            closeQuietly(is, ioException -> {
                throw new RuntimeException("Impossible de désérialiser :" + ioException.getMessage());
            });
            closeQuietly(in, ioException -> {
                throw new RuntimeException("Impossible de désérialiser :" + ioException.getMessage());
            });
        }
    }

    ///// Définition des actions de l'ontologie :

    /**
     * <p>
     * Action de l'ontologie permettant de retourner la valeur d'une action
     * exécutée, par exemple, par une instance de Callable
     * </p>
     *
     * @see java.util.concurrent.Callable
     */
    @Element(name = "returnValue")
    public static class ReturnValueAction<T> implements AgentAction {
        private byte[] returnedValue;

        @SuppressWarnings("unused")
        public ReturnValueAction() {
            this.returnedValue = new byte[0];
        }

        public ReturnValueAction(final T returnedValue) {
            this.setReturnedValue(returnedValue);
        }

        @Slot(name = "value")
        public byte[] getReturnedValueAsBytes() {
            return this.returnedValue;
        }

        public void setReturnedValue(final byte[] bytes) {
            this.returnedValue = bytes;
        }

        @SuppressSlot
        public T getReturnedValue() {
            return deserialize(returnedValue);
        }

        public void setReturnedValue(final T returnedValue) {
            this.returnedValue = serialize(returnedValue);
        }
    }

}
