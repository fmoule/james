package org.laruche.james.plugin;

import java.util.Collection;
import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * <p>
 * Interface définissant les gestionnaires de plugin. <br />
 * </p>
 *
 * @see Plugin
 */
public interface PluginManager extends Plugin {

    void addPlugin(Plugin plugin);

    void removePlugin(Plugin plugin) throws Exception;

    Collection<Plugin> findPlugins(Predicate<Plugin> predicate);

    ///// Méthode

    default Plugin findFirstPlugin(final Predicate<Plugin> predicate) {
        if (predicate == null) {
            return null;
        }
        final Collection<Plugin> foundPlugins = this.findPlugins(predicate);
        if (foundPlugins != null && !foundPlugins.isEmpty()) {
            return foundPlugins.stream().findFirst().orElse(null);
        } else {
            return null;
        }
    }

    /**
     * <p>
     * Méthode permettant de récupérer le premier plugin dont
     * la classe correspond au paramètre de la fonction.<br />
     * </p>
     *
     * @param pluginClass :  classe du plugin
     * @param <T>         type de Plugin
     * @return premier plugin de classe <i>pluginClass</i>
     */
    default <T extends Plugin> T findFirstPlugin(final Class<? extends T> pluginClass) {
        if (pluginClass == null) {
            return null;
        }
        //noinspection unchecked
        return (T) this.findFirstPlugin(new ClassPredicate(pluginClass));
    }

    default Plugin findFirstPlugin(final String pluginId) {
        if (isEmpty(pluginId)) {
            return null;
        }
        return this.findFirstPlugin(new PluginIdPredicate(pluginId));
    }

    ///// Classes internes :

    /**
     * <p>
     * Prédicat testant la classe de l'objet. <br />
     * </p>
     */
    class ClassPredicate implements Predicate<Plugin> {
        private final Class<? extends Plugin> initClass;

        public ClassPredicate(final Class<? extends Plugin> pluginClass) {
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

    class PluginIdPredicate implements Predicate<Plugin> {
        private final String pluginId;

        public PluginIdPredicate(final String pluginId) {
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
