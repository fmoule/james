package org.laruche.james.bean;

import java.io.Serializable;
import java.util.Objects;

public class TestBean implements Serializable {
    private final String firstName;
    private final String name;

    public TestBean(final String firstName, final String name) {
        this.firstName = firstName;
        this.name = name;
    }

    ///// Methods

    @Override
    public int hashCode() {
        int hashCode = Objects.hashCode(this.firstName);
        hashCode = (17 * hashCode) + (Objects.hashCode(this.name));
        return hashCode;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (this.getClass() != obj.getClass())) {
            return false;
        }
        final TestBean that = (TestBean) obj;
        boolean equals = Objects.equals(this.firstName, that.firstName);
        equals = equals && Objects.equals(this.name, that.name);
        return equals;
    }


    ///// Getters & Setters :

    public String getFirstName() {
        return firstName;
    }

    public String getName() {
        return name;
    }
}
