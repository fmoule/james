package org.laruche.james.plugin.utils;

import java.util.Objects;
import java.util.function.Predicate;

public class BeanEqualsPredicate<T> implements Predicate<T> {
    private final T initBean;

    public BeanEqualsPredicate(final T initBean) {
        this.initBean = initBean;
    }

    @Override
    public boolean test(final T bean) {
        return Objects.equals(initBean, bean);
    }
}
