package com.github.iryabov.dronedelivery.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ResponseError {
    private String message;
    private List<Field> errors;


    @Getter
    @Setter
    @NoArgsConstructor
    public static class Field {
        private String message;
        private String field;

        public Field(String message, String field) {
            this.message = message;
            this.field = field;
        }
    }
}
