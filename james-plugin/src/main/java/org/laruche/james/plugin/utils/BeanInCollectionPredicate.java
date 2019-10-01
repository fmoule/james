package org.laruche.james.plugin.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public class BeanInCollectionPredicate<T> implements Predicate<T> {
    private final Collection<T> beans = new ArrayList<>();

    public BeanInCollectionPredicate(final Collection<? extends T> beans) {
        this.beans.addAll(beans);
    }

    @Override
    public boolean test(final T bean) {
        return this.beans.contains(bean);
    }
}
