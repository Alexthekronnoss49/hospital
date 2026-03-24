package com.alexander.commons.dto;

public record ErrorResponse(
        int codigo,
        String mensaje
) {
}
