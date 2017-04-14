package de.xn__ho_hia.yosql.benchmark.parse_file;

import java.io.PrintStream;

import de.xn__ho_hia.yosql.YoSql;
import de.xn__ho_hia.yosql.model.ExecutionConfiguration;
import de.xn__ho_hia.yosql.model.ExecutionErrors;
import de.xn__ho_hia.yosql.parser.Java8SqlFileParser;
import de.xn__ho_hia.yosql.parser.SqlConfigurationFactory;

/**
 * JMH based micro benchmark for the {@link Java8SqlFileParser} running against each .sql file individually.
 */
public class Java8SqlFileParserParseEachFileBenchmark extends AbstractParseEachFileBenchmark {

    @Override
    public void setUpParser() throws Exception {
        final ExecutionErrors errors = new ExecutionErrors();
        final ExecutionConfiguration configuration = YoSql.defaultConfiguration().build();
        final SqlConfigurationFactory configurationFactory = new SqlConfigurationFactory(errors, configuration);
        final PrintStream output = null;
        parser = new Java8SqlFileParser(errors, configuration, configurationFactory, output);
    }

}
