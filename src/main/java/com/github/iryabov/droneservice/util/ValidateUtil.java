package com.github.iryabov.droneservice.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.util.Set;

public abstract class ValidateUtil {

    public static  <T> void validate(Validator validator, T input) {
        Set<ConstraintViolation<T>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
