package es.fernandopal.aforoqr.api.exception;

public class ApiUnauthorizedException extends RuntimeException {
    public boolean showView;

    public ApiUnauthorizedException(String message) {
        super(message);
    }

    public ApiUnauthorizedException(String message, String location, Boolean simplifyError) {
        super(message + " - " + location);
        this.showView = simplifyError;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
