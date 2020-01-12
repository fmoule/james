package org.laruche.james.bean;

import org.junit.jupiter.api.Test;
import org.laruche.james.bean.TestBeanOntology.AddTestBeanAction;

import static org.assertj.core.api.Assertions.assertThat;

class TestBeanOntologyTest {

    @Test
    public void shouldGetBeanFromAddAction() throws Exception {
        final AddTestBeanAction addTestBeanAction = new AddTestBeanAction("frederic", "moule");
        assertThat(addTestBeanAction.getTestBean()).isEqualTo(new TestBean("frederic", "moule"));
    }

}