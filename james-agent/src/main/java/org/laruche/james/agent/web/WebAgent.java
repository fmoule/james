package org.laruche.james.agent.web;

import jade.content.onto.Ontology;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.laruche.james.agent.AbstractAgent;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * <p>
 * Agent se comportant en tant que serveur WEB. <br />
 * </p>
 */
public class WebAgent extends AbstractAgent {
    private Server webServer;
    private List<Handler> handlers = new ArrayList<>();
    private Ontology ontology;

    public WebAgent(final Ontology ontology) {
        super();
        this.ontology = ontology;
        this.addHandler(new HelloHandler());
    }

    ///// Initialisation & Arret

    @Override
    protected void doSetUp() throws Exception {
        this.webServer = new Server(8080);
        this.webServer.setHandler(getHandlers());
        this.webServer.start();
        this.webServer.join();
        super.doSetUp();
    }

    @Override
    protected void doTakeDown() throws Exception {
        if (!this.webServer.isStopped()) {
            this.webServer.stop();
        }
        super.doTakeDown();
    }

    @Override
    protected Ontology getOntologyInstance() {
        return ontology;
    }

    ///// Méthodes générales :

    public void addHandler(final Handler handler) {
        this.handlers.add(handler);
    }

    private HandlerList getHandlers() {
        final HandlerList handlers = new HandlerList();
        for (Handler handler : this.handlers) {
            handlers.addHandler(handler);
        }
        return handlers;
    }

    ///// Classes privées :

    private static class HelloHandler extends AbstractHandler {

        @Override
        public void handle(final String target,
                           final Request baseRequest,
                           final HttpServletRequest request,
                           final HttpServletResponse response) throws IOException, ServletException {
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(SC_OK);
            response.getWriter().println("<h1>Test OK !!</h1>");
            baseRequest.setHandled(true);
        }
    }
}
