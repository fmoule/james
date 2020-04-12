package org.laruche.james.plugin.manager;

import org.laruche.james.plugin.AbstractPlugin;
import org.laruche.james.plugin.Plugin;
import org.laruche.james.plugin.PluginManager;
import org.laruche.james.plugin.utils.BeanEqualsPredicate;

import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

/**
 * <p>
 * Gestionnaire des plugins
 * </p>
 */
public class DefaultPluginManager extends AbstractPlugin implements PluginManager {
    private final Set<Plugin> plugins = new TreeSet<>(new PluginComparator());

    public DefaultPluginManager(final String id) {
        super(id);
    }

    /// Méthodes privées

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

    private static String buildExceptionMessage(final Collection<Class<? extends Plugin>> classes) {
        final StringBuilder buffer = new StringBuilder("Un des plugins {");
        int count = 0;
        for (Class<? extends Plugin> clazz : classes) {
            if (count > 0) {
                buffer.append(",");
            }
            buffer.append(clazz.getSimpleName());
            count++;
        }
        buffer.append("} n'est pas démarré");
        return buffer.toString();
    }

    /// Méthodes de l'interface Plugin :

    @Override
    protected void doStart() throws Exception {
        Collection<Class<? extends Plugin>> dependencies;
        for (Plugin plugin : plugins) {
            dependencies = plugin.getPluginDependencies();
            if (!areDependenciesStarted(dependencies)) {
                throw new Exception(buildExceptionMessage(dependencies));
            }
            plugin.start();
        }
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

    ///// Classes Internes :

    private static class PluginComparator implements Comparator<Plugin> {

        @Override
        public int compare(final Plugin plugin1, final Plugin plugin2) {
            if (plugin1 == null && plugin2 == null) {
                return 0;
            } else if (plugin1 == null) {
                return -1;
            } else if (plugin2 == null) {
                return 1;
            }
            final Collection<Class<? extends Plugin>> collect1 = plugin1.getPluginDependencies();
            final Collection<Class<? extends Plugin>> collect2 = plugin2.getPluginDependencies();
            if (collect1 != null && collect1.contains(plugin2.getClass())) {
                return 1;
            } else if (collect2 != null && collect2.contains(plugin1.getClass())) {
                return -1;
            } else {
                return plugin1.compareTo(plugin2);
            }
        }
    }


}