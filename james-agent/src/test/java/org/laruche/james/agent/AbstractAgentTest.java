package org.laruche.james.agent;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.laruche.james.message.MessageUtils.DEFAULT_LANGUAGE;

class AbstractAgentTest {

    @Test
    void shouldGetCodecName() {
        assertThat(DEFAULT_LANGUAGE.getName()).isNotNull();
    }
}