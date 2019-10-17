package org.laruche.james.plugin.utils;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class BeanInCollectionPredicateTest {

    @Test
    void shouldTest() {
        final BeanInCollectionPredicate<String> predicate = new BeanInCollectionPredicate<>(asList("str1", "str2", "str3"));
        assertThat(predicate.test("str1")).isTrue();
        assertThat(predicate.test(null)).isFalse();
        assertThat(predicate.test("str2")).isTrue();
        assertThat(predicate.test("other")).isFalse();
    }

}