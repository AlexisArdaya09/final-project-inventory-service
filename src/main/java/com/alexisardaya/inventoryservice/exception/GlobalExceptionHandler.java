package com.alexisardaya.inventoryservice.exception;

import com.alexisardaya.inventoryservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Maneja excepciones cuando un recurso no se encuentra (404).
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex,
      HttpServletRequest request) {
    log.warn("Recurso no encontrado: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.notFound(ex.getMessage(), request.getRequestURI()));
  }

  /**
   * Maneja errores de validación de Bean Validation (400).
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    String message = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .collect(Collectors.joining("; "));
    
    log.warn("Error de validación en {}: {}", request.getRequestURI(), message);
    
    return ResponseEntity.badRequest()
        .body(ErrorResponse.validation(message, request.getRequestURI()));
  }

  /**
   * Maneja errores de validación de parámetros de path/query (400).
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
      HttpServletRequest request) {
    String message = ex.getConstraintViolations()
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.joining("; "));
    
    log.warn("Violación de constraint en {}: {}", request.getRequestURI(), message);
    
    return ResponseEntity.badRequest()
        .body(ErrorResponse.validation(message, request.getRequestURI()));
  }

  /**
   * Maneja errores cuando falta un parámetro requerido (400).
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParameter(
      MissingServletRequestParameterException ex, HttpServletRequest request) {
    String message = "Parámetro requerido faltante: " + ex.getParameterName();
    log.warn("Parámetro faltante en {}: {}", request.getRequestURI(), message);
    
    return ResponseEntity.badRequest()
        .body(ErrorResponse.of(
            HttpStatus.BAD_REQUEST,
            "MISSING_PARAMETER",
            message,
            request.getRequestURI()
        ));
  }

  /**
   * Maneja errores de tipo de argumento incorrecto (400).
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
      HttpServletRequest request) {
    String message = String.format("El parámetro '%s' debe ser de tipo %s",
        ex.getName(),
        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido");
    log.warn("Tipo de argumento incorrecto en {}: {}", request.getRequestURI(), message);
    
    return ResponseEntity.badRequest()
        .body(ErrorResponse.of(
            HttpStatus.BAD_REQUEST,
            "TYPE_MISMATCH",
            message,
            request.getRequestURI()
        ));
  }

  /**
   * Maneja errores cuando el cuerpo de la petición no es válido (400).
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException ex,
      HttpServletRequest request) {
    log.warn("Cuerpo de petición no legible en {}: {}", request.getRequestURI(), ex.getMessage());
    
    String message = "El cuerpo de la petición no es válido. Verifica el formato JSON.";
    if (ex.getMessage() != null && ex.getMessage().contains("JSON")) {
      message = "Error al parsear JSON: formato inválido";
    }
    
    return ResponseEntity.badRequest()
        .body(ErrorResponse.of(
            HttpStatus.BAD_REQUEST,
            "INVALID_REQUEST_BODY",
            message,
            request.getRequestURI()
        ));
  }

  /**
   * Maneja errores de integridad de datos de la base de datos (400).
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
      HttpServletRequest request) {
    log.error("Error de integridad de datos en {}: {}", request.getRequestURI(), ex.getMessage());
    
    String message = "Error de integridad de datos. Verifica que todos los campos requeridos estén presentes.";
    
    if (ex.getMessage() != null) {
      if (ex.getMessage().contains("unique") || ex.getMessage().contains("duplicate")) {
        message = "Ya existe un item de inventario para este producto";
      } else if (ex.getMessage().contains("foreign key") || ex.getMessage().contains("FK")) {
        message = "No se puede realizar la operación debido a restricciones de integridad referencial";
      }
    }

    return ResponseEntity.badRequest()
        .body(ErrorResponse.of(
            HttpStatus.BAD_REQUEST,
            "DATA_INTEGRITY_ERROR",
            message,
            request.getRequestURI()
        ));
  }

  /**
   * Maneja excepciones cuando un item de inventario ya existe (409).
   */
  @ExceptionHandler(InventoryItemAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleInventoryItemExists(InventoryItemAlreadyExistsException ex,
      HttpServletRequest request) {
    log.warn("Intento de crear item de inventario duplicado: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ErrorResponse.of(
            HttpStatus.CONFLICT,
            "INVENTORY_ITEM_EXISTS",
            ex.getMessage(),
            request.getRequestURI()
        ));
  }

  /**
   * Maneja cualquier otra excepción no manejada (500).
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
    log.error("Error inesperado en {}: ", request.getRequestURI(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.generic(
            "Ocurrió un error inesperado. Por favor, contacta al administrador del sistema.",
            request.getRequestURI()
        ));
  }
}

