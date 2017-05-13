package de.xn__ho_hia.yosql.acceptance;

import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.Assertions;

import cucumber.api.java8.En;
import yosql.yep_1.PersonRepository;
import yosql.yep_1.SchemaRepository;
import yosql.yep_1.util.ResultRow;

/**
 * Defines steps for YEP-1.
 */
@SuppressWarnings("nls")
public final class Yep1Steps implements En {

    private PersonRepository repository;
    private List<ResultRow>  data;

    /**
     * Creates the steps for YEP-1.
     */
    public Yep1Steps() {
        Given("A repository exists", () -> {
            final DataSource dataSource = createDatSource(1);
            repository = new PersonRepository(dataSource);
            initSchema(dataSource);
        });
        When("the (\\w+) read method is called", (final String methodType) -> {
            switch (methodType) {
                case "stream lazy":
                    data = repository.readPersonStreamLazy().collect(Collectors.toList());
                    break;
                case "stream":
                    data = repository.readPersonStreamEager().collect(Collectors.toList());
                    break;
                case "rxjava":
                    data = repository.readPersonFlow().toList().blockingGet();
                    break;
                case "standard":
                default:
                    data = repository.readPerson();
            }
        });
        Then("data from a database should be returned", () -> {
            Assertions.assertFalse(data.isEmpty());
        });
    }

    private void initSchema(final DataSource dataSource) {
        final SchemaRepository schemaRepository = new SchemaRepository(dataSource);
        schemaRepository.dropTables();
        schemaRepository.createTables();
        repository.writePerson(1, "YEP-1");
    }

    private static final DataSource createDatSource(final int yep) {
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:h2:mem:YEP-" + yep + ";DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        return dataSource;
    }

}