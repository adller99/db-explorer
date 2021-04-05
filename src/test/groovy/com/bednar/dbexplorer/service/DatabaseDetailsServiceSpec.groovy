package com.bednar.dbexplorer.service

import com.bednar.dbexplorer.IntegrationTest
import com.bednar.dbexplorer.domain.Database
import com.bednar.dbexplorer.repository.DatabaseRepository
import com.bednar.dbexplorer.util.TestcontainersUtil
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Unroll

import java.sql.Timestamp

@IntegrationTest
class DatabaseDetailsServiceSpec extends Specification {

    @Autowired
    DatabaseService databaseService

    @Autowired
    DatabaseDetailsService databaseDetailsService

    @Autowired
    DatabaseRepository databaseRepository

    @Autowired
    TestcontainersUtil testcontainersUtil

    def static dbCreated = false
    def static savedDatabase

    def "list schemas"() {
        given: "a database connection details exist are persisted"
        def database = saveDatabase()

        when: "the get schemas method is called"
        def result = databaseDetailsService.getSchemas(database.id)

        then: "the result contains correct schema"
        result.contains("integration_test")
    }

    def "list tables"() {
        given: "a database connection details exist are persisted"
        def database = saveDatabase()

        when: "the get tables method is called"
        List<String> result = databaseDetailsService.getTables(database.id, "integration_test")

        then: "it contains correct database tables"
        ["user", "user_details"].every { result.contains(it) }
    }

    def "get table data"() {
        given: "a database connection details exist are persisted"
        def database = saveDatabase()

        and: "expected result exists"
        def expectedResult = [id           : 1, username: "big carl", is_active: true,
                              registered_at: new Date(121, 2, 5),
                              modified_at  : new Timestamp(121, 2, 15, 16, 54, 0, 0)]

        when: "the get table details method is called"
        def result = databaseDetailsService.getTableData(database.id, "integration_test", "user")

        then: "the result contains correct data"
        expectedResult == result.get(0)
    }

    @Unroll
    def "list tables details"() {
        given: "a database connection details exist are persisted"
        def database = saveDatabase()

        when: "the get table details method is called"
        def result = databaseDetailsService.getTableDetails(database.id, "integration_test", tableName)

        then: "the table name is correct"
        result.tableName == tableName

        and: "the column definition is correct"
        def column = result.columns.find({ it.columnName == columnName })

        with(column) {
            columnDataType == dataType
            columnSize == columnSize
            nullable == isNullable
            primaryKey == isPrimaryKey
            primaryKeyName == primaryKeyName

            and: "foreign key details are correct"
            if (foreignKeyDetails == null) {
                foreignKey == null
            } else {
                with(foreignKey) {
                    referencedTableName == foreignKeyDetails[0]
                    referencedColumnName == foreignKeyDetails[1]
                    foreignKeyName == foreignKeyDetails[2]
                }
            }
        }

        where:
        tableName      | columnName      | dataType    | columnSize | isNullable | isPrimaryKey | primaryKeyName    | foreignKeyDetails
        "user"         | "id"            | "int4"      | 10         | false      | true         | "pk_user"         | null
        "user"         | "username"      | "varchar"   | 255        | true       | false        | null              | null
        "user"         | "is_active"     | "bool"      | 1          | false      | false        | null              | null
        "user"         | "registered_at" | "date"      | 13         | false      | false        | null              | null
        "user"         | "modified_at"   | "timestamp" | 13         | false      | false        | null              | null
        "user_details" | "user_id"       | "int4"      | 10         | false      | true         | "pk_user_details" | ["user", "id", "fk_user_details__user_id"]
        "user_details" | "phone_number"  | "varchar"   | 255        | false      | false        | null              | null
    }

    private Database saveDatabase() {
        if (!dbCreated) {
            def database = Database.builder()
                    .name("main database")
                    .hostname("localhost")
                    .port(testcontainersUtil.getConnectionPort())
                    .databaseName("test")
                    .username("postgres")
                    .password("postgres")
                    .build()

            dbCreated = true
            savedDatabase = databaseRepository.save(database)
        }

        return savedDatabase
    }

    def cleanup() {
        //primitive way of cleaning the DB
        databaseRepository.deleteAll()
    }
}
