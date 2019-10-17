package org.laruche.james.plugin.manager;

import org.laruche.james.plugin.AbstractPlugin;
import org.laruche.james.plugin.Plugin;
import org.laruche.james.plugin.utils.BeanEqualsPredicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * <p>
 * Gestionnaire des plugins
 * </p>
 */
public class PluginManager extends AbstractPlugin {
    private final Set<Plugin> plugins = new HashSet<>();

    public PluginManager(final String id) {
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

    public void addPlugin(final Plugin plugin) {
        this.plugins.add(plugin);
    }

    public void removePlugin(final Plugin plugin) throws Exception {
        final Plugin foundPlugin = this.findPlugin(new BeanEqualsPredicate<>(plugin));
        if (foundPlugin == null) {
            return;
        }
        if (foundPlugin.isStarted()) {
            foundPlugin.stop();
        }
        this.plugins.remove(foundPlugin);
    }


    public Plugin findPlugin(final Predicate<Plugin> predicate) {
        if (predicate == null) {
            return null;
        }
        return this.plugins.stream().filter(predicate).findFirst().orElse(null);
    }

    public Collection<Plugin> findPlugins(final Predicate<Plugin> predicate) {
        if (predicate == null) {
            return new ArrayList<>();
        }
        return this.plugins.stream().filter(predicate).collect(toList());
    }

    public Plugin findPlugin(final Class<? extends Plugin> pluginClass) {
        if (pluginClass == null) {
            return null;
        }
        return this.findPlugin(new ClassPredicate(pluginClass));
    }

    public Plugin findPlugin(final String pluginId) {
        if (isEmpty(pluginId)) {
            return null;
        }
        return this.findPlugin(new PluginIdPredicate(pluginId));
    }

    ///// Classes Internes :

    /**
     * <p>
     * Prédicat testant la classe de l'objet. <br />
     * </p>
     */
    private static class ClassPredicate implements Predicate<Plugin> {
        private final Class<? extends Plugin> initClass;

        ClassPredicate(final Class<? extends Plugin> pluginClass) {
            this.initClass = pluginClass;
        }

        @Override
        public boolean test(final Plugin plugin) {
            if (plugin == null) {
                return false;
            }
            return initClass.equals(plugin.getClass());
        }
    }

    private static class PluginIdPredicate implements Predicate<Plugin> {
        private final String pluginId;

        PluginIdPredicate(final String pluginId) {
            this.pluginId = pluginId;
        }

        @Override
        public boolean test(final Plugin plugin) {
            if (plugin == null) {
                return false;
            }
            return pluginId.equals(plugin.getId());
        }
    }
}