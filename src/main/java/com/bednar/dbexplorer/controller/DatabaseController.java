package com.bednar.dbexplorer.controller;

import com.bednar.dbexplorer.cache.JdbcTemplateService;
import com.bednar.dbexplorer.domain.Database;
import com.bednar.dbexplorer.dto.DatabaseDTO;
import com.bednar.dbexplorer.dto.DatabaseRequestDTO;
import com.bednar.dbexplorer.service.DatabaseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/database")
public class DatabaseController {

    private final DatabaseService databaseService;
    private final JdbcTemplateService jdbcTemplateService;

    @ApiOperation(value = "Lists existing databases")
    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<DatabaseDTO> getDatabases() {
        log.debug("action=list_databases status=start");
        try {
            final List<DatabaseDTO> databaseDTOS = databaseService.getDatabases();

            log.debug("action=list_databases status=ok");
            return databaseDTOS;
        } catch (Exception e) {
            log.error("action=list_databases status=error", e);
            throw e;
        }
    }

    @ApiOperation(value = "Persists new database",
            notes = "Multiple status values can be provided with comma seperated strings")
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> saveDatabase(@RequestBody DatabaseRequestDTO requestDTO) {
        log.debug("action=store_database payload={} status=start", requestDTO.toString());
        try {
            final Database database = databaseService.saveDatabase(requestDTO);

            log.debug("action=store_database payload={} status=ok", requestDTO.toString());
            return ResponseEntity
                    .created(URI.create("/database/" + database.getId()))
                    .build();
        } catch (Exception e) {
            log.error("action=store_database payload={} status=error", requestDTO.toString(), e);
            throw e;
        }
    }

    @ApiOperation(value = "Updates an existing database",
            notes = "If no password is provided the original one will be not be overridden")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @ResponseBody
    @PutMapping("/{databaseId}")
    public void updateDatabase(@PathVariable("databaseId") final long databaseId,
                                 @RequestBody DatabaseRequestDTO requestDTO) {

        log.debug("action=update_database payload={} status=start", requestDTO.toString());
        try {
            databaseService.updateDatabase(databaseId, requestDTO);
            jdbcTemplateService.invalidate(databaseId);

            log.debug("action=update_database payload={} status=ok", requestDTO.toString());
        } catch (Exception e) {
            log.error("action=update_database payload={} status=error", requestDTO.toString(), e);
            throw e;
        }
    }
}
