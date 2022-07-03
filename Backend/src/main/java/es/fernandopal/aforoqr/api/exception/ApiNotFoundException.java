package es.fernandopal.aforoqr.api.exception;

public class ApiNotFoundException extends RuntimeException {
    public boolean showView;

    public ApiNotFoundException(String message) {
        super(message);
    }

    public ApiNotFoundException(String message, Boolean simplifyError) {
        super(message);
        this.showView = simplifyError;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
