package dev.scastillo.user_tickets.shared.code_error;


import lombok.Getter;

@Getter
public enum ErrorCode {



    ROLE_NOT_FOUND("ROLE_NOT_FOUND", "Rol no encontrado"),


    INTERNAL_ERROR("ABC001", "Error no controlado"),
    BAD_REQUEST("ABC002", "Solicitud inválida"),
    UNAUTHORIZED("ABC003", "No autorizado"),
    FORBIDDEN("ABC004", "Acceso prohibido"),
    NOT_FOUND("ABC005", "Recurso no encontrado");
    // Agrega más códigos según tus necesidades

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    public static ErrorCode valueOfCode(String code) {
        for (ErrorCode value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
