package org.laruche.james.agent.web;

import jade.content.onto.Ontology;
import org.laruche.james.agent.AbstractAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public abstract class AbstractWebAgent extends AbstractAgent {
    public static final String SLASH = "/";
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractWebAgent.class);
    protected final int port;
    protected final String basePath;
    protected Ontology ontology;

    public AbstractWebAgent(final int port, final String basePath) {
        this.port = port;
        this.basePath = basePath;
    }

    @Override
    protected void doSetUp() throws Exception {
        LOGGER.info("==== Démarrage de l'agent Web {}", this.getName());
        initWebServer();
        super.doSetUp();
    }

    protected abstract void initWebServer() throws Exception;

    ///// Private methods :

    protected static String processPathSpec(final String basePath) {
        if (isEmpty(basePath)) {
            return "/*";
        } else if (basePath.startsWith(SLASH)) {
            return basePath.trim() + "/*";
        } else {
            return SLASH + basePath.trim() + "/*";
        }
    }

    ///// Getters & Setters :

    @Override
    protected Ontology getOntology() {
        return ontology;
    }

    public void setOntology(final Ontology ontology) {
        this.ontology = ontology;
    }
}
