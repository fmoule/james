package org.laruche.james.plugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <p>
 * Classe abstraite représentant un modèle pour tous les plugins utilisés par la plateforme. <br />
 * </p>
 */
public abstract class AbstractPlugin implements Plugin {
    private boolean isStarted = false;
    private final String id;
    private final Set<Class<? extends Plugin>> pluginDependencies = new HashSet<>();
    private transient PluginManager pluginManager;

    protected AbstractPlugin(final String id) {
        this.id = id;
    }

    /// Méthodes de la classe Object :

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (this.getClass() != obj.getClass())) {
            return false;
        }
        return Objects.equals(this.id, ((AbstractPlugin) obj).id);
    }

    /// Méthodes de l'interface Plugin :

    @Override
    public void start() throws Exception {
        this.doStart();
        this.isStarted = true;
    }

    protected abstract void doStart() throws Exception;

    @Override
    public void stop() throws Exception {
        this.doStop();
        this.isStarted = false;
    }

    protected abstract void doStop() throws Exception;

    ///// Méthodes générales :

    public void addPluginDependency(final Class<? extends Plugin> pluginClass) {
        if (pluginClass == null) {
            return;
        }
        this.pluginDependencies.add(pluginClass);
    }

    ///// Getters & Setters :

    @Override
    public boolean isStarted() {
        return this.isStarted;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Collection<Class<? extends Plugin>> getPluginDependencies() {
        return pluginDependencies;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public void setPluginManager(final PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }
}