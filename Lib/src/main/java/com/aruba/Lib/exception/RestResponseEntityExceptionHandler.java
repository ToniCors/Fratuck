package com.aruba.Lib.exception;

import com.aruba.Lib.logging.logger.MsLogger;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;


@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String ACCESS_DENIED = "Access denied!";
    public static final String INVALID_REQUEST = "Invalid request";
    public static final String ERROR_MESSAGE_TEMPLATE = "message: %s %n requested uri: %s";
    public static final String LIST_JOIN_DELIMITER = ",";
    public static final String FIELD_ERROR_SEPARATOR = ": ";
    private static final String ERRORS_FOR_PATH = "errors {} for path {}";
    private static final String PATH = "path";
    private static final String ERRORS = "error";
    private static final String STATUS = "status";
    private static final String MESSAGE = "message";
    private static final String TIMESTAMP = "timestamp";
    private static final String TYPE = "type";

    @ExceptionHandler(value = {ApiException.class})
    protected ResponseEntity<?> handleApiException(ApiException exception, WebRequest request) {
        MsLogger.logger.info("ApiException Exception Handled: " + exception.toString());
        MsLogger.logger.error(exception.getResponseError().toString());
        return getExceptionResponseEntity(exception, exception.getResponseError().getHttpStatus(), request, Collections.singletonList(exception.getResponseError().getMessage()));

    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception, WebRequest request) {
        MsLogger.logger.info("ConstraintViolationException Exception Handled: " + exception.toString());
        final List<String> validationErrors = exception.getConstraintViolations().stream().map(violation -> violation.getPropertyPath() + FIELD_ERROR_SEPARATOR + violation.getMessage()).collect(Collectors.toList());
        return getExceptionResponseEntity(exception, HttpStatus.BAD_REQUEST, request, validationErrors);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ResponseError> handleException(Exception ex) {
        MsLogger.logger.info("Exception Handled");

        MsLogger.logger.error("Exception: {}, Message: {}",ex.getClass().getCanonicalName(), ex.getMessage());

        printStackTrace(ex);
        ResponseError error = ResponseError.builder().httpStatus(HttpStatus.INTERNAL_SERVER_ERROR).errorCodes(ErrorCodes.INTERNAL_SERVER_ERROR).message("Errore del sistema non previsto").moreInfo(ex.getMessage()).build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseEntity<Object> test(final TypeMismatchException exception, final HttpStatus status, final WebRequest request, final List<String> errors) {

        final Map<String, Object> body = new LinkedHashMap<>();
        final String path = request.getDescription(false);

        body.put("-getPropertyName-", exception.getPropertyName());
        body.put("-getRequiredType-", exception.getRequiredType());
        body.put("-getValue-", exception.getValue());


        body.put(TIMESTAMP, new Date());
        body.put(STATUS, status.value());
        body.put(ERRORS, errors);
        body.put(TYPE, exception.getClass().getSimpleName());
        body.put(PATH, path);
        body.put(MESSAGE, getMessageForStatus(status));
        final String errorsMessage = !errors.isEmpty() ? errors.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining(LIST_JOIN_DELIMITER)) : status.getReasonPhrase();
        MsLogger.logger.error(ERRORS_FOR_PATH, errorsMessage, path);
        return new ResponseEntity<>(body, status);
    }

    private ResponseEntity<Object> getExceptionResponseEntity(final Exception exception, final HttpStatus status, final WebRequest request, final List<String> errors) {
        final Map<String, Object> body = new LinkedHashMap<>();
        final String path = request.getDescription(false);

        body.put(TIMESTAMP, new Date());
        body.put(STATUS, status.value());
        body.put(ERRORS, errors);
        body.put(TYPE, exception.getClass().getSimpleName());
        body.put(PATH, path);
        body.put(MESSAGE, getMessageForStatus(status));
        final String errorsMessage = !errors.isEmpty() ? errors.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining(LIST_JOIN_DELIMITER)) : status.getReasonPhrase();
        MsLogger.logger.error(ERRORS_FOR_PATH, errorsMessage, path);
        return new ResponseEntity<>(body, status);
    }

    private String getMessageForStatus(HttpStatus status) {
        return switch (status) {
            case UNAUTHORIZED -> ACCESS_DENIED;
            case BAD_REQUEST -> INVALID_REQUEST;
            default -> status.getReasonPhrase();
        };
    }

    private void printStackTrace(Exception e) {

        e.printStackTrace();
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        e.printStackTrace(pw);
//
//        System.out.println(pw);


    }

}
