package org.laruche.james.plugin.config;

import org.laruche.james.plugin.AbstractPlugin;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * <p>
 * Plugin type permettant de gérer et d'importer un fichier de configuration.<br />
 * De façon plus précise, ce plugin importe le fichier de configuration voulu et expose
 * les propriétés. <br />
 * </p>
 *
 * @see Properties
 */
public class ConfigPlugin extends AbstractPlugin {
    private transient Properties confProperties = new Properties();
    private final String confFilePath;

    public ConfigPlugin(final String id, final String confFilePath) {
        super(id);
        this.confFilePath = confFilePath;
    }

    ///// Méthode

    @Override
    protected void doStart() throws Exception {
        if (isEmpty(confFilePath)) {
            throw new Exception("Le fichier de configuration ne doit pas être nul ou vide");
        }
        final File confFile = new File(confFilePath);
        if (!confFile.exists()) {
            throw new Exception("Le fichier de configuration " + confFilePath + " n'existe pas");
        }
        this.confProperties.load(new FileReader(confFile));
    }

    @Override
    protected void doStop() {
        this.confProperties.clear();
    }

    ///// Getters & Setters :

    public String getProperty(final String propName) {
        if (isEmpty(propName)) {
            return null;
        }
        return this.confProperties.getProperty(propName);
    }

    public int getNbProperties() {
        return this.confProperties.size();
    }

}
