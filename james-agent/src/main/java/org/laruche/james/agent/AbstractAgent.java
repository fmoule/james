package org.laruche.james.agent;

import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.Arrays.asList;
import static org.laruche.james.message.MessageUtils.DEFAULT_LANGUAGE;

/**
 * <p>
 * Classe abstraite représentant les agents et les méthodes
 * communes. <br />
 * </p>
 */
public abstract class AbstractAgent extends Agent {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAgent.class);

    private final Set<Behaviour> closeableBehaviours = new HashSet<>();
    private final Set<ServiceDescription> serviceDescriptions = new HashSet<>();

    private transient boolean started = false;
    private transient boolean registred = false;

    ///// Constructeur(s)

    protected AbstractAgent() {
        // EMPTY
    }

    ///// Méthodes de la classe Object :

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getAID());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (this.getClass() != obj.getClass())) {
            return false;
        }
        return (this.getAID().equals(((AbstractAgent) obj).getAID()));
    }


    ///// Méthode de la classe Agent :

    @Override
    protected void setup() {
        try {
            this.getContentManager().registerLanguage(getAgentLanguage());
            this.getContentManager().registerOntology(getOntologyInstance());
            this.doSetUp();
            this.started = true;
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Démarrage de l'agent
     */
    protected void doSetUp() throws Exception {
        if (!serviceDescriptions.isEmpty()) {
            DFService.register(this, this.getAgentDescription());
            this.registred = true;
        }
    }


    @Override
    protected void takeDown() {
        try {
            doTakeDown();
            if (registred) {
                DFService.deregister(this);
                this.registred = false;
            }
            this.started = false;
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Arrêt de l'agent
     */
    protected void doTakeDown() throws Exception {
        for (Behaviour closeableBehaviour : closeableBehaviours) {
            if (closeableBehaviour instanceof AutoCloseable) {
                ((AutoCloseable) closeableBehaviour).close();
            }
        }
    }

    ///// Méthodes abstraites :

    protected abstract Ontology getOntologyInstance();


    protected Codec getAgentLanguage() {
        return DEFAULT_LANGUAGE;
    }

    ///// Méthodes générales :

    @Override
    public void addBehaviour(final Behaviour behaviour) {
        if (behaviour instanceof AutoCloseable) {
            closeableBehaviours.add(behaviour);
        }
        super.addBehaviour(behaviour);
    }

    public void addServiceDescription(final ServiceDescription serviceDescription) {
        serviceDescriptions.add(serviceDescription);
    }

    ///// Getters & Setters /////


    /**
     * Méthode montrant si l'agent est démarré
     *
     * @return démarré ou non
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * <p>
     * Méthode retournant la description de l'agent
     * utilisée par la DF de la plateforme.<br />
     * </p>
     *
     * @return description de l'agent
     */
    public DFAgentDescription getAgentDescription() {
        final DFAgentDescription description = new DFAgentDescription();
        description.setName(this.getAID());
        serviceDescriptions.forEach(description::addServices);
        return description;
    }


    /**
     * Méthode permettant de récupérer les arguments sous la forme
     * de la liste. <br />
     *
     * @return liste des arguments
     */
    protected List<Object> getArgumentsAsList() {
        final Object[] arguments = this.getArguments();
        if (arguments == null || arguments.length == 0) {
            return new ArrayList<>();
        }
        return asList(arguments);
    }

}