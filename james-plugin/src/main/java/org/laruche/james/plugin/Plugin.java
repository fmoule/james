package org.laruche.james.plugin;

import java.util.Collection;

/**
 * <p>
 * Interface représentant les plugins. <br />
 * </p>
 */
public interface Plugin extends AutoCloseable, Identifiable {

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
}

