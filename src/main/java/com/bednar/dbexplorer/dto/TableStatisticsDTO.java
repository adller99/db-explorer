package com.bednar.dbexplorer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TableStatisticsDTO {

    private final long recordsCount;
    private final long attributesCount;
}
