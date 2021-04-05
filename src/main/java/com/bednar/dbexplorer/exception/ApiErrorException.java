package com.bednar.dbexplorer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ApiErrorException extends RuntimeException {

    private final ApiError apiError;

    public ApiErrorException(final String message, final HttpStatus httpStatus) {
        this.apiError = ApiError.builder()
                .httpStatus(httpStatus)
                .errorMessage(message)
                .build();
    }

    public ApiErrorException(final String message, final HttpStatus httpStatus, final Exception e) {
        super(e);
        this.apiError = ApiError.builder()
                .httpStatus(httpStatus)
                .errorMessage(message)
                .build();
    }
}
