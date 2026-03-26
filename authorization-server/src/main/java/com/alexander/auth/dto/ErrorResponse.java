package com.alexander.auth.dto;

public record ErrorResponse(
        int codigo,
        String mensaje
) { }
