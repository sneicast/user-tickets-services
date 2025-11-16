package dev.scastillo.user_tickets.shared.exception;

import dev.scastillo.user_tickets.shared.code_error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        String trackingId = request.getHeader("tracking-id");
        ErrorCode errorCode = ErrorCode.valueOfCode(ex.getCode());
        String message = errorCode != null ? errorCode.getMessage() : "Error desconocido";

        logger.error("ErrorCode: {}, Message: {}, TrackingId: {}, Exception: {}",
                ex.getCode(), message, trackingId, ex);

        ErrorResponse response = new ErrorResponse(
                new ErrorResponse.ErrorDetail(ex.getCode(), message)
        );
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }
}
