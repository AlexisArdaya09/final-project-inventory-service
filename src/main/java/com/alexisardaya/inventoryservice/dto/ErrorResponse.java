package com.alexisardaya.inventoryservice.dto;

import java.time.Instant;
import org.springframework.http.HttpStatus;

/**
 * DTO para respuestas de error estandarizadas.
 * Proporciona una estructura consistente para todos los errores de la API.
 */
public record ErrorResponse(
    Instant timestamp,
    int status,
    String code,
    String message,
    String path
) {

  /**
   * Crea una respuesta de error para recursos no encontrados (404).
   */
  public static ErrorResponse notFound(String message, String path) {
    return new ErrorResponse(
        Instant.now(),
        HttpStatus.NOT_FOUND.value(),
        "RESOURCE_NOT_FOUND",
        message,
        path
    );
  }

  /**
   * Crea una respuesta de error para errores de validación (400).
   */
  public static ErrorResponse validation(String message, String path) {
    return new ErrorResponse(
        Instant.now(),
        HttpStatus.BAD_REQUEST.value(),
        "VALIDATION_ERROR",
        message,
        path
    );
  }

  /**
   * Crea una respuesta de error genérica para errores internos (500).
   */
  public static ErrorResponse generic(String message, String path) {
    return new ErrorResponse(
        Instant.now(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "INTERNAL_SERVER_ERROR",
        message,
        path
    );
  }

  /**
   * Crea una respuesta de error personalizada.
   */
  public static ErrorResponse of(HttpStatus status, String code, String message, String path) {
    return new ErrorResponse(
        Instant.now(),
        status.value(),
        code,
        message,
        path
    );
  }
}

