package org.laruche.james.agent.web;

import jade.content.onto.Ontology;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.Resource.Builder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.laruche.james.agent.AbstractAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * <p>
 * Agent permettant d'exposer et/ou de se connecter à un protocol web. <br />
 * De façon plus précise, cet agent gère en interne un serveur web permettant :
 * <ul>
 *     <li>d'exposer un site web</li>
 *     <li>d'exposer une API Rest</li>
 * </ul>
 * </p>
 */
public class WebAgent extends AbstractAgent {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebAgent.class);
    private transient Server webServer;
    private Ontology ontology;
    private final int port;
    private final String basePath;
    private WebAgentResourceConfig resourceConfig;

    public WebAgent(final int port, final String basePath) {
        super();
        this.port = port;
        this.basePath = basePath;
    }

    ///// Initialisation & Arret

    @Override
    protected void doSetUp() throws Exception {
        LOGGER.info("==== Démarrage de l'agent Web {}", this.getName());
        initWebServer();
        this.webServer.start();
        super.doSetUp();
    }

    private void initWebServer() {
        this.webServer = new Server(port);
        if (resourceConfig == null) {
            resourceConfig = new WebAgentResourceConfig();
        }
        final ServletContextHandler jettyHandler = new ServletContextHandler();
        jettyHandler.addServlet(new ServletHolder(new ServletContainer(resourceConfig)), getPathSpec());
        webServer.setHandler(jettyHandler);
    }

    @Override
    protected void doTakeDown() throws Exception {
        LOGGER.info("==== Arret de l'agent Web {}", this.getName());
        if (!this.webServer.isStopped()) {
            this.webServer.stop();
        }
        super.doTakeDown();
    }

    ///// Méthodes générales :

    public void registerResource(final String relativePath, final Class<?> resourceClass) {
        if (this.resourceConfig == null) {
            this.resourceConfig = new WebAgentResourceConfig();
        }
        this.resourceConfig.registerResource(relativePath, resourceClass);
    }

    ///// Getters & Setters :

    private String getPathSpec() {
        if (isEmpty(basePath)) {
            return "/*";
        } else {
            return "/" + basePath + "/*";
        }
    }

    @Override
    protected Ontology getOntologyInstance() {
        return ontology;
    }

    ///// Classes privées :

    @ApplicationPath("/")
    private static class WebAgentResourceConfig extends ResourceConfig {

        void registerResource(final String relativePath, final Class<?> resourceClass) {
            final Builder builder = Resource.builder(resourceClass);
            builder.path(relativePath);
            this.registerResources(builder.build());
        }

    }

}
