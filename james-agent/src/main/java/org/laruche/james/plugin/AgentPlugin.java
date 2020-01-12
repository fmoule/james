package org.laruche.james.plugin;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static jade.core.Profile.*;
import static java.lang.Thread.sleep;
import static java.util.Arrays.asList;

public class AgentPlugin extends AbstractPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentPlugin.class);
    private static final Object[] EMPTY_ARGS = {};
    private final Map<String, AgentParameter> agentsToStartByClass = new TreeMap<>();
    private final Map<String, Agent> agentsToStart = new HashMap<>();
    private transient AgentContainer mainContainer;

    public AgentPlugin(final String platformId) {
        super(platformId);
    }

    ///// Implémentation de l'interface Plugin :

    @Override
    protected void doStop() throws Exception {
        try {
            if (mainContainer != null) {
                this.mainContainer.kill();
                sleep(1000);
            }
        } catch (final Exception controllerException) {
            LOGGER.error(controllerException.getMessage(), controllerException);
            throw new Exception(controllerException.getMessage(), controllerException);
        }
    }

    @Override
    protected void doStart() throws Exception {
        mainContainer = Runtime.instance().createMainContainer(createJadeProfile());
        try {
            mainContainer.start();
            for (String agentName : agentsToStart.keySet()) {
                mainContainer.acceptNewAgent(agentName, agentsToStart.get(agentName)).start();
            }
        } catch (final ControllerException controllerException) {
            LOGGER.error(controllerException.getMessage(), controllerException);
            throw new Exception(controllerException.getMessage(), controllerException);
        }
    }


    ///// Méthodes générales :

    private Profile createJadeProfile() {
        final Profile profile = new ProfileImpl();
        profile.setParameter(MAIN, "true");
        if (!agentsToStartByClass.isEmpty()) {
            final StringBuilder buffer = new StringBuilder();
            int count = 0;
            for (String agentName : agentsToStartByClass.keySet()) {
                buffer.append((count > 0 ? ";" : ""));
                buffer.append(agentName);
                buffer.append(":");
                final AgentParameter agentParameter = agentsToStartByClass.get(agentName);
                buffer.append(agentParameter.getAgentClass().getCanonicalName());
                final List<Object> arguments = agentParameter.getAgentParameters();
                if (!arguments.isEmpty()) {
                    buffer.append("(");
                    final StringBuilder argBuffer = new StringBuilder();
                    int cursor = 0;
                    for (Object argument : arguments) {
                        argBuffer.append((cursor == 0 ? argument : "," + argument));
                        cursor++;
                    }
                    buffer.append(argBuffer.toString());
                    buffer.append(")");
                }
                count++;
            }
            profile.setParameter(AGENTS, buffer.toString());
            profile.setParameter(PLATFORM_ID, this.getId());
        }
        return profile;
    }

    ///// Méthodes de gestion des agents :

    public void addAgentToStart(final String name, final Class<? extends Agent> agentClazz) {
        addAgentToStart(name, agentClazz, EMPTY_ARGS);
    }

    public void addAgentToStart(final String name, final Class<? extends Agent> agentClazz, final Object[] args) {
        try {
            if (mainContainer != null) {
                this.mainContainer
                        .createNewAgent(name, agentClazz.getName(), args)
                        .start();
            } else {
                this.agentsToStartByClass.put(name, new AgentParameter(agentClazz, args));
            }
        } catch (final Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }

    /**
     * Ajoute une instance d'agent dans le plugin;
     *
     * @param name  : nom de l'agent
     * @param agent : instance de l'agent
     */
    public void addAgentToStart(final String name, final Agent agent) {
        try {
            if (mainContainer != null) {
                this.mainContainer
                        .acceptNewAgent(name, agent)
                        .start();
            } else {
                this.agentsToStart.put(name, agent);
            }
        } catch (final Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }

    public AgentController getAgentController(final String agentGUID) {
        try {
            return this.mainContainer.getAgent(agentGUID);
        } catch (final ControllerException controllerException) {
            return null;
        }
    }

    public void clearAgents() {
        this.agentsToStart.clear();
        this.agentsToStartByClass.clear();
    }

    ///// Classes Internes :

    private static class AgentParameter {
        private final Class<? extends Agent> agentClass;
        private final Object[] agentParameters;

        private AgentParameter(final Class<? extends Agent> agentClass, final Object[] agentParameters) {
            this.agentClass = agentClass;
            this.agentParameters = agentParameters;
        }

        ///// Méthode de la classe Object :

        @Override
        public int hashCode() {
            final int prime = 17;
            int code = Objects.hashCode(agentClass);
            code = (prime * code) + Arrays.hashCode(agentParameters);
            return code;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if ((obj == null) || (obj.getClass() != AgentParameter.class)) {
                return false;
            }
            final AgentParameter that = (AgentParameter) obj;
            boolean areEquals = Objects.equals(agentClass, that.agentClass);
            areEquals = areEquals && Arrays.equals(agentParameters, that.agentParameters);
            return areEquals;
        }

        ///// Getters & Setters :


        public Class<? extends Agent> getAgentClass() {
            return agentClass;
        }

        public List<Object> getAgentParameters() {
            if (agentParameters == null || agentParameters.length == 0) {
                return new ArrayList<>();
            }
            return asList(agentParameters);
        }
    }
}