package com.bednar.dbexplorer.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString(exclude = "password")
@Data
@AllArgsConstructor
@Builder
public class DatabaseRequestDTO {

    private final String name;
    private final String hostname;
    private final int port;
    private final String databaseName;
    private final String username;
    private final String password;
}
