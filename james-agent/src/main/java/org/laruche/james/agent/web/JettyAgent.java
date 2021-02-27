package org.laruche.james.agent.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URL;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * <p>
 * Agent permettant d'encapsuler un serveur Jetty et, donc,
 * d'exposer un front web. <br />
 * </p>
 */
public class JettyAgent extends AbstractWebAgent {
    private String path;
    private transient Server jettyServer;
    private final JettyAgentMode mode;

    protected JettyAgent(final int port, final String basePath, final String path, JettyAgentMode mode) {
        super(port, basePath);
        this.path = path;
        this.mode = mode;
    }

    @Override
    protected void doTakeDown() throws Exception {
        if (jettyServer != null
                && jettyServer.isStarted()) {
            this.jettyServer.stop();
        }
    }

    ///// private / static methods

    @Override
    protected void initWebServer() throws Exception {
        this.jettyServer = new Server(this.port);
        final WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath(this.basePath);
        webAppContext.setServer(jettyServer);
        if (mode == JettyAgentMode.WEBAPP) {
            setWebAppPath(webAppContext, path);
        } else if (mode == JettyAgentMode.WAR) {
            setWarPath(webAppContext, path);
        }
        jettyServer.setHandler(webAppContext);
        this.jettyServer.start();
    }

    private static void setWebAppPath(final WebAppContext webAppContext, final String path) {
        if (isEmpty(path)) {
            throw new IllegalArgumentException("Le path ne doit pas être vide ou null");
        }
        final URL resource = JettyAgent.class.getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("La ressource " + path + " n'existe pas");
        }
        webAppContext.setDescriptor(path + "/WEB-INF/web.xml");
        webAppContext.setResourceBase(resource.getPath());
    }

    private static void setWarPath(final WebAppContext webAppContext, final String path) {
        if (isEmpty(path)) {
            throw new IllegalArgumentException("Le path ne doit pas être vide ou null");
        }
        final URL warResource = JettyAgent.class.getResource(path);
        if (warResource == null) {
            throw new IllegalArgumentException("La ressource " + path + " n'existe pas");
        }
        webAppContext.setWar(warResource.getPath());
    }

    public static JettyAgent createJettyAgentForWebApp(final String basePath,
                                                       final int port,
                                                       final String webappPath) {
        return new JettyAgent(port, basePath, webappPath, JettyAgentMode.WEBAPP);
    }

    public static JettyAgent createJettyAgentForWar(final String basePath,
                                                    final int port,
                                                    final String warPath) {
        return new JettyAgent(port, basePath, warPath, JettyAgentMode.WAR);
    }

    ///// Getters & Setters :

    public void setPath(final String path) {
        this.path = path;
    }

    ///// Classes internes

    public enum JettyAgentMode {
        WAR,
        WEBAPP
    }

}
