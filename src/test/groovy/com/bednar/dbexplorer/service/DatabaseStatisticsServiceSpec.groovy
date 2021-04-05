package com.bednar.dbexplorer.service

import com.bednar.dbexplorer.IntegrationTest
import com.bednar.dbexplorer.domain.Database
import com.bednar.dbexplorer.repository.DatabaseRepository
import com.bednar.dbexplorer.util.TestcontainersUtil
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Unroll

@IntegrationTest
class DatabaseStatisticsServiceSpec extends Specification {

    @Autowired
    DatabaseService databaseService

    @Autowired
    DatabaseStatisticsService databaseStatisticsService

    @Autowired
    DatabaseRepository databaseRepository

    @Autowired
    TestcontainersUtil testcontainersUtil

    @Unroll
    def "get the column statistics"() {
        given: "a database connection details exist are persisted"
        def database = saveDatabase()

        when: "the get column statistics method is called"
        def result = databaseStatisticsService.getColumnStatistics(database.id, "integration_test", "login_statistics",
                "user_id", sqlOperation)

        then: "it contains the correct result"
        result == value

        where:
        sqlOperation                | value
        StatisticsOperation.MIN     | 3
        StatisticsOperation.MAX     | 15
        StatisticsOperation.AVERAGE | 9
        StatisticsOperation.MEDIAN  | 9.5
    }

    def "get the table statistics"() {
        given: "a database connection details exist are persisted"
        def database = saveDatabase()

        when: "the get table statistics method is called"
        def result = databaseStatisticsService.getTableStatistics(database.id, "integration_test", "login_statistics")

        then: "it contains the correct result"
        with(result) {
            attributesCount == 1
            recordsCount == 12
        }
    }

    private Database saveDatabase() {
        def database = Database.builder()
                .name("main database")
                .hostname("localhost")
                .port(testcontainersUtil.getConnectionPort())
                .databaseName("test")
                .username("postgres")
                .password("postgres")
                .build()

        return databaseRepository.save(database)
    }

    def cleanup() {
        //primitive way of cleaning the DB
        databaseRepository.deleteAll()
    }
}
