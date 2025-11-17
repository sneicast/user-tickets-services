package dev.scastillo.user_tickets.shared.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {

    public NotFoundException(String code, String detailError) {
        super(code, detailError);
    }
    @Override
    public int getHttpStatus() {
        return HttpStatus.NOT_FOUND.value();
    }
}
