package com.github.iryabov.droneservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class ResponseId<T> {
    @Schema(description = "Identifier of created entity", example = "1")
    private T id;

    public ResponseId(T id) {
        this.id = id;
    }
}
