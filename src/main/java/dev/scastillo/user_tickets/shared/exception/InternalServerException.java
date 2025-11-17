package dev.scastillo.user_tickets.shared.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends ApiException {
    public InternalServerException(String code, String detailError) {
        super(code, detailError);
    }

    @Override
    public int getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
