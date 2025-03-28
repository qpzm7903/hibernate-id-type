package com.example.idtypedemo.type;

import com.example.idtypedemo.domain.Identifier;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 * Custom Hibernate type for the Identifier class.
 * Maps the Identifier to the appropriate database column type based on its Java type:
 * - Long Identifiers are stored as BIGINT
 * - String Identifiers are stored as VARCHAR
 */
public class IdentifierType implements UserType<Identifier> {

    // Constant for the type discriminator column
    public static final String TYPE_DISCRIMINATOR_PREFIX = "__TYPE:";

    @Override
    public int getSqlType() {
        // For metadata purposes, we default to VARCHAR as it can store both types
        return Types.VARCHAR;
    }

    @Override
    public Class<Identifier> returnedClass() {
        return Identifier.class;
    }

    @Override
    public boolean equals(Identifier x, Identifier y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Identifier x) {
        return Objects.hashCode(x);
    }

    @Override
    public Identifier nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) 
            throws SQLException {
        String value = rs.getString(position);
        if (rs.wasNull() || value == null) {
            return null;
        }
        
        if (value.startsWith(TYPE_DISCRIMINATOR_PREFIX)) {
            // This is a String identifier with our type discriminator
            String actualValue = value.substring(TYPE_DISCRIMINATOR_PREFIX.length());
            return Identifier.of(actualValue);
        }
        
        try {
            // Try to parse as Long first
            return Identifier.of(Long.valueOf(value));
        } catch (NumberFormatException e) {
            // If not a valid Long, use as String
            return Identifier.of(value);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Identifier value, int index, SharedSessionContractImplementor session) 
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
            return;
        }
        
        if (value.getType() == Identifier.Type.LONG) {
            // For Long values, store directly as BIGINT
            st.setLong(index, value.asLong());
        } else {
            // For String values, add a type discriminator prefix to ensure
            // we can distinguish strings that could be parsed as numbers
            String stringValue = value.asString();
            try {
                Long.parseLong(stringValue);
                // If we get here, the string is parseable as a Long, so add discriminator
                st.setString(index, TYPE_DISCRIMINATOR_PREFIX + stringValue);
            } catch (NumberFormatException e) {
                // Not parseable as Long, no need for discriminator
                st.setString(index, stringValue);
            }
        }
    }

    @Override
    public Identifier deepCopy(Identifier value) {
        if (value == null) {
            return null;
        }
        
        // Identifier is immutable, so we can return the original instance
        return value;
    }

    @Override
    public boolean isMutable() {
        // Identifier is immutable
        return false;
    }

    @Override
    public Serializable disassemble(Identifier value) {
        if (value == null) {
            return null;
        }
        
        if (value.getType() == Identifier.Type.LONG) {
            return value.asLong();
        } else {
            String stringValue = value.asString();
            try {
                Long.parseLong(stringValue);
                // If we get here, the string is parseable as a Long, so add discriminator
                return TYPE_DISCRIMINATOR_PREFIX + stringValue;
            } catch (NumberFormatException e) {
                // Not parseable as Long, no need for discriminator
                return stringValue;
            }
        }
    }

    @Override
    public Identifier assemble(Serializable cached, Object owner) {
        if (cached == null) {
            return null;
        }
        
        if (cached instanceof Long) {
            return Identifier.of((Long) cached);
        } else {
            String value = cached.toString();
            if (value.startsWith(TYPE_DISCRIMINATOR_PREFIX)) {
                return Identifier.of(value.substring(TYPE_DISCRIMINATOR_PREFIX.length()));
            }
            
            try {
                // Try to parse as Long first if it's not prefixed
                return Identifier.of(Long.valueOf(value));
            } catch (NumberFormatException e) {
                // If not a valid Long, use as String
                return Identifier.of(value);
            }
        }
    }

    @Override
    public Identifier replace(Identifier original, Identifier target, Object owner) {
        // Identifier is immutable, so we can return the original instance
        return original;
    }
} 