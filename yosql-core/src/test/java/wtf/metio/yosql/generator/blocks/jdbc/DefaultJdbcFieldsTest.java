package wtf.metio.yosql.generator.blocks.jdbc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wtf.metio.yosql.model.sql.SqlConfiguration;

import static wtf.metio.yosql.generator.blocks.jdbc.JdbcObjectMother.jdbcFieldsConfiguration;

@DisplayName("DefaultJdbcFields")
class DefaultJdbcFieldsTest {

    private DefaultJdbcFields generator;

    @BeforeEach
    void setUp() {
        generator = new DefaultJdbcFields(jdbcFieldsConfiguration());
    }

    @Test
    void constantSqlStatementFieldName() {
        // given
        final var config = new SqlConfiguration();
        config.setName("test");

        // when
        final var constant = generator.constantSqlStatementFieldName(config);

        // then
        Assertions.assertEquals("""
                TEST""", constant);
    }

    @Test
    void constantSqlStatementFieldNameWithVendor() {
        // given
        final var config = new SqlConfiguration();
        config.setName("test");
        config.setVendor("MyDB");

        // when
        final var constant = generator.constantSqlStatementFieldName(config);

        // then
        Assertions.assertEquals("""
                TEST_MYDB""", constant);
    }

    @Test
    void constantRawSqlStatementFieldName() {
        // given
        final var config = new SqlConfiguration();
        config.setName("test");

        // when
        final var constant = generator.constantRawSqlStatementFieldName(config);

        // then
        Assertions.assertEquals("""
                TEST_RAW""", constant);
    }

    @Test
    void constantRawSqlStatementFieldNameWithVendor() {
        // given
        final var config = new SqlConfiguration();
        config.setName("test");
        config.setVendor("Delphi DB");

        // when
        final var constant = generator.constantRawSqlStatementFieldName(config);

        // then
        Assertions.assertEquals("""
                TEST_DELPHI_DB_RAW""", constant);
    }

    @Test
    void constantSqlStatementParameterIndexFieldName() {
        // given
        final var config = new SqlConfiguration();
        config.setName("test");

        // when
        final var constant = generator.constantSqlStatementParameterIndexFieldName(config);

        // then
        Assertions.assertEquals("""
                TEST_INDEX""", constant);
    }

    @Test
    void constantSqlStatementParameterIndexFieldNameWithVendor() {
        // given
        final var config = new SqlConfiguration();
        config.setName("testQueryStatement");
        config.setVendor("PregreSQL");

        // when
        final var constant = generator.constantSqlStatementParameterIndexFieldName(config);

        // then
        Assertions.assertEquals("""
                TEST_QUERY_STATEMENT_PREGRESQL_INDEX""", constant);
    }

}