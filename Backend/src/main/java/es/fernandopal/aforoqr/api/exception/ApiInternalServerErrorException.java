package es.fernandopal.aforoqr.api.exception;

public class ApiInternalServerErrorException extends RuntimeException {
    public boolean showView;

    public ApiInternalServerErrorException(String message) {
        super(message);
    }

    public ApiInternalServerErrorException(String message, Boolean simplifyError) {
        super(message);
        this.showView = simplifyError;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
