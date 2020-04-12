package org.laruche.james.plugin.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.laruche.james.plugin.AbstractPlugin;
import org.laruche.james.plugin.Plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class DefaultPluginManagerTest {
    private final DefaultPluginManager pluginManager = new DefaultPluginManager("pluginManager");

    @AfterEach
    void tearDown() throws Exception {
        pluginManager.close();
    }

    @Test
    void shouldStartThePlugins() throws Exception {
        pluginManager.addPlugin(new TestPlugin("plugin1"));
        pluginManager.addPlugin(new TestPlugin("plugin2"));
        pluginManager.addPlugin(new TestPlugin("plugin3"));
        pluginManager.start();
        Plugin plugin = pluginManager.findFirstPlugin((plugin1 -> "plugin2".equals(plugin1.getId())));
        assertThat(plugin.isStarted()).isTrue();
        plugin = pluginManager.findFirstPlugin((plugin1 -> "plugin3".equals(plugin1.getId())));
        assertThat(plugin.isStarted()).isTrue();
        plugin = pluginManager.findFirstPlugin((plugin1 -> "plugin1".equals(plugin1.getId())));
        assertThat(plugin.isStarted()).isTrue();
    }

    @Test
    void shouldStartPluginsWithDependencies() throws Exception {
        pluginManager.addPlugin(new OtherTestPlugin("plugin1"));
        TestPlugin plugin2 = new TestPlugin("plugin2");
        plugin2.addPluginDependency(OtherTestPlugin.class);
        pluginManager.addPlugin(plugin2);
        pluginManager.start();
        Plugin plugin = pluginManager.findFirstPlugin("plugin2");
        assertThat(plugin).isNotNull();
        assertThat(plugin.isStarted()).isTrue();
    }

    @Test
    void shouldNotStartPluginsWithDependencies() throws Exception {
        final TestPlugin plugin2 = new TestPlugin("plugin2");
        plugin2.addPluginDependency(OtherTestPlugin.class);
        pluginManager.addPlugin(plugin2);
        try {
            pluginManager.start();
            fail("Doit échouer");
        } catch (final Exception e) {
            final Plugin plugin = pluginManager.findFirstPlugin("plugin2");
            assertThat(plugin).isNotNull();
            assertThat(plugin.isStarted()).isFalse();
            assertThat(e.getMessage()).isEqualTo("Un des plugins {OtherTestPlugin} n'est pas démarré");
        }
    }

    @Test
    void shouldFindPluginByItsClass() {
        pluginManager.addPlugin(new TestPlugin("plugin1"));
        final Plugin plugin = pluginManager.findFirstPlugin(TestPlugin.class);
        assertThat(plugin).isNotNull();
        assertThat(plugin.getId()).isEqualTo("plugin1");
    }

    @Test
    void shouldFindPluginById() throws Exception {
        pluginManager.addPlugin(new TestPlugin("plugin1"));
        pluginManager.addPlugin(new TestPlugin("plugin2"));
        final Plugin plugin = pluginManager.findFirstPlugin("plugin2");
        assertThat(plugin).isNotNull();
    }

    @Test
    void shoudRemoveThePlugin() throws Exception {
        pluginManager.addPlugin(new TestPlugin("plugin1"));
        pluginManager.addPlugin(new TestPlugin("plugin2"));
        pluginManager.addPlugin(new TestPlugin("plugin3"));
        pluginManager.start();
        Plugin plugin = pluginManager.findFirstPlugin("plugin3");
        assertThat(plugin).isNotNull();
        assertThat(plugin.isStarted()).isTrue();
        pluginManager.removePlugin(plugin);
        plugin = pluginManager.findFirstPlugin("plugin3");
        assertThat(plugin).isNull();
    }

    ///// Classes Internes /////

    private static class TestPlugin extends AbstractPlugin {

        private TestPlugin(final String id) {
            super(id);
        }

        @Override
        protected void doStart() {
            // DO NOTHING !!
        }

        @Override
        protected void doStop() {
            // DO NOTHING !!
        }
    }

    private static class OtherTestPlugin extends AbstractPlugin {

        private OtherTestPlugin(final String id) {
            super(id);
        }

        @Override
        protected void doStart() {
            // DO NOTHING !!
        }

        @Override
        protected void doStop() {
            // DO NOTHING !!
        }
    }
}