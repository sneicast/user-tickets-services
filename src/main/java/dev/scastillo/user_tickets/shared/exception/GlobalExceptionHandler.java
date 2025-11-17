package dev.scastillo.user_tickets.shared.exception;

import dev.scastillo.user_tickets.shared.dtos.ErrorResponse;
import dev.scastillo.user_tickets.shared.dtos.FieldError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        return new ResponseEntity<>(ex.toErrorResponse(), HttpStatus.valueOf(ex.getHttpStatus()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.add(FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
        );

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Error de validación en los campos")
                .code("VALIDATION_ERROR")
                .fieldErrors(fieldErrors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        List<FieldError> fieldErrors = new ArrayList<>();
        String message = ex.getCause().getMessage();

        if (message.contains("UUID")) {
            fieldErrors.add(FieldError.builder()
                    .field("userId")
                    .message("El userId debe ser un UUID válido")
                    .rejectedValue(extractRejectedValue(message))
                    .build());
        } else if (message.contains("TicketStatus") || message.contains("enum")) {
            fieldErrors.add(FieldError.builder()
                    .field("status")
                    .message("El estado debe ser ABIERTO o CERRADO")
                    .rejectedValue(extractRejectedValue(message))
                    .build());
        }

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Error en el formato de los datos")
                .code("INVALID_FORMAT_ERROR")
                .fieldErrors(fieldErrors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        List<FieldError> fieldErrors = new ArrayList<>();
        String fieldName = ex.getName();
        String message = "Formato inválido";

        if (ex.getRequiredType() == UUID.class) {
            message = "El ID debe ser un UUID válido";
        }

        fieldErrors.add(FieldError.builder()
                .field(fieldName)
                .message(message)
                .rejectedValue(ex.getValue())
                .build());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Error en el formato de los parámetros")
                .code("INVALID_TYPE_ERROR")
                .fieldErrors(fieldErrors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private String extractRejectedValue(String message) {
        int startIndex = message.indexOf("\"");
        int endIndex = message.indexOf("\"", startIndex + 1);
        if (startIndex != -1 && endIndex != -1) {
            return message.substring(startIndex + 1, endIndex);
        }
        return null;
    }
}
