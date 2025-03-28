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
 * Maps the Identifier to a VARCHAR column, using type discriminators to distinguish between Long and String values.
 */
public class IdentifierType implements UserType<Identifier> {

    // Constants for type discriminators
    public static final String LONG_TYPE_PREFIX = "__LONG:";
    public static final String STRING_TYPE_PREFIX = "__STRING:";

    @Override
    public int getSqlType() {
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
        
        if (value.startsWith(LONG_TYPE_PREFIX)) {
            String longValue = value.substring(LONG_TYPE_PREFIX.length());
            return Identifier.of(Long.valueOf(longValue));
        } else if (value.startsWith(STRING_TYPE_PREFIX)) {
            return Identifier.of(value.substring(STRING_TYPE_PREFIX.length()));
        }
        
        // For backward compatibility, try to parse as Long if no prefix
        try {
            return Identifier.of(Long.valueOf(value));
        } catch (NumberFormatException e) {
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
            st.setString(index, LONG_TYPE_PREFIX + value.asLong());
        } else {
            st.setString(index, STRING_TYPE_PREFIX + value.asString());
        }
    }

    @Override
    public Identifier deepCopy(Identifier value) {
        return value; // Identifier is immutable
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Identifier value) {
        if (value == null) {
            return null;
        }
        
        if (value.getType() == Identifier.Type.LONG) {
            return LONG_TYPE_PREFIX + value.asLong();
        } else {
            return STRING_TYPE_PREFIX + value.asString();
        }
    }

    @Override
    public Identifier assemble(Serializable cached, Object owner) {
        if (cached == null) {
            return null;
        }
        
        String value = cached.toString();
        if (value.startsWith(LONG_TYPE_PREFIX)) {
            return Identifier.of(Long.valueOf(value.substring(LONG_TYPE_PREFIX.length())));
        } else if (value.startsWith(STRING_TYPE_PREFIX)) {
            return Identifier.of(value.substring(STRING_TYPE_PREFIX.length()));
        }
        
        // For backward compatibility
        try {
            return Identifier.of(Long.valueOf(value));
        } catch (NumberFormatException e) {
            return Identifier.of(value);
        }
    }

    @Override
    public Identifier replace(Identifier original, Identifier target, Object owner) {
        return original; // Identifier is immutable
    }
} 