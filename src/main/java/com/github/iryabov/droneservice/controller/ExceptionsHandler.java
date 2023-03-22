package com.github.iryabov.droneservice.controller;

import com.github.iryabov.droneservice.exception.DroneDeliveryException;
import com.github.iryabov.droneservice.model.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@ControllerAdvice
@RestController
public class ExceptionsHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ValidationError handleConstraintViolation(ConstraintViolationException ex) {
        ValidationError error = new ValidationError();
        error.setMessage("Some fields are filled in incorrectly");
        error.setErrors(ex.getConstraintViolations().stream()
                .map(violation -> new ValidationError.Field(
                        violation.getMessage(),
                        buildFieldName(violation)))
                .collect(toList()));
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DroneDeliveryException.class)
    public ValidationError handleBusinessValidations(DroneDeliveryException ex) {
        ValidationError error = new ValidationError();
        error.setMessage(ex.getMessage());
        return error;
    }

    private String buildFieldName(ConstraintViolation<?> violation) {
        return StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
                .reduce((first, second) -> second)
                .map(Path.Node::toString)
                .orElse(null);
    }
}
