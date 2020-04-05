package org.laruche.james.plugin.manager;

import org.laruche.james.plugin.AbstractPlugin;
import org.laruche.james.plugin.Plugin;
import org.laruche.james.plugin.PluginManager;
import org.laruche.james.plugin.utils.BeanEqualsPredicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

/**
 * <p>
 * Gestionnaire des plugins
 * </p>
 */
public class DefaultPluginManager extends AbstractPlugin implements PluginManager {
    private final Set<Plugin> plugins = new HashSet<>();

    public DefaultPluginManager(final String id) {
        super(id);
    }

    /// Méthodes de l'interface Plugin :

    @Override
    protected void doStart() throws Exception {
        for (Plugin plugin : plugins) {
            if (!areDependenciesStarted(plugin.getPluginDependencies())) {
                continue;
            }
            plugin.start();
        }
    }

    private boolean areDependenciesStarted(final Collection<Class<? extends Plugin>> pluginDependencies) {
        if (pluginDependencies == null) {
            return true;
        }
        boolean areStarted = true;
        boolean started;
        for (Class<? extends Plugin> pluginClass : pluginDependencies) {
            started = false;
            for (Plugin plugin : this.findPlugins(new ClassPredicate(pluginClass))) {
                started = started || (plugin.isStarted());
            }
            areStarted = areStarted && started;
        }
        return areStarted;
    }

    @Override
    protected void doStop() throws Exception {
        for (Plugin plugin : plugins) {
            plugin.stop();
        }
    }

    /// Méthodes générales :

    @Override
    public void addPlugin(final Plugin plugin) {
        if (plugin instanceof AbstractPlugin) {
            ((AbstractPlugin) plugin).setPluginManager(this);
        }
        this.plugins.add(plugin);
    }

    @Override
    public void removePlugin(final Plugin plugin) throws Exception {
        final Plugin foundPlugin = this.findFirstPlugin(new BeanEqualsPredicate<>(plugin));
        if (foundPlugin == null) {
            return;
        }
        if (foundPlugin.isStarted()) {
            foundPlugin.stop();
        }
        this.plugins.remove(foundPlugin);
    }

    @Override
    public Collection<Plugin> findPlugins(final Predicate<Plugin> predicate) {
        if (predicate == null) {
            return new ArrayList<>();
        }
        return this.plugins.stream().filter(predicate).collect(toList());
    }

}