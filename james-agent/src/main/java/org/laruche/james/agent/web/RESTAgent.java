package org.laruche.james.agent.web;

import jade.content.AgentAction;
import jade.content.lang.Codec;
import jade.content.onto.BeanOntology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.ApplicationPath;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static jakarta.servlet.DispatcherType.REQUEST;
import static java.util.Arrays.asList;
import static java.util.EnumSet.of;
import static org.laruche.james.message.MessageUtils.createMessage;

/**
 * <p>
 * Agent permettant d'exposer et/ou de se connecter à un protocol web. <br />
 * De façon plus précise, cet agent gère en interne un serveur web permettant
 * d'exposer une API REST.<br />
 * </p>
 */
public class RESTAgent extends AbstractWebAgent {
    public static final String CORS_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String CORS_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private transient Server webServer;
    private WebAgentResourceConfig resourceConfig;
    private final Set<String> corsUrls;
    private final Set<String> corsHeaders;

    ///// Constructeurs :

    public RESTAgent(final int port, final String basePath) {
        super(port, basePath);
        this.webServer = null;
        this.resourceConfig = null;
        this.corsUrls = new HashSet<>();
        this.corsHeaders = new HashSet<>();
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
        final ServletContextHandler jettyContextHandler = new ServletContextHandler();
        jettyContextHandler.addServlet(new ServletHolder(new ServletContainer(resourceConfig)), processPathSpec(basePath));
        jettyContextHandler.addFilter(new FilterHolder(new CorsFilter(corsUrls, corsHeaders)), "/*", of(REQUEST));
        webServer.setHandler(jettyContextHandler);
        this.webServer.start();
    }

    ///// Méthodes générales :

    public void registerSimpleResource(final AbstractWebAgentResource webAgentResource) {
        if (this.resourceConfig == null) {
            this.resourceConfig = new WebAgentResourceConfig(this);
        }
        this.resourceConfig.registerResource(webAgentResource);
    }

    public void addCORSUrls(final String... urls) {
        this.corsUrls.addAll(asList(urls));
    }

    public void addCORSHeaders(final String... headers) {
        this.corsHeaders.addAll(asList(headers));
    }

    ///// Getters & Setters :

    public void setCORSUrls(final Collection<String> urls) {
        this.corsUrls.clear();
        this.corsUrls.addAll(urls);
    }

    public void setCORSHeaders(final Collection<String> headers) {
        this.corsHeaders.clear();
        this.corsHeaders.addAll(headers);
    }

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

    private static class CorsFilter extends HttpFilter {
        private final Set<String> corsOrigins;
        private final Set<String> corsHeaders;

        public CorsFilter(final Set<String> corUrls, final Set<String> corsHeaders) {
            this.corsOrigins = corUrls;
            this.corsHeaders = corsHeaders;
        }

        @Override
        protected void doFilter(final HttpServletRequest req,
                                final HttpServletResponse res,
                                final FilterChain chain) throws IOException, ServletException {
            if (this.corsOrigins != null
                    && !this.corsOrigins.isEmpty()) {
                res.setHeader(CORS_ALLOW_ORIGIN, getCORSUrlsValue(corsOrigins));
            }
            if (this.corsHeaders != null && !this.corsHeaders.isEmpty()) {
                res.setHeader(CORS_ALLOW_HEADERS, getCORSUrlsValue(corsHeaders));
            }
            super.doFilter(req, res, chain);
        }

        private static String getCORSUrlsValue(final Collection<String> corsValue) {
            int cursor = 0;
            final StringBuilder buffer = new StringBuilder();
            for (String corsUrl : new TreeSet<>(corsValue)) {
                if (cursor > 0) {
                    buffer.append(",");
                }
                buffer.append(corsUrl);
                cursor++;
            }
            return buffer.toString();
        }
    }
}
