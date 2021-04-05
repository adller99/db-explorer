package com.bednar.dbexplorer.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@Slf4j
@RestControllerAdvice
public class GlobalApiExceptionHandler {

    @ExceptionHandler(value = ApiErrorException.class)
    protected ResponseEntity<ApiError> handleError(ApiErrorException e) {

        return createResponse(e.getApiError());
    }

    @ExceptionHandler(value = {Exception.class, SQLException.class})
    protected ResponseEntity<ApiError> handleError(Exception e) {
        final ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorMessage("Unexpected error")
                .build();

        return createResponse(apiError);
    }

    private static ResponseEntity<ApiError> createResponse(final ApiError apiError) {
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }
}
