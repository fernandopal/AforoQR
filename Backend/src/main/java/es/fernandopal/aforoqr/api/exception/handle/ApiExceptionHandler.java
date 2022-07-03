package es.fernandopal.aforoqr.api.exception.handle;

import es.fernandopal.aforoqr.api.exception.ApiBadRequestException;
import es.fernandopal.aforoqr.api.exception.ApiInternalServerErrorException;
import es.fernandopal.aforoqr.api.exception.ApiNotFoundException;
import es.fernandopal.aforoqr.api.exception.ApiUnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Clase encargada de manejar excepciones, devuelve resupestas HTTP ya formateadas
 */

@ControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private ResponseEntity<Object> constructException(String message, HttpStatus status, Boolean showView) {
        LOGGER.warn(message);

        if (showView) return new ResponseEntity<>(message, new HttpHeaders(), status);

        ApiException apiException = new ApiException(message, status, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, new HttpHeaders(), status);
    }

    /* API CUSTOM EXCEPTIONS */
    @ExceptionHandler(value = {ApiUnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedException(ApiUnauthorizedException exception) {
        return constructException(exception.getMessage(), HttpStatus.UNAUTHORIZED, exception.showView);
    }

    @ExceptionHandler(value = {ApiBadRequestException.class})
    public ResponseEntity<Object> handleBadRequestException(ApiBadRequestException exception) {
        return constructException(exception.getMessage(), HttpStatus.BAD_REQUEST, exception.showView);
    }

    @ExceptionHandler(value = {ApiInternalServerErrorException.class})
    public ResponseEntity<Object> handleInternalServerErrorException(ApiInternalServerErrorException exception) {
        return constructException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, exception.showView);
    }

    @ExceptionHandler(value = {ApiNotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(ApiNotFoundException exception) {
        return constructException(exception.getMessage(), HttpStatus.NOT_FOUND, exception.showView);
    }

    /* GENERIC EXCEPTIONS */
    @ExceptionHandler(value = {RequestRejectedException.class})
    public ResponseEntity<Object> handleRequestRejectedException(RequestRejectedException exception) {
        return constructException(exception.getMessage(), HttpStatus.BAD_REQUEST, false);
    }
}
