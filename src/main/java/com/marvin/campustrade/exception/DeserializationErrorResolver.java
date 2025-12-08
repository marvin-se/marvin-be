package com.marvin.campustrade.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.math.BigDecimal;
import java.util.Arrays;

public class DeserializationErrorResolver {
    public static RuntimeException resolve(InvalidFormatException ex) {
        String field = ex.getPath().get(0).getFieldName();
        Class<?> target = ex.getTargetType();

        // ENUM fields
        if (target.isEnum()) {
            String allowed = Arrays.toString(target.getEnumConstants());
            return new InvalidEnumValueException(
                    field + ": Invalid value. Allowed values are " + allowed
            );
        }

        // Numeric fields
        if (Number.class.isAssignableFrom(target)) {
            return new InvalidNumberFormatException(
                    field + ": Must be a valid number and cannot be empty."
            );
        }

        // BigDecimal specific
        if (target.equals(BigDecimal.class)) {
            return new InvalidNumberFormatException(
                    field + ": Must be a valid decimal number"
            );
        }
        return new InvalidRequestFieldException(
                field + ": Invalid value."
        );
    }
}
