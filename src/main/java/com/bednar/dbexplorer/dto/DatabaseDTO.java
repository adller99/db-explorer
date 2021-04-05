package com.bednar.dbexplorer.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@RequiredArgsConstructor
public class DatabaseDTO {

    private final long id;
    private final String name;
    private final String hostname;
    private final int port;
    private final String databaseName;
    private final String username;
}
