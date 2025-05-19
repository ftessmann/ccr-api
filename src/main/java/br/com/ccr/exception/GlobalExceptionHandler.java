package br.com.ccr.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.Getter;
import lombok.Setter;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Erro interno do servidor", exception.getMessage()))
                .build();
    }

    public static class ErrorResponse {
        private String message;
        private String details;

        public ErrorResponse(String message, String details) {
            this.message = message;
            this.details = details;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

    }
}
