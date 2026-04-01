package com.concierge.backend.controller.exceptionhandler;

public record ApiErrorResponse(
        int status,
        String message,
        java.util.Map<String, String> errors
) {}
