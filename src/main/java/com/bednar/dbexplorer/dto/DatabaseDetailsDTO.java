package com.bednar.dbexplorer.dto;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
@Builder
public class DatabaseDetailsDTO {

    private final String tableName;
    private final List<ColumnDTO> columns;
}
