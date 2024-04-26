package ua.savchenko.user_management.exception;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ua.savchenko.user_management.model.ErrorMessage;

@ControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = { BusinessException.class })
    protected ResponseEntity<Object> handleBusinessExceptions(
            RuntimeException ex, WebRequest request) {
        HttpStatus httpStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class).code();
        ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), httpStatus, httpStatus.value());
        return handleExceptionInternal(ex, errorMessage,
                new HttpHeaders(), httpStatus, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), status, status.value());
        return handleExceptionInternal(ex, errorMessage,
                new HttpHeaders(), status, request);
    }
}
