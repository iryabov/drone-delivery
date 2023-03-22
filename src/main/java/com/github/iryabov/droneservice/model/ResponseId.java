package com.github.iryabov.droneservice.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class ResponseId<T> {
    private T id;

    public ResponseId(T id) {
        this.id = id;
    }
}