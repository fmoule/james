package org.laruche.james.agent.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.laruche.james.agent.web.JSONResponse.createErrorResponse;
import static org.laruche.james.agent.web.JSONResponse.createOKResponse;

public class JSONResponseTest {

    @Test
    public void shouldBeEquals() {
        assertThat(createOKResponse("Test")).isEqualTo(createOKResponse("Test"));
        assertThat(createOKResponse("Test").hashCode())
                .isEqualTo(createOKResponse("Test").hashCode());
        assertThat(createOKResponse("Test")).isNotEqualTo(null);
        assertThat(createErrorResponse("Erreur")).isEqualTo(createErrorResponse("Erreur"));
        assertThat(createErrorResponse("Erreur").hashCode())
                .isEqualTo(createErrorResponse("Erreur").hashCode());
        assertThat(createOKResponse("Test")).isNotEqualTo(createOKResponse("Other message"));
        assertThat(createOKResponse("Test")).isNotEqualTo(createErrorResponse("Test"));
    }

}