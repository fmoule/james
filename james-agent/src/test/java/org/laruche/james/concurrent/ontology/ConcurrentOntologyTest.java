package org.laruche.james.concurrent.ontology;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.laruche.james.concurrent.ontology.ConcurrentOntology.deserialize;
import static org.laruche.james.concurrent.ontology.ConcurrentOntology.serialize;

class ConcurrentOntologyTest {

    @Test
    public void shouldSerialiazeAndDesrialize() throws Exception {
        byte[] bytes = serialize("toto");
        assertThat(bytes).isNotNull();
        assertThat((String) deserialize(bytes)).isEqualTo("toto");
        bytes = serialize(56);
        assertThat(bytes).isNotNull();
        assertThat((Integer) deserialize(bytes)).isEqualTo(56);
        bytes = serialize(null);
        assertThat(bytes).isNotNull();
        assertThat(bytes.length).isEqualTo(0);
        assertThat((Object) deserialize(bytes)).isNull();
    }

}