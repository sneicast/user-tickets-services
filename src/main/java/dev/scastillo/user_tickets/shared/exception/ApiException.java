package dev.scastillo.user_tickets.shared.exception;

import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {
    private final String code;

    public ApiException(String code, String detailError) {
        super(detailError);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public abstract int getHttpStatus();
}
