package org.laruche.james.agent.web;

import jade.content.AgentAction;
import jade.content.lang.Codec;
import jade.content.onto.BeanOntology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.ApplicationPath;

import java.util.Set;

import static org.laruche.james.message.MessageUtils.createMessage;

/**
 * <p>
 * Agent permettant d'exposer et/ou de se connecter à un protocol web. <br />
 * De façon plus précise, cet agent gère en interne un serveur web permettant
 * d'exposer une API REST.<br />
 * </p>
 */
public class RESTAgent extends AbstractWebAgent {
    private transient Server webServer;
    private WebAgentResourceConfig resourceConfig;

    ///// Constructeurs :

    public RESTAgent(final int port, final String basePath) {
        super(port, basePath);
    }

    ///// Initialisation & Arret :

    @Override
    protected void doTakeDown() throws Exception {
        LOGGER.info("==== Arret de l'agent Web {}", this.getName());
        for (Object resource : this.resourceConfig.getInstances()) {
            if (resource instanceof AutoCloseable) {
                ((AutoCloseable) resource).close();
            }
        }
        if (!this.webServer.isStopped()) {
            this.webServer.stop();
        }
        super.doTakeDown();
    }

    ///// Méthodes privées :

    @Override
    protected void initWebServer() throws Exception {
        this.webServer = new Server(port);
        if (resourceConfig == null) {
            resourceConfig = new WebAgentResourceConfig(this);
        }
        final ServletContextHandler jettyHandler = new ServletContextHandler();
        jettyHandler.addServlet(new ServletHolder(new ServletContainer(resourceConfig)), processPathSpec(basePath));
        webServer.setHandler(jettyHandler);
        this.webServer.start();
    }

    ///// Méthodes générales :

    public void registerSimpleResource(final AbstractWebAgentResource webAgentResource) {
        if (this.resourceConfig == null) {
            this.resourceConfig = new WebAgentResourceConfig(this);
        }
        this.resourceConfig.registerResource(webAgentResource);
    }

    ///// Getters & Setters :

    ///////////////////////
    ///// Classes privées :
    ///////////////////////

    @ApplicationPath("/")
    private static class WebAgentResourceConfig extends ResourceConfig {
        private final RESTAgent RESTAgent;

        private WebAgentResourceConfig(final RESTAgent RESTAgent) {
            this.RESTAgent = RESTAgent;
        }

        void registerResource(final AbstractWebAgentResource webAgentResource) {
            webAgentResource.setRESTAgent(RESTAgent);
            this.registerInstances(webAgentResource);
        }
    }


    /**
     * <p>
     * Classe abstraite représentant les ressources Jersey susceptibles d'etre gérées
     * par les agents de type WebAgent
     * </p>
     *
     * @see RESTAgent
     */
    public abstract static class AbstractWebAgentResource {
        private RESTAgent RESTAgent;

        public RESTAgent getRESTAgent() {
            return RESTAgent;
        }

        public void setRESTAgent(final RESTAgent RESTAgent) {
            this.RESTAgent = RESTAgent;
        }

        protected void sendMessage(final AID receiverAID,
                                   final int performative,
                                   final BeanOntology ontology,
                                   final AgentAction agentAction)
                throws Codec.CodecException, OntologyException {
            final RESTAgent RESTAgent = this.getRESTAgent();
            RESTAgent.send(createMessage(RESTAgent.getContentManager(),
                    RESTAgent.getAID(),
                    receiverAID,
                    performative,
                    ontology,
                    agentAction));
        }
    }
}
