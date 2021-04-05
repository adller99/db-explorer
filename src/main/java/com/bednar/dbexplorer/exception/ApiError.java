package com.bednar.dbexplorer.exception;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@ApiModel
@Data
@Builder
public class ApiError {

    private final String errorMessage;
    private final HttpStatus httpStatus;
}

