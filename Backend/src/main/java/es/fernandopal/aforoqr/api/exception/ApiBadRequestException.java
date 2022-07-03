package es.fernandopal.aforoqr.api.exception;

public class ApiBadRequestException extends RuntimeException {
    public boolean showView = false;

    public ApiBadRequestException(String message) {
        super(message);
    }

    public ApiBadRequestException(String message, Boolean simplifyError) {
        super(message);
        this.showView = simplifyError;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
