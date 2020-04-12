package org.laruche.james.plugin.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigPluginTest {
    private ConfigPlugin confPlugin;
    private static final String BASE_PATH = ConfigPluginTest.class.getResource(".").getPath();

    @AfterEach
    void tearDown() throws Exception {
        if (confPlugin != null
                && confPlugin.isStarted()) {
            confPlugin.stop();
        }
    }

    @Test
    public void shouldLoadProperties() throws Exception {
        confPlugin = new ConfigPlugin("confPlugin", BASE_PATH + "/config.properties");
        confPlugin.start();
        assertThat(confPlugin.getNbProperties()).isEqualTo(2);
        assertThat(confPlugin.getProperty("prop1")).isEqualTo("val1");
        assertThat(confPlugin.getProperty("prop2")).isEqualTo("val2");
    }

    @Test
    public void shouldThrowExceptionWithNotExistingFile() {
        confPlugin = new ConfigPlugin("confPlugin", BASE_PATH + "/notExistingFile.properties");
        try {
            confPlugin.start();
        } catch (final Exception exception) {
            assertThat(exception.getMessage()).isEqualTo("Le fichier de configuration " + BASE_PATH + "/notExistingFile.properties n'existe pas");
        }
    }

    @Test
    public void shouldThrowExceptionWithNullFile() {
        confPlugin = new ConfigPlugin("confPlugin", null);
        try {
            confPlugin.start();
        } catch (final Exception exception) {
            assertThat(exception.getMessage()).isEqualTo("Le fichier de configuration ne doit pas être nul ou vide");
        }
    }

}