package wtf.metio.yosql.generator.blocks.jdbc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static wtf.metio.yosql.generator.blocks.jdbc.JdbcObjectMother.jdbcNamesConfiguration;

@DisplayName("DefaultJdbcStatementMethods")
class DefaultJdbcStatementMethodsTest {

    private DefaultJdbcStatementMethods generator;

    @BeforeEach
    void setUp() {
        generator = new DefaultJdbcStatementMethods(jdbcNamesConfiguration());
    }

    @Test
    void executeQuery() {
        Assertions.assertEquals("""
                statement.executeQuery()""", generator.executeQuery().toString());
    }

    @Test
    void addBatch() {
        Assertions.assertEquals("""
                statement.addBatch()""", generator.addBatch().toString());
    }

    @Test
    void executeBatch() {
        Assertions.assertEquals("""
                statement.executeBatch()""", generator.executeBatch().toString());
    }

    @Test
    void executeUpdate() {
        Assertions.assertEquals("""
                statement.executeUpdate()""", generator.executeUpdate().toString());
    }

}