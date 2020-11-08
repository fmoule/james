package org.laruche.james.plugin;

import java.util.Collection;

/**
 * <p>
 * Interface représentant les plugins. <br />
 * </p>
 */
public interface Plugin extends AutoCloseable, Identifiable, Comparable<Plugin> {

    /**
     * Méthode démarrant le plugin. <br />
     *
     * @throws Exception : En cas d'exception lors du démarrage
     */
    void start() throws Exception;

    /**
     * Méthode permettant d'arreter le plugin. <br />
     *
     * @throws Exception : En cas d'exception
     */
    void stop() throws Exception;

    /**
     * Retourne un booléen montrant si le plugin est démarré.
     *
     * @return booléen
     */
    boolean isStarted();


    @Override
    default void close() throws Exception {
        this.stop();
    }

    Collection<Class<? extends Plugin>> getPluginDependencies();

    ///// Méthodes par défaut :

    @Override
    default int compareTo(final Plugin plugin) {
        if (plugin == null) {
            return 1;
        }
        final String id1 = this.getId();
        final String id2 = plugin.getId();
        if (id1 == null && id2 == null) {
            return 0;
        } else if (id1 == null) {
            return -1;
        } else if (id2 == null) {
            return 1;
        } else {
            return id1.compareTo(id2);
        }
    }
}

