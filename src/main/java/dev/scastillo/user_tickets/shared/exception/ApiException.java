package dev.scastillo.user_tickets.shared.exception;

import dev.scastillo.user_tickets.shared.dtos.ErrorResponse;
import dev.scastillo.user_tickets.shared.dtos.FieldError;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class ApiException extends RuntimeException {
    private final String code;
    private final String detailError;
    private List<FieldError> fieldErrors = new ArrayList<>();

    public ApiException(String code, String detailError) {
        super(detailError);
        this.code = code;
        this.detailError = detailError;
    }

    public abstract int getHttpStatus();

    public String getCode() {
        return code;
    }

    public String getDetailError() {
        return detailError;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void addFieldError(FieldError fieldError) {
        this.fieldErrors.add(fieldError);
    }

    public ErrorResponse toErrorResponse() {
        HttpStatus status = HttpStatus.valueOf(getHttpStatus());
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(getHttpStatus())
                .error(status.getReasonPhrase())
                .message(getDetailError())
                .code(code)
                .fieldErrors(fieldErrors.isEmpty() ? null : fieldErrors)
                .build();
    }
}