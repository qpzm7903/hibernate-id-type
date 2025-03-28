package com.example.idtypedemo.type;

import com.example.idtypedemo.domain.Identifier;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;

/**
 * Hibernate Java type descriptor for the Identifier class.
 * Handles conversion between Identifier and other Java types.
 */
public class IdentifierJavaTypeDescriptor extends AbstractClassJavaType<Identifier> {

    public static final IdentifierJavaTypeDescriptor INSTANCE = new IdentifierJavaTypeDescriptor();

    public IdentifierJavaTypeDescriptor() {
        super(Identifier.class);
    }

    @Override
    public <X> X unwrap(Identifier value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }

        if (String.class.isAssignableFrom(type)) {
            return (X) value.asString();
        }

        if (Long.class.isAssignableFrom(type)) {
            try {
                return (X) value.asLong();
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert Identifier with value '" + value + "' to " + type.getName());
            }
        }

        throw new IllegalArgumentException("Cannot unwrap Identifier to " + type.getName());
    }

    @Override
    public <X> Identifier wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return Identifier.of((String) value);
        }

        if (value instanceof Long) {
            return Identifier.of((Long) value);
        }

        throw new IllegalArgumentException("Cannot wrap " + value.getClass().getName() + " as Identifier");
    }

    @Override
    public String toString(Identifier value) {
        return value == null ? null : value.asString();
    }

    @Override
    public Identifier fromString(CharSequence string) {
        if (string == null) {
            return null;
        }

        try {
            return Identifier.of(Long.valueOf(string.toString()));
        } catch (NumberFormatException e) {
            return Identifier.of(string.toString());
        }
    }

    @Override
    public boolean areEqual(Identifier one, Identifier another) {
        if (one == another) {
            return true;
        }
        if (one == null || another == null) {
            return false;
        }
        return one.equals(another);
    }
} 