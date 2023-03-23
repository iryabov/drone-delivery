package com.github.iryabov.dronedelivery.controller;

import com.github.iryabov.dronedelivery.exception.DroneDeliveryException;
import com.github.iryabov.dronedelivery.exception.DroneDischargedException;
import com.github.iryabov.dronedelivery.exception.DroneOverweightException;
import com.github.iryabov.dronedelivery.model.ResponseError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@ControllerAdvice
@RestController
public class ExceptionsHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseError handleConstraintViolation(ConstraintViolationException ex) {
        ResponseError error = new ResponseError();
        error.setMessage("Some fields are filled in incorrectly");
        error.setErrors(ex.getConstraintViolations().stream()
                .map(violation -> new ResponseError.Field(
                        violation.getMessage(),
                        buildFieldName(violation)))
                .collect(toList()));
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DroneDeliveryException.class)
    public ResponseError handleBusinessValidations(DroneDeliveryException ex) {
        ResponseError error = new ResponseError();
        error.setMessage(ex.getMessage());
        return error;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public void handleNoSuchElement(NoSuchElementException ex) {}

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DroneDischargedException.class)
    public ResponseError handleDischarge(DroneDischargedException ex) {
        ResponseError error = new ResponseError();
        error.setMessage("Drone is fully discharged");
        return error;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DroneDischargedException.class)
    public ResponseError handleDischarge(DroneOverweightException ex) {
        ResponseError error = new ResponseError();
        error.setMessage("Too big load");
        return error;
    }

    private String buildFieldName(ConstraintViolation<?> violation) {
        return StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
                .reduce((first, second) -> second)
                .map(Path.Node::toString)
                .orElse(null);
    }
}
